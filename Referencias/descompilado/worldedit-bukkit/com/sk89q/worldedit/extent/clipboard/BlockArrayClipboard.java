/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  javax.annotation.Nullable
 */
package com.sk89q.worldedit.extent.clipboard;

import com.google.common.base.Preconditions;
import com.sk89q.worldedit.entity.BaseEntity;
import com.sk89q.worldedit.entity.Entity;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.StoredEntity;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;

public class BlockArrayClipboard
implements Clipboard {
    private final Region region;
    private BlockVector3 origin;
    private final BaseBlock[][][] blocks;
    private BiomeType[][][] biomes = null;
    private final List<ClipboardEntity> entities = new ArrayList<ClipboardEntity>();

    public BlockArrayClipboard(Region region) {
        Preconditions.checkNotNull((Object)region);
        this.region = region.clone();
        this.origin = region.getMinimumPoint();
        BlockVector3 dimensions = this.getDimensions();
        this.blocks = new BaseBlock[dimensions.x()][dimensions.y()][dimensions.z()];
    }

    @Override
    public Region getRegion() {
        return this.region.clone();
    }

    @Override
    public BlockVector3 getOrigin() {
        return this.origin;
    }

    @Override
    public void setOrigin(BlockVector3 origin) {
        this.origin = origin;
    }

    @Override
    public BlockVector3 getDimensions() {
        return this.region.getMaximumPoint().subtract(this.region.getMinimumPoint()).add(1, 1, 1);
    }

    @Override
    public BlockVector3 getMinimumPoint() {
        return this.region.getMinimumPoint();
    }

    @Override
    public BlockVector3 getMaximumPoint() {
        return this.region.getMaximumPoint();
    }

    @Override
    public List<? extends Entity> getEntities(Region region) {
        ArrayList<Entity> filtered = new ArrayList<Entity>();
        for (Entity entity : this.entities) {
            if (!region.contains(entity.getLocation().toVector().toBlockPoint())) continue;
            filtered.add(entity);
        }
        return Collections.unmodifiableList(filtered);
    }

    @Override
    public List<? extends Entity> getEntities() {
        return Collections.unmodifiableList(this.entities);
    }

    @Override
    @Nullable
    public Entity createEntity(Location location, BaseEntity entity) {
        ClipboardEntity ret = new ClipboardEntity(location, entity);
        this.entities.add(ret);
        return ret;
    }

    @Override
    public BlockState getBlock(BlockVector3 position) {
        BlockVector3 v;
        BaseBlock block;
        if (this.region.contains(position) && (block = this.blocks[(v = position.subtract(this.region.getMinimumPoint())).x()][v.y()][v.z()]) != null) {
            return block.toImmutableState();
        }
        return BlockTypes.AIR.getDefaultState();
    }

    @Override
    public BaseBlock getFullBlock(BlockVector3 position) {
        BlockVector3 v;
        BaseBlock block;
        if (this.region.contains(position) && (block = this.blocks[(v = position.subtract(this.region.getMinimumPoint())).x()][v.y()][v.z()]) != null) {
            return block;
        }
        return BlockTypes.AIR.getDefaultState().toBaseBlock();
    }

    public <B extends BlockStateHolder<B>> boolean setBlock(BlockVector3 position, B block) {
        if (this.region.contains(position)) {
            BlockVector3 v = position.subtract(this.region.getMinimumPoint());
            this.blocks[v.x()][v.y()][v.z()] = block.toBaseBlock();
            return true;
        }
        return false;
    }

    @Override
    public boolean hasBiomes() {
        return this.biomes != null;
    }

    @Override
    public BiomeType getBiome(BlockVector3 position) {
        BlockVector3 v;
        BiomeType biomeType;
        if (this.biomes != null && position.containedWithin(this.getMinimumPoint(), this.getMaximumPoint()) && (biomeType = this.biomes[(v = position.subtract(this.region.getMinimumPoint())).x()][v.y()][v.z()]) != null) {
            return biomeType;
        }
        return BiomeTypes.OCEAN;
    }

    @Override
    public boolean setBiome(BlockVector3 position, BiomeType biome) {
        if (position.containedWithin(this.getMinimumPoint(), this.getMaximumPoint())) {
            BlockVector3 v = position.subtract(this.region.getMinimumPoint());
            if (this.biomes == null) {
                this.biomes = new BiomeType[this.region.getWidth()][this.region.getHeight()][this.region.getLength()];
            }
            this.biomes[v.x()][v.y()][v.z()] = biome;
            return true;
        }
        return false;
    }

    @Override
    @Nullable
    public Operation commit() {
        return null;
    }

    private class ClipboardEntity
    extends StoredEntity {
        ClipboardEntity(Location location, BaseEntity entity) {
            super(location, entity);
        }

        @Override
        public boolean remove() {
            return BlockArrayClipboard.this.entities.remove(this);
        }

        @Override
        @Nullable
        public <T> T getFacet(Class<? extends T> cls) {
            return null;
        }
    }
}

