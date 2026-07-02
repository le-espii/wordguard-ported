/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Function
 *  com.google.common.base.Joiner
 *  com.google.common.base.Preconditions
 *  javax.annotation.Nullable
 */
package com.sk89q.worldguard.protection.util;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.util.UnresolvedNamesException;
import com.sk89q.worldguard.util.profile.Profile;
import com.sk89q.worldguard.util.profile.resolver.ProfileService;
import com.sk89q.worldguard.util.profile.util.UUIDs;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nullable;

public class DomainInputResolver
implements Callable<DefaultDomain> {
    private static final Pattern GROUP_PATTERN = Pattern.compile("(?i)^[G]:(.+)$");
    private final ProfileService profileService;
    private final String[] input;
    private UserLocatorPolicy locatorPolicy = UserLocatorPolicy.UUID_ONLY;

    public DomainInputResolver(ProfileService profileService, String[] input) {
        Preconditions.checkNotNull((Object)profileService);
        Preconditions.checkNotNull((Object)input);
        this.profileService = profileService;
        this.input = input;
    }

    public UserLocatorPolicy getLocatorPolicy() {
        return this.locatorPolicy;
    }

    public void setLocatorPolicy(UserLocatorPolicy locatorPolicy) {
        Preconditions.checkNotNull((Object)((Object)locatorPolicy));
        this.locatorPolicy = locatorPolicy;
    }

    @Override
    public DefaultDomain call() throws UnresolvedNamesException {
        DefaultDomain domain = new DefaultDomain();
        ArrayList<String> namesToQuery = new ArrayList<String>();
        block8: for (String s : this.input) {
            Matcher m = GROUP_PATTERN.matcher(s);
            if (m.matches()) {
                domain.addGroup(m.group(1));
                continue;
            }
            UUID uuid = DomainInputResolver.parseUUID(s);
            if (uuid != null) {
                domain.addPlayer(UUID.fromString(UUIDs.addDashes(s.replaceAll("^uuid:", ""))));
                continue;
            }
            switch (this.locatorPolicy.ordinal()) {
                case 1: {
                    domain.addPlayer(s);
                    continue block8;
                }
                case 0: {
                    namesToQuery.add(s.toLowerCase());
                    continue block8;
                }
                case 2: {
                    domain.addPlayer(s);
                    namesToQuery.add(s.toLowerCase());
                }
            }
        }
        if (!namesToQuery.isEmpty()) {
            try {
                for (Profile profile : this.profileService.findAllByName(namesToQuery)) {
                    namesToQuery.remove(profile.getName().toLowerCase());
                    domain.addPlayer(profile.getUniqueId());
                }
            }
            catch (IOException e) {
                throw new UnresolvedNamesException("The UUID lookup service failed so the names entered could not be turned into UUIDs");
            }
            catch (InterruptedException e) {
                throw new UnresolvedNamesException("UUID lookup was interrupted");
            }
        }
        if (!namesToQuery.isEmpty()) {
            throw new UnresolvedNamesException("Unable to resolve the names " + Joiner.on((String)", ").join(namesToQuery));
        }
        return domain;
    }

    @Deprecated
    public Function<DefaultDomain, DefaultDomain> createAddAllFunction(DefaultDomain target) {
        return domain -> {
            target.addAll((DefaultDomain)domain);
            return domain;
        };
    }

    @Deprecated
    public Function<DefaultDomain, DefaultDomain> createRemoveAllFunction(DefaultDomain target) {
        return domain -> {
            target.removeAll((DefaultDomain)domain);
            return domain;
        };
    }

    @Nullable
    public static UUID parseUUID(String input) {
        Preconditions.checkNotNull((Object)input);
        try {
            return UUID.fromString(UUIDs.addDashes(input.replaceAll("^uuid:", "")));
        }
        catch (IllegalArgumentException e) {
            return null;
        }
    }

    public static enum UserLocatorPolicy {
        UUID_ONLY,
        NAME_ONLY,
        UUID_AND_NAME;

    }
}

