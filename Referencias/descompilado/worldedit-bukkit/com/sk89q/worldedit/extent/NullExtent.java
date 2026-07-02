/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.sk89q.worldedit.extent;

import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.entity.BaseEntity;
import com.sk89q.worldedit.entity.Entity;
import com.sk89q.worldedit.extent.Extent;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.util.Location;
import com.sk89q.worldedit.world.biome.BiomeType;
import com.sk89q.worldedit.world.biome.BiomeTypes;
import com.sk89q.worldedit.world.block.BaseBlock;
import com.sk89q.worldedit.world.block.BlockState;
import com.sk89q.worldedit.world.block.BlockStateHolder;
import com.sk89q.worldedit.world.block.BlockTypes;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;

public class NullExtent
implements Extent {
    @Override
    public BlockVector3 getMinimumPoint() {
        return BlockVector3.ZERO;
    }

    @Override
    public BlockVector3 getMaximumPoint() {
        return BlockVector3.ZERO;
    }

    public List<Entity> getEntities(Region region) {
        return Collections.emptyList();
    }

    public List<Entity> getEntities() {
        return Collections.emptyList();
    }

    @Override
    @Nullable
    public Entity createEntity(Location location, BaseEntity entity) {
        return null;
    }

    @Override
    public BlockState getBlock(BlockVector3 position) {
        return BlockTypes.AIR.getDefaultState();
    }

    @Override
    public BaseBlock getFullBlock(BlockVector3 position) {
        return this.getBlock(position).toBaseBlock();
    }

    @Override
    public BiomeType getBiome(BlockVector3 position) {
        return BiomeTypes.THE_VOID;
    }

    public <B extends BlockStateHolder<B>> boolean setBlock(BlockVector3 position, B block) throws WorldEditException {
        return false;
    }

    @Override
    public boolean fullySupports3DBiomes() {
        return false;
    }

    @Override
    public boolean setBiome(BlockVector3 position, BiomeType biome) {
        return false;
    }

    @Override
    @Nullable
    public Operation commit() {
        return null;
    }
}

