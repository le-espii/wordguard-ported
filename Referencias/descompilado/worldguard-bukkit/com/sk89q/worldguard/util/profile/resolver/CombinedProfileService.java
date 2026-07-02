/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ImmutableList
 *  javax.annotation.Nullable
 */
package com.sk89q.worldguard.util.profile.resolver;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.sk89q.worldguard.util.profile.Profile;
import com.sk89q.worldguard.util.profile.resolver.ProfileService;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.function.Predicate;
import javax.annotation.Nullable;

public class CombinedProfileService
implements ProfileService {
    private final List<ProfileService> services;

    public CombinedProfileService(List<ProfileService> services) {
        Preconditions.checkNotNull(services);
        this.services = ImmutableList.copyOf(services);
    }

    public CombinedProfileService(ProfileService ... services) {
        Preconditions.checkNotNull((Object)services);
        this.services = ImmutableList.copyOf((Object[])services);
    }

    @Override
    public int getIdealRequestLimit() {
        int ideal = Integer.MAX_VALUE;
        for (ProfileService service : this.services) {
            ideal = Math.min(service.getIdealRequestLimit(), ideal);
        }
        return ideal;
    }

    @Override
    @Nullable
    public Profile findByName(String name) throws IOException, InterruptedException {
        for (ProfileService service : this.services) {
            Profile profile = service.findByName(name);
            if (profile == null) continue;
            return profile;
        }
        return null;
    }

    @Override
    public ImmutableList<Profile> findAllByName(Iterable<String> names) throws IOException, InterruptedException {
        ArrayList<String> missing = new ArrayList<String>();
        ArrayList<Profile> totalResults = new ArrayList<Profile>();
        for (String name : names) {
            missing.add(name.toLowerCase(Locale.US));
        }
        for (ProfileService service : this.services) {
            ImmutableList<Profile> results = service.findAllByName(missing);
            for (Profile profile : results) {
                String nameLower = profile.getName().toLowerCase(Locale.US);
                missing.remove(nameLower);
                totalResults.add(profile);
            }
            if (!missing.isEmpty()) continue;
            break;
        }
        return ImmutableList.copyOf(totalResults);
    }

    @Override
    public void findAllByName(Iterable<String> names, Predicate<Profile> consumer) throws IOException, InterruptedException {
        List<String> missing = Collections.synchronizedList(new ArrayList());
        Predicate<Profile> forwardingConsumer = profile -> {
            missing.remove(profile.getName().toLowerCase(Locale.US));
            return consumer.test((Profile)profile);
        };
        for (String name : names) {
            missing.add(name.toLowerCase(Locale.US));
        }
        for (ProfileService service : this.services) {
            service.findAllByName(new ArrayList<String>(missing), forwardingConsumer);
            if (!missing.isEmpty()) continue;
            break;
        }
    }

    @Override
    @Nullable
    public Profile findByUuid(UUID uuid) throws IOException, InterruptedException {
        for (ProfileService service : this.services) {
            Profile profile = service.findByUuid(uuid);
            if (profile == null) continue;
            return profile;
        }
        return null;
    }

    @Override
    public ImmutableList<Profile> findAllByUuid(Iterable<UUID> uuids) throws IOException, InterruptedException {
        ArrayList<UUID> missing = new ArrayList<UUID>();
        ArrayList<Profile> totalResults = new ArrayList<Profile>();
        for (UUID uuid : uuids) {
            missing.add(uuid);
        }
        for (ProfileService service : this.services) {
            ImmutableList<Profile> results = service.findAllByUuid(missing);
            for (Profile profile : results) {
                UUID foundUuid = profile.getUniqueId();
                missing.remove(foundUuid);
                totalResults.add(profile);
            }
            if (!missing.isEmpty()) continue;
            break;
        }
        return ImmutableList.copyOf(totalResults);
    }

    @Override
    public void findAllByUuid(Iterable<UUID> uuids, Predicate<Profile> consumer) throws IOException, InterruptedException {
        List<UUID> missing = Collections.synchronizedList(new ArrayList());
        Predicate<Profile> forwardingConsumer = profile -> {
            missing.remove(profile.getUniqueId());
            return consumer.test((Profile)profile);
        };
        for (UUID uuid : uuids) {
            missing.add(uuid);
        }
        for (ProfileService service : this.services) {
            service.findAllByUuid(new ArrayList<UUID>(missing), forwardingConsumer);
            if (!missing.isEmpty()) continue;
            break;
        }
    }
}

