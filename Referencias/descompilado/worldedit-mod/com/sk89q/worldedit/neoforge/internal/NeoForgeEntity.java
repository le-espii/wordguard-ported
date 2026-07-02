/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  javax.annotation.Nullable
 *  net.minecraft.core.registries.BuiltInRegistries
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.Entity$RemovalReason
 */
package com.sk89q.worldedit.neoforge.internal;

import com.google.common.base.Preconditions;
import com.sk89q.worldedit.entity.BaseEntity;
import com.sk89q.worldedit.entity.Entity;
import com.sk89q.worldedit.entity.metadata.EntityProperties;
import com.sk89q.worldedit.extent.Extent;
import com.sk89q.worldedit.math.Vector3;
import com.sk89q.worldedit.neoforge.NeoForgeAdapter;
import com.sk89q.worldedit.neoforge.NeoForgeEntityProperties;
import com.sk89q.worldedit.neoforge.internal.NBTConverter;
import com.sk89q.worldedit.util.Location;
import com.sk89q.worldedit.util.concurrency.LazyReference;
import com.sk89q.worldedit.world.NullWorld;
import com.sk89q.worldedit.world.entity.EntityTypes;
import java.lang.ref.WeakReference;
import javax.annotation.Nullable;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;

public class NeoForgeEntity
implements Entity {
    private final WeakReference<net.minecraft.world.entity.Entity> entityRef;

    public NeoForgeEntity(net.minecraft.world.entity.Entity entity) {
        Preconditions.checkNotNull((Object)entity);
        this.entityRef = new WeakReference<net.minecraft.world.entity.Entity>(entity);
    }

    @Override
    public BaseEntity getState() {
        net.minecraft.world.entity.Entity entity = (net.minecraft.world.entity.Entity)this.entityRef.get();
        if (entity == null || entity.isPassenger()) {
            return null;
        }
        ResourceLocation id = BuiltInRegistries.ENTITY_TYPE.getKey((Object)entity.getType());
        CompoundTag tag = new CompoundTag();
        entity.saveWithoutId(tag);
        return new BaseEntity(EntityTypes.get(id.toString()), LazyReference.from(() -> NBTConverter.fromNative(tag)));
    }

    @Override
    public Location getLocation() {
        net.minecraft.world.entity.Entity entity = (net.minecraft.world.entity.Entity)this.entityRef.get();
        if (entity != null) {
            Vector3 position = Vector3.at(entity.getX(), entity.getY(), entity.getZ());
            float yaw = entity.getYRot();
            float pitch = entity.getXRot();
            return new Location((Extent)NeoForgeAdapter.adapt((ServerLevel)entity.level()), position, yaw, pitch);
        }
        return new Location(NullWorld.getInstance());
    }

    @Override
    public boolean setLocation(Location location) {
        return false;
    }

    @Override
    public Extent getExtent() {
        net.minecraft.world.entity.Entity entity = (net.minecraft.world.entity.Entity)this.entityRef.get();
        if (entity != null) {
            return NeoForgeAdapter.adapt((ServerLevel)entity.level());
        }
        return NullWorld.getInstance();
    }

    @Override
    public boolean remove() {
        net.minecraft.world.entity.Entity entity = (net.minecraft.world.entity.Entity)this.entityRef.get();
        if (entity != null) {
            entity.remove(Entity.RemovalReason.KILLED);
        }
        return true;
    }

    @Override
    @Nullable
    public <T> T getFacet(Class<? extends T> cls) {
        net.minecraft.world.entity.Entity entity = (net.minecraft.world.entity.Entity)this.entityRef.get();
        if (entity != null) {
            if (EntityProperties.class.isAssignableFrom(cls)) {
                return (T)new NeoForgeEntityProperties(entity);
            }
            return null;
        }
        return null;
    }
}

