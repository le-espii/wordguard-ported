/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  javax.annotation.Nullable
 */
package com.sk89q.worldguard.util.profile.cache;

import com.google.common.collect.ImmutableMap;
import com.sk89q.worldguard.util.profile.Profile;
import java.util.UUID;
import javax.annotation.Nullable;

public interface ProfileCache {
    public void put(Profile var1);

    public void putAll(Iterable<Profile> var1);

    @Nullable
    public Profile getIfPresent(UUID var1);

    public ImmutableMap<UUID, Profile> getAllPresent(Iterable<UUID> var1);
}

