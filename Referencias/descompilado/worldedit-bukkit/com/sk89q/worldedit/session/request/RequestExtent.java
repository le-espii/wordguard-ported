/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.sk89q.worldedit.session.request;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.entity.BaseEntity;
import com.sk89q.worldedit.entity.Entity;
import com.sk89q.worldedit.extent.Extent;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.session.request.Request;
import com.sk89q.worldedit.util.Location;
import com.sk89q.worldedit.world.biome.BiomeType;
import com.sk89q.worldedit.world.block.BaseBlock;
import com.sk89q.worldedit.world.block.BlockState;
import com.sk89q.worldedit.world.block.BlockStateHolder;
import java.util.List;
import javax.annotation.Nullable;

public class RequestExtent
implements Extent {
    private Request request;

    protected Extent getExtent() {
        EditSession editSession;
        if (this.request == null || !this.request.isValid()) {
            this.request = Request.request();
        }
        return (editSession = this.request.getEditSession()) == null ? this.request.getWorld() : editSession;
    }

    @Override
    public BlockVector3 getMinimumPoint() {
        return this.getExtent().getMinimumPoint();
    }

    @Override
    public BlockVector3 getMaximumPoint() {
        return this.getExtent().getMaximumPoint();
    }

    @Override
    public List<? extends Entity> getEntities(Region region) {
        return this.getExtent().getEntities(region);
    }

    @Override
    public List<? extends Entity> getEntities() {
        return this.getExtent().getEntities();
    }

    @Override
    @Nullable
    public Entity createEntity(Location location, BaseEntity entity) {
        return this.getExtent().createEntity(location, entity);
    }

    @Override
    public BlockState getBlock(BlockVector3 position) {
        return this.getExtent().getBlock(position);
    }

    @Override
    public BaseBlock getFullBlock(BlockVector3 position) {
        return this.getExtent().getFullBlock(position);
    }

    @Override
    public BiomeType getBiome(BlockVector3 position) {
        return this.getExtent().getBiome(position);
    }

    @Override
    public <T extends BlockStateHolder<T>> boolean setBlock(BlockVector3 position, T block) throws WorldEditException {
        return this.getExtent().setBlock(position, block);
    }

    @Override
    public boolean fullySupports3DBiomes() {
        return this.getExtent().fullySupports3DBiomes();
    }

    @Override
    public boolean setBiome(BlockVector3 position, BiomeType biome) {
        return this.getExtent().setBiome(position, biome);
    }

    @Override
    @Nullable
    public Operation commit() {
        Operation commit = this.getExtent().commit();
        this.request = null;
        return commit;
    }
}

