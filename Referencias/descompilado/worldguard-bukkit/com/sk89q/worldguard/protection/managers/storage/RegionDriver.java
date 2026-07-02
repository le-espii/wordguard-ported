/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldguard.protection.managers.storage;

import com.sk89q.worldguard.protection.managers.storage.RegionDatabase;
import com.sk89q.worldguard.protection.managers.storage.StorageException;
import java.util.List;

public interface RegionDriver {
    public RegionDatabase get(String var1);

    public List<RegionDatabase> getAll() throws StorageException;
}

