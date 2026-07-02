/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.extent.clipboard.io.legacycompat;

import com.sk89q.jnbt.CompoundTag;
import com.sk89q.worldedit.internal.util.DeprecationUtil;
import com.sk89q.worldedit.internal.util.NonAbstractForCompatibility;
import com.sk89q.worldedit.world.entity.EntityType;
import org.enginehub.linbus.tree.LinCompoundTag;

public interface EntityNBTCompatibilityHandler {
    @Deprecated
    default public boolean isAffectedEntity(EntityType type, CompoundTag entityTag) {
        LinCompoundTag updated;
        LinCompoundTag original = (LinCompoundTag)entityTag.toLinTag();
        return !original.equals(updated = this.updateNbt(type, original));
    }

    @Deprecated
    default public CompoundTag updateNBT(EntityType type, CompoundTag entityTag) {
        return new CompoundTag(this.updateNbt(type, (LinCompoundTag)entityTag.toLinTag()));
    }

    @NonAbstractForCompatibility(delegateName="updateNBT", delegateParams={EntityType.class, CompoundTag.class})
    default public LinCompoundTag updateNbt(EntityType type, LinCompoundTag entityTag) {
        DeprecationUtil.checkDelegatingOverride(this.getClass());
        return (LinCompoundTag)this.updateNBT(type, new CompoundTag(entityTag)).toLinTag();
    }
}

