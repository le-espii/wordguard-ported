/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  javax.annotation.Nullable
 *  net.minecraft.class_1297
 *  net.minecraft.class_1297$class_5529
 *  net.minecraft.class_2487
 *  net.minecraft.class_2960
 *  net.minecraft.class_7924
 */
package com.sk89q.worldedit.fabric.internal;

import com.google.common.base.Preconditions;
import com.sk89q.worldedit.entity.BaseEntity;
import com.sk89q.worldedit.entity.Entity;
import com.sk89q.worldedit.entity.metadata.EntityProperties;
import com.sk89q.worldedit.extent.Extent;
import com.sk89q.worldedit.fabric.FabricAdapter;
import com.sk89q.worldedit.fabric.FabricEntityProperties;
import com.sk89q.worldedit.fabric.FabricWorldEdit;
import com.sk89q.worldedit.fabric.internal.NBTConverter;
import com.sk89q.worldedit.math.Vector3;
import com.sk89q.worldedit.util.Location;
import com.sk89q.worldedit.util.concurrency.LazyReference;
import com.sk89q.worldedit.world.NullWorld;
import com.sk89q.worldedit.world.entity.EntityTypes;
import java.lang.ref.WeakReference;
import javax.annotation.Nullable;
import net.minecraft.class_1297;
import net.minecraft.class_2487;
import net.minecraft.class_2960;
import net.minecraft.class_7924;

public class FabricEntity
implements Entity {
    private final WeakReference<class_1297> entityRef;

    public FabricEntity(class_1297 entity) {
        Preconditions.checkNotNull((Object)entity);
        this.entityRef = new WeakReference<class_1297>(entity);
    }

    @Override
    public BaseEntity getState() {
        class_1297 entity = (class_1297)this.entityRef.get();
        if (entity == null || entity.method_5765()) {
            return null;
        }
        class_2960 id = FabricWorldEdit.getRegistry(class_7924.field_41266).method_10221((Object)entity.method_5864());
        class_2487 tag = new class_2487();
        entity.method_5647(tag);
        return new BaseEntity(EntityTypes.get(id.toString()), LazyReference.from(() -> NBTConverter.fromNative(tag)));
    }

    @Override
    public Location getLocation() {
        class_1297 entity = (class_1297)this.entityRef.get();
        if (entity != null) {
            Vector3 position = Vector3.at(entity.method_23317(), entity.method_23318(), entity.method_23321());
            float yaw = entity.method_36454();
            float pitch = entity.method_36455();
            return new Location((Extent)FabricAdapter.adapt(entity.method_37908()), position, yaw, pitch);
        }
        return new Location(NullWorld.getInstance());
    }

    @Override
    public boolean setLocation(Location location) {
        return false;
    }

    @Override
    public Extent getExtent() {
        class_1297 entity = (class_1297)this.entityRef.get();
        if (entity != null) {
            return FabricAdapter.adapt(entity.method_37908());
        }
        return NullWorld.getInstance();
    }

    @Override
    public boolean remove() {
        class_1297 entity = (class_1297)this.entityRef.get();
        if (entity != null) {
            entity.method_5650(class_1297.class_5529.field_26998);
        }
        return true;
    }

    @Override
    @Nullable
    public <T> T getFacet(Class<? extends T> cls) {
        class_1297 entity = (class_1297)this.entityRef.get();
        if (entity != null) {
            if (EntityProperties.class.isAssignableFrom(cls)) {
                return (T)new FabricEntityProperties(entity);
            }
            return null;
        }
        return null;
    }
}

