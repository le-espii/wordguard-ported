/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  javax.annotation.Nullable
 */
package com.sk89q.worldguard.util.profile.cache;

import com.google.common.collect.ImmutableList;
import com.sk89q.worldguard.util.profile.Profile;
import com.sk89q.worldguard.util.profile.cache.ProfileCache;
import java.util.UUID;
import javax.annotation.Nullable;

abstract class AbstractProfileCache
implements ProfileCache {
    AbstractProfileCache() {
    }

    @Override
    public void put(Profile profile) {
        this.putAll((Iterable<Profile>)ImmutableList.of((Object)profile));
    }

    @Override
    @Nullable
    public Profile getIfPresent(UUID uuid) {
        return (Profile)this.getAllPresent((Iterable<UUID>)ImmutableList.of((Object)uuid)).get((Object)uuid);
    }
}

