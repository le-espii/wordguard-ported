/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sk89q.worldedit.math.BlockVector2
 *  com.sk89q.worldedit.math.BlockVector3
 *  javax.annotation.Nullable
 */
package com.sk89q.worldguard.protection.managers.index;

import com.sk89q.worldedit.math.BlockVector2;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.protection.managers.RegionDifference;
import com.sk89q.worldguard.protection.managers.RemovalStrategy;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.util.ChangeTracked;
import java.util.Collection;
import java.util.Set;
import java.util.function.Predicate;
import javax.annotation.Nullable;

public interface RegionIndex
extends ChangeTracked {
    public void bias(BlockVector2 var1);

    public void biasAll(Collection<BlockVector2> var1);

    public void forget(BlockVector2 var1);

    public void forgetAll();

    public void add(ProtectedRegion var1);

    public void addAll(Collection<ProtectedRegion> var1);

    public Set<ProtectedRegion> remove(String var1, RemovalStrategy var2);

    public boolean contains(String var1);

    @Nullable
    public ProtectedRegion get(String var1);

    public void apply(Predicate<ProtectedRegion> var1);

    public void applyContaining(BlockVector3 var1, Predicate<ProtectedRegion> var2);

    public void applyIntersecting(ProtectedRegion var1, Predicate<ProtectedRegion> var2);

    public int size();

    public RegionDifference getAndClearDifference();

    public void setDirty(RegionDifference var1);

    public Collection<ProtectedRegion> values();
}

