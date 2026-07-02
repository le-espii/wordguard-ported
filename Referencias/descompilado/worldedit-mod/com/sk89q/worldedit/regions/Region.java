/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.sk89q.worldedit.regions;

import com.sk89q.worldedit.internal.util.DeprecationUtil;
import com.sk89q.worldedit.internal.util.NonAbstractForCompatibility;
import com.sk89q.worldedit.math.BlockVector2;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.math.Vector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.RegionOperationException;
import com.sk89q.worldedit.world.World;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;

public interface Region
extends Iterable<BlockVector3>,
Cloneable {
    public BlockVector3 getMinimumPoint();

    public BlockVector3 getMaximumPoint();

    default public CuboidRegion getBoundingBox() {
        return new CuboidRegion(this.getMinimumPoint(), this.getMaximumPoint());
    }

    public Vector3 getCenter();

    @Deprecated
    default public int getArea() {
        return (int)this.getVolume();
    }

    @NonAbstractForCompatibility(delegateName="getArea", delegateParams={})
    default public long getVolume() {
        DeprecationUtil.checkDelegatingOverride(this.getClass());
        return this.getArea();
    }

    public int getWidth();

    public int getHeight();

    public int getLength();

    public void expand(BlockVector3 ... var1) throws RegionOperationException;

    public void contract(BlockVector3 ... var1) throws RegionOperationException;

    public void shift(BlockVector3 var1) throws RegionOperationException;

    public boolean contains(BlockVector3 var1);

    public Set<BlockVector2> getChunks();

    public Set<BlockVector3> getChunkCubes();

    @Nullable
    public World getWorld();

    public void setWorld(@Nullable World var1);

    public Region clone();

    public List<BlockVector2> polygonize(int var1);
}

