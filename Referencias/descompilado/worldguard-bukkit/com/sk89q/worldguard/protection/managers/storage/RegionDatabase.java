/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldguard.protection.managers.storage;

import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import com.sk89q.worldguard.protection.managers.RegionDifference;
import com.sk89q.worldguard.protection.managers.storage.DifferenceSaveException;
import com.sk89q.worldguard.protection.managers.storage.StorageException;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import java.util.Set;

public interface RegionDatabase {
    public String getName();

    public Set<ProtectedRegion> loadAll(FlagRegistry var1) throws StorageException;

    public void saveAll(Set<ProtectedRegion> var1) throws StorageException;

    public void saveChanges(RegionDifference var1) throws DifferenceSaveException, StorageException;
}

