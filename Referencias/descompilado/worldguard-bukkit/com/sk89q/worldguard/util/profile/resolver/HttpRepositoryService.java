/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 *  com.google.common.collect.Iterables
 *  javax.annotation.Nullable
 */
package com.sk89q.worldguard.util.profile.resolver;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.sk89q.worldguard.util.profile.Profile;
import com.sk89q.worldguard.util.profile.resolver.ProfileService;
import com.sk89q.worldguard.util.profile.util.HttpRequest;
import com.sk89q.worldguard.util.profile.util.UUIDs;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Nullable;

public class HttpRepositoryService
implements ProfileService {
    public static final String MINECRAFT_AGENT = "minecraft";
    private static final Logger log = Logger.getLogger(HttpRepositoryService.class.getCanonicalName());
    private static final int MAX_NAMES_PER_REQUEST = 100;
    private final URL profilesURL;
    private final Function<UUID, URL> nameHistoryUrlCreator;
    private int maxRetries = 5;
    private long retryDelay = 50L;

    public HttpRepositoryService(String agent) {
        Preconditions.checkNotNull((Object)agent);
        this.profilesURL = HttpRequest.url("https://api.mojang.com/profiles/" + agent);
        this.nameHistoryUrlCreator = uuid -> HttpRequest.url("https://sessionserver.mojang.com/session/minecraft/profile/" + UUIDs.stripDashes(uuid.toString()));
    }

    @Nullable
    private static Profile decodeProfileResult(Object entry) {
        try {
            if (entry instanceof Map) {
                Map mapEntry = (Map)entry;
                Object rawUuid = mapEntry.get("id");
                Object rawName = mapEntry.get("name");
                if (rawUuid != null && rawName != null) {
                    UUID uuid = UUID.fromString(UUIDs.addDashes(String.valueOf(rawUuid)));
                    String name = String.valueOf(rawName);
                    return new Profile(uuid, name);
                }
            }
        }
        catch (ClassCastException | IllegalArgumentException e) {
            log.log(Level.WARNING, "Got invalid value from UUID lookup service", e);
        }
        return null;
    }

    public static ProfileService forMinecraft() {
        return new HttpRepositoryService(MINECRAFT_AGENT);
    }

    public int getMaxRetries() {
        return this.maxRetries;
    }

    public void setMaxRetries(int maxRetries) {
        Preconditions.checkArgument((maxRetries > 0 ? 1 : 0) != 0, (Object)"maxRetries must be >= 0");
        this.maxRetries = maxRetries;
    }

    public long getRetryDelay() {
        return this.retryDelay;
    }

    public void setRetryDelay(long retryDelay) {
        this.retryDelay = retryDelay;
    }

    @Override
    public int getIdealRequestLimit() {
        return 100;
    }

    @Override
    @Nullable
    public Profile findByName(String name) throws IOException, InterruptedException {
        ImmutableList<Profile> profiles = this.findAllByName((Iterable<String>)ImmutableList.of((Object)name));
        if (!profiles.isEmpty()) {
            return (Profile)profiles.get(0);
        }
        return null;
    }

    @Override
    public ImmutableList<Profile> findAllByName(Iterable<String> names) throws IOException, InterruptedException {
        ImmutableList.Builder builder = ImmutableList.builder();
        for (List partition : Iterables.partition(names, (int)100)) {
            builder.addAll(this.queryByName(partition));
        }
        return builder.build();
    }

    @Override
    public void findAllByName(Iterable<String> names, Predicate<Profile> consumer) throws IOException, InterruptedException {
        for (List partition : Iterables.partition(names, (int)100)) {
            for (Profile profile : this.queryByName(partition)) {
                consumer.test(profile);
            }
        }
    }

    @Override
    @Nullable
    public Profile findByUuid(UUID uuid) throws IOException, InterruptedException {
        ImmutableList<Profile> profiles = this.findAllByUuid((Iterable<UUID>)ImmutableList.of((Object)uuid));
        if (!profiles.isEmpty()) {
            return (Profile)profiles.get(0);
        }
        return null;
    }

    @Override
    public ImmutableList<Profile> findAllByUuid(Iterable<UUID> uuids) throws IOException, InterruptedException {
        return ImmutableList.copyOf(this.queryByUuid(uuids));
    }

    @Override
    public void findAllByUuid(Iterable<UUID> uuids, Predicate<Profile> consumer) throws IOException, InterruptedException {
        for (Profile profile : this.queryByUuid(uuids)) {
            consumer.test(profile);
        }
    }

    protected ImmutableList<Profile> queryByName(Iterable<String> names) throws IOException, InterruptedException {
        Object result;
        ArrayList<Profile> profiles = new ArrayList<Profile>();
        int retriesLeft = this.maxRetries;
        long retryDelay = this.retryDelay;
        while (true) {
            try {
                result = HttpRequest.post(this.profilesURL).bodyJson(names).execute().returnContent().asJson();
            }
            catch (IOException e) {
                if (retriesLeft == 0) {
                    throw e;
                }
                log.log(Level.WARNING, "Failed to query profile service -- retrying...", e);
                Thread.sleep(retryDelay);
                retryDelay *= 2L;
                --retriesLeft;
                continue;
            }
            break;
        }
        if (result instanceof Iterable) {
            for (Object entry : (Iterable)result) {
                Profile profile = HttpRepositoryService.decodeProfileResult(entry);
                if (profile == null) continue;
                profiles.add(profile);
            }
        }
        return ImmutableList.copyOf(profiles);
    }

    protected ImmutableList<Profile> queryByUuid(Iterable<UUID> uuids) throws IOException, InterruptedException {
        ArrayList<Profile> profiles = new ArrayList<Profile>();
        int retriesLeft = this.maxRetries;
        long retryDelay = this.retryDelay;
        block2: for (UUID uuid : uuids) {
            while (true) {
                try {
                    Map map;
                    Object rawName;
                    Object result = HttpRequest.get(this.nameHistoryUrlCreator.apply(uuid)).execute().returnContent().asJson();
                    if (!(result instanceof Map) || (rawName = (map = (Map)result).get("name")) == null) continue block2;
                    profiles.add(new Profile(uuid, String.valueOf(rawName)));
                    continue block2;
                }
                catch (IOException e) {
                    if (retriesLeft == 0) {
                        throw e;
                    }
                    log.log(Level.WARNING, "Failed to query name history service -- retrying...", e);
                    Thread.sleep(retryDelay);
                    retryDelay *= 2L;
                    --retriesLeft;
                    continue;
                }
                break;
            }
        }
        return ImmutableList.copyOf(profiles);
    }
}

