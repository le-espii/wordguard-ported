/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  javax.annotation.Nullable
 */
package com.sk89q.worldedit.entity;

import com.google.common.base.Preconditions;
import com.sk89q.jnbt.CompoundTag;
import com.sk89q.worldedit.util.concurrency.LazyReference;
import com.sk89q.worldedit.world.NbtValued;
import com.sk89q.worldedit.world.entity.EntityType;
import javax.annotation.Nullable;
import org.enginehub.linbus.tree.LinCompoundTag;

public class BaseEntity
implements NbtValued {
    private final EntityType type;
    @Nullable
    private LazyReference<LinCompoundTag> nbtData;

    @Deprecated
    public BaseEntity(EntityType type, CompoundTag nbtData) {
        this(type);
        this.setNbtData(nbtData);
    }

    public BaseEntity(EntityType type, LazyReference<LinCompoundTag> nbtData) {
        this(type);
        this.setNbtReference(nbtData);
    }

    public BaseEntity(EntityType type) {
        this.type = type;
    }

    public BaseEntity(BaseEntity other) {
        Preconditions.checkNotNull((Object)other);
        this.type = other.getType();
        this.setNbtReference(other.getNbtReference());
    }

    @Override
    @Nullable
    public LazyReference<LinCompoundTag> getNbtReference() {
        return this.nbtData;
    }

    @Override
    public void setNbtReference(@Nullable LazyReference<LinCompoundTag> nbtData) {
        this.nbtData = nbtData;
    }

    public EntityType getType() {
        return this.type;
    }
}

