/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 */
package com.sk89q.worldguard.commands.task;

import com.google.common.base.Preconditions;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.managers.storage.StorageException;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.Callable;

public class RegionManagerLoader
implements Callable<Collection<RegionManager>> {
    private final Collection<RegionManager> managers;

    public RegionManagerLoader(Collection<RegionManager> managers) {
        Preconditions.checkNotNull(managers);
        this.managers = managers;
    }

    public RegionManagerLoader(RegionManager ... manager) {
        this(Arrays.asList(manager));
    }

    @Override
    public Collection<RegionManager> call() throws StorageException {
        for (RegionManager manager : this.managers) {
            manager.load();
        }
        return this.managers;
    }
}

