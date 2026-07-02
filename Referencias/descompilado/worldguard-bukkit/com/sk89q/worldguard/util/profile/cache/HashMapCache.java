/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 */
package com.sk89q.worldguard.util.profile.cache;

import com.google.common.collect.ImmutableMap;
import com.sk89q.worldguard.util.profile.Profile;
import com.sk89q.worldguard.util.profile.cache.AbstractProfileCache;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class HashMapCache
extends AbstractProfileCache {
    private final ConcurrentMap<UUID, String> cache = new ConcurrentHashMap<UUID, String>();

    @Override
    public void putAll(Iterable<Profile> profiles) {
        for (Profile profile : profiles) {
            this.cache.put(profile.getUniqueId(), profile.getName());
        }
    }

    @Override
    public ImmutableMap<UUID, Profile> getAllPresent(Iterable<UUID> uuids) {
        HashMap<UUID, Profile> results = new HashMap<UUID, Profile>();
        for (UUID uuid : uuids) {
            String name = (String)this.cache.get(uuid);
            if (name == null) continue;
            results.put(uuid, new Profile(uuid, name));
        }
        return ImmutableMap.copyOf(results);
    }
}

