/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 */
package com.sk89q.worldguard.util.profile.resolver;

import com.google.common.collect.ImmutableList;
import com.sk89q.worldguard.util.profile.Profile;
import com.sk89q.worldguard.util.profile.resolver.ProfileService;
import java.io.IOException;
import java.util.UUID;
import java.util.function.Predicate;

abstract class SingleRequestService
implements ProfileService {
    SingleRequestService() {
    }

    @Override
    public final ImmutableList<Profile> findAllByName(Iterable<String> names) throws IOException, InterruptedException {
        ImmutableList.Builder builder = ImmutableList.builder();
        for (String name : names) {
            Profile profile = this.findByName(name);
            if (profile == null) continue;
            builder.add((Object)profile);
        }
        return builder.build();
    }

    @Override
    public final void findAllByName(Iterable<String> names, Predicate<Profile> consumer) throws IOException, InterruptedException {
        for (String name : names) {
            Profile profile = this.findByName(name);
            if (profile == null) continue;
            consumer.test(profile);
        }
    }

    @Override
    public ImmutableList<Profile> findAllByUuid(Iterable<UUID> uuids) throws IOException, InterruptedException {
        ImmutableList.Builder builder = ImmutableList.builder();
        for (UUID uuid : uuids) {
            Profile profile = this.findByUuid(uuid);
            if (profile == null) continue;
            builder.add((Object)profile);
        }
        return builder.build();
    }

    @Override
    public final void findAllByUuid(Iterable<UUID> uuids, Predicate<Profile> consumer) throws IOException, InterruptedException {
        for (UUID uuid : uuids) {
            Profile profile = this.findByUuid(uuid);
            if (profile == null) continue;
            consumer.test(profile);
        }
    }
}

