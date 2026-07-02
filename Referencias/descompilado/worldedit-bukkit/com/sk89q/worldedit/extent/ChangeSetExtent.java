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
import com.sk89q.worldedit.extent.AbstractDelegateExtent;
import com.sk89q.worldedit.extent.Extent;
import com.sk89q.worldedit.history.change.BiomeChange3D;
import com.sk89q.worldedit.history.change.BlockChange;
import com.sk89q.worldedit.history.change.EntityCreate;
import com.sk89q.worldedit.history.change.EntityRemove;
import com.sk89q.worldedit.history.changeset.ChangeSet;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.util.Location;
import com.sk89q.worldedit.world.biome.BiomeType;
import com.sk89q.worldedit.world.block.BaseBlock;
import com.sk89q.worldedit.world.block.BlockStateHolder;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;

public class ChangeSetExtent
extends AbstractDelegateExtent {
    private final ChangeSet changeSet;
    private boolean enabled;

    public ChangeSetExtent(Extent extent, ChangeSet changeSet) {
        this(extent, changeSet, true);
    }

    public ChangeSetExtent(Extent extent, ChangeSet changeSet, boolean enabled) {
        super(extent);
        Preconditions.checkNotNull((Object)changeSet);
        this.changeSet = changeSet;
        this.enabled = true;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        this.changeSet.setRecordChanges(enabled);
    }

    public <B extends BlockStateHolder<B>> boolean setBlock(BlockVector3 location, B block) throws WorldEditException {
        if (this.enabled) {
            BaseBlock previous = this.getFullBlock(location);
            this.changeSet.add(new BlockChange(location, previous, block));
        }
        return super.setBlock(location, block);
    }

    @Override
    public boolean setBiome(BlockVector3 position, BiomeType biome) {
        if (this.enabled) {
            BiomeType previous = this.getBiome(position);
            this.changeSet.add(new BiomeChange3D(position, previous, biome));
        }
        return super.setBiome(position, biome);
    }

    @Override
    @Nullable
    public Entity createEntity(Location location, BaseEntity state) {
        Entity entity = super.createEntity(location, state);
        if (this.enabled && entity != null) {
            this.changeSet.add(new EntityCreate(location, state, entity));
        }
        return entity;
    }

    @Override
    public List<? extends Entity> getEntities() {
        return this.wrapEntities(super.getEntities());
    }

    @Override
    public List<? extends Entity> getEntities(Region region) {
        return this.wrapEntities(super.getEntities(region));
    }

    private List<? extends Entity> wrapEntities(List<? extends Entity> entities) {
        if (!this.enabled) {
            return entities;
        }
        ArrayList<TrackedEntity> newList = new ArrayList<TrackedEntity>(entities.size());
        for (Entity entity : entities) {
            newList.add(new TrackedEntity(entity));
        }
        return newList;
    }

    private class TrackedEntity
    implements Entity {
        private final Entity entity;

        private TrackedEntity(Entity entity) {
            this.entity = entity;
        }

        @Override
        public BaseEntity getState() {
            return this.entity.getState();
        }

        @Override
        public Location getLocation() {
            return this.entity.getLocation();
        }

        @Override
        public boolean setLocation(Location location) {
            return this.entity.setLocation(location);
        }

        @Override
        public Extent getExtent() {
            return this.entity.getExtent();
        }

        @Override
        public boolean remove() {
            Location location = this.entity.getLocation();
            BaseEntity state = this.entity.getState();
            boolean success = this.entity.remove();
            if (state != null && success) {
                ChangeSetExtent.this.changeSet.add(new EntityRemove(location, state));
            }
            return success;
        }

        @Override
        @Nullable
        public <T> T getFacet(Class<? extends T> cls) {
            return this.entity.getFacet(cls);
        }
    }
}

