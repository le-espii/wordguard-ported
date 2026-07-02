/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.sk89q.worldguard.util.profile.resolver;

import com.sk89q.worldguard.util.profile.Profile;
import com.sk89q.worldguard.util.profile.resolver.SingleRequestService;
import java.io.IOException;
import java.util.Collection;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.Nullable;

public class HashMapService
extends SingleRequestService {
    private final ConcurrentHashMap<String, UUID> nameToIdMap = new ConcurrentHashMap();
    private final ConcurrentHashMap<UUID, String> idToNameMap = new ConcurrentHashMap();

    public HashMapService() {
    }

    public HashMapService(Map<String, UUID> map) {
        for (Map.Entry<String, UUID> entry : map.entrySet()) {
            this.nameToIdMap.put(entry.getKey().toLowerCase(Locale.US), entry.getValue());
            this.idToNameMap.put(entry.getValue(), entry.getKey().toLowerCase(Locale.US));
        }
    }

    public void put(Profile profile) {
        this.nameToIdMap.put(profile.getName().toLowerCase(Locale.US), profile.getUniqueId());
        this.idToNameMap.put(profile.getUniqueId(), profile.getName().toLowerCase(Locale.US));
    }

    public void putAll(Collection<Profile> profiles) {
        for (Profile profile : profiles) {
            this.put(profile);
        }
    }

    @Override
    public int getIdealRequestLimit() {
        return Integer.MAX_VALUE;
    }

    @Override
    @Nullable
    public Profile findByName(String name) throws IOException, InterruptedException {
        UUID uuid = this.nameToIdMap.get(name.toLowerCase(Locale.US));
        if (uuid != null) {
            return new Profile(uuid, name);
        }
        return null;
    }

    @Override
    @Nullable
    public Profile findByUuid(UUID uuid) throws IOException, InterruptedException {
        String name = this.idToNameMap.get(uuid);
        if (name != null) {
            return new Profile(uuid, name);
        }
        return null;
    }
}

