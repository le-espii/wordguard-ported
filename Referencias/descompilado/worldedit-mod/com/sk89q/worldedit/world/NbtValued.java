/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.sk89q.worldedit.world;

import com.sk89q.jnbt.CompoundTag;
import com.sk89q.worldedit.internal.util.DeprecationUtil;
import com.sk89q.worldedit.internal.util.NonAbstractForCompatibility;
import com.sk89q.worldedit.util.concurrency.LazyReference;
import javax.annotation.Nullable;
import org.enginehub.linbus.tree.LinCompoundTag;

public interface NbtValued {
    @Deprecated
    default public boolean hasNbtData() {
        return this.getNbt() != null;
    }

    @Deprecated
    @Nullable
    default public CompoundTag getNbtData() {
        LinCompoundTag tag = this.getNbt();
        return tag == null ? null : new CompoundTag(tag);
    }

    @Deprecated
    default public void setNbtData(@Nullable CompoundTag nbtData) {
        this.setNbtReference(nbtData == null ? null : LazyReference.from(nbtData::toLinTag));
    }

    @NonAbstractForCompatibility(delegateName="getNbtData", delegateParams={})
    @Nullable
    default public LazyReference<LinCompoundTag> getNbtReference() {
        DeprecationUtil.checkDelegatingOverride(this.getClass());
        CompoundTag nbtData = this.getNbtData();
        return nbtData == null ? null : LazyReference.from(nbtData::toLinTag);
    }

    @Nullable
    default public LinCompoundTag getNbt() {
        LazyReference<LinCompoundTag> ref = this.getNbtReference();
        return ref == null ? null : ref.getValue();
    }

    @NonAbstractForCompatibility(delegateName="setNbtData", delegateParams={CompoundTag.class})
    default public void setNbtReference(@Nullable LazyReference<LinCompoundTag> nbtData) {
        DeprecationUtil.checkDelegatingOverride(this.getClass());
        this.setNbtData(nbtData == null ? null : new CompoundTag(nbtData.getValue()));
    }

    default public void setNbt(@Nullable LinCompoundTag nbtData) {
        this.setNbtReference(nbtData == null ? null : LazyReference.computed(nbtData));
    }
}

