/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  javax.annotation.Nullable
 */
package com.sk89q.worldedit.extent;

import com.google.common.base.Preconditions;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.entity.BaseEntity;
import com.sk89q.worldedit.entity.Entity;
import com.sk89q.worldedit.extent.Extent;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.OperationQueue;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.util.Location;
import com.sk89q.worldedit.world.biome.BiomeType;
import com.sk89q.worldedit.world.block.BaseBlock;
import com.sk89q.worldedit.world.block.BlockState;
import com.sk89q.worldedit.world.block.BlockStateHolder;
import java.util.List;
import javax.annotation.Nullable;

public abstract class AbstractDelegateExtent
implements Extent {
    private final Extent extent;

    protected AbstractDelegateExtent(Extent extent) {
        Preconditions.checkNotNull((Object)extent);
        this.extent = extent;
    }

    public Extent getExtent() {
        return this.extent;
    }

    @Override
    public BlockState getBlock(BlockVector3 position) {
        return this.extent.getBlock(position);
    }

    @Override
    public BaseBlock getFullBlock(BlockVector3 position) {
        return this.extent.getFullBlock(position);
    }

    @Override
    public <T extends BlockStateHolder<T>> boolean setBlock(BlockVector3 location, T block) throws WorldEditException {
        return this.extent.setBlock(location, block);
    }

    @Override
    @Nullable
    public Entity createEntity(Location location, BaseEntity entity) {
        return this.extent.createEntity(location, entity);
    }

    @Override
    public List<? extends Entity> getEntities() {
        return this.extent.getEntities();
    }

    @Override
    public List<? extends Entity> getEntities(Region region) {
        return this.extent.getEntities(region);
    }

    @Override
    public boolean fullySupports3DBiomes() {
        return this.extent.fullySupports3DBiomes();
    }

    @Override
    public BiomeType getBiome(BlockVector3 position) {
        return this.extent.getBiome(position);
    }

    @Override
    public boolean setBiome(BlockVector3 position, BiomeType biome) {
        return this.extent.setBiome(position, biome);
    }

    @Override
    public BlockVector3 getMinimumPoint() {
        return this.extent.getMinimumPoint();
    }

    @Override
    public BlockVector3 getMaximumPoint() {
        return this.extent.getMaximumPoint();
    }

    protected Operation commitBefore() {
        return null;
    }

    @Override
    @Nullable
    public final Operation commit() {
        Operation ours = this.commitBefore();
        Operation other = this.extent.commit();
        if (ours != null && other != null) {
            return new OperationQueue(ours, other);
        }
        if (ours != null) {
            return ours;
        }
        if (other != null) {
            return other;
        }
        return null;
    }
}

