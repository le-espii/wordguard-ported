/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldguard.protection.managers.storage;

import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import com.sk89q.worldguard.protection.managers.RegionDifference;
import com.sk89q.worldguard.protection.managers.storage.DifferenceSaveException;
import com.sk89q.worldguard.protection.managers.storage.RegionDatabase;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class MemoryRegionDatabase
implements RegionDatabase {
    private Set<ProtectedRegion> regions = Collections.emptySet();

    @Override
    public String getName() {
        return "MEMORY";
    }

    @Override
    public Set<ProtectedRegion> loadAll(FlagRegistry flagRegistry) {
        return this.regions;
    }

    @Override
    public void saveAll(Set<ProtectedRegion> regions) {
        this.regions = Collections.unmodifiableSet(new HashSet<ProtectedRegion>(regions));
    }

    @Override
    public void saveChanges(RegionDifference difference) throws DifferenceSaveException {
        throw new DifferenceSaveException();
    }
}

