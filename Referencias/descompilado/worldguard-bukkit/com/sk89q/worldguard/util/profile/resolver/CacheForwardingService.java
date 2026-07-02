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
import com.sk89q.worldguard.util.profile.cache.ProfileCache;
import com.sk89q.worldguard.util.profile.resolver.ProfileService;
import java.io.IOException;
import java.util.UUID;
import java.util.function.Predicate;
import javax.annotation.Nullable;

public class CacheForwardingService
implements ProfileService {
    private final ProfileService resolver;
    private final ProfileCache cache;

    public CacheForwardingService(ProfileService resolver, ProfileCache cache) {
        Preconditions.checkNotNull((Object)resolver);
        Preconditions.checkNotNull((Object)cache);
        this.resolver = resolver;
        this.cache = cache;
    }

    @Override
    public int getIdealRequestLimit() {
        return this.resolver.getIdealRequestLimit();
    }

    @Override
    @Nullable
    public Profile findByName(String name) throws IOException, InterruptedException {
        Profile profile = this.resolver.findByName(name);
        if (profile != null) {
            this.cache.put(profile);
        }
        return profile;
    }

    @Override
    public ImmutableList<Profile> findAllByName(Iterable<String> names) throws IOException, InterruptedException {
        ImmutableList<Profile> profiles = this.resolver.findAllByName(names);
        for (Profile profile : profiles) {
            this.cache.put(profile);
        }
        return profiles;
    }

    @Override
    public void findAllByName(Iterable<String> names, Predicate<Profile> consumer) throws IOException, InterruptedException {
        this.resolver.findAllByName(names, input -> {
            this.cache.put((Profile)input);
            return consumer.test((Profile)input);
        });
    }

    @Override
    @Nullable
    public Profile findByUuid(UUID uuid) throws IOException, InterruptedException {
        Profile profile = this.resolver.findByUuid(uuid);
        if (profile != null) {
            this.cache.put(profile);
        }
        return profile;
    }

    @Override
    public ImmutableList<Profile> findAllByUuid(Iterable<UUID> uuids) throws IOException, InterruptedException {
        ImmutableList<Profile> profiles = this.resolver.findAllByUuid(uuids);
        for (Profile profile : profiles) {
            this.cache.put(profile);
        }
        return profiles;
    }

    @Override
    public void findAllByUuid(Iterable<UUID> uuids, Predicate<Profile> consumer) throws IOException, InterruptedException {
        this.resolver.findAllByUuid(uuids, input -> {
            this.cache.put((Profile)input);
            return consumer.test((Profile)input);
        });
    }
}

