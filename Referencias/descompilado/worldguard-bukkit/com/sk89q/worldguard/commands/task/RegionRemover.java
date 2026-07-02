/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.sk89q.minecraft.util.commands.CommandException
 *  javax.annotation.Nullable
 */
package com.sk89q.worldguard.commands.task;

import com.google.common.base.Preconditions;
import com.sk89q.minecraft.util.commands.CommandException;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.managers.RemovalStrategy;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import java.util.Set;
import java.util.concurrent.Callable;
import javax.annotation.Nullable;

public class RegionRemover
implements Callable<Set<ProtectedRegion>> {
    private final RegionManager manager;
    private final ProtectedRegion region;
    @Nullable
    private RemovalStrategy removalStrategy;

    public RegionRemover(RegionManager manager, ProtectedRegion region) {
        Preconditions.checkNotNull((Object)manager);
        Preconditions.checkNotNull((Object)region);
        this.manager = manager;
        this.region = region;
    }

    @Nullable
    public RemovalStrategy getRemovalStrategy() {
        return this.removalStrategy;
    }

    public void setRemovalStrategy(@Nullable RemovalStrategy removalStrategy) {
        this.removalStrategy = removalStrategy;
    }

    @Override
    public Set<ProtectedRegion> call() throws Exception {
        if (this.removalStrategy == null) {
            for (ProtectedRegion test : this.manager.getRegions().values()) {
                ProtectedRegion parent = test.getParent();
                if (parent == null || !parent.equals(this.region)) continue;
                throw new CommandException("The region '" + this.region.getId() + "' has child regions. Use -f to force removal of children or -u to unset the parent value of these children.");
            }
            return this.manager.removeRegion(this.region.getId(), RemovalStrategy.UNSET_PARENT_IN_CHILDREN);
        }
        return this.manager.removeRegion(this.region.getId(), this.removalStrategy);
    }
}

