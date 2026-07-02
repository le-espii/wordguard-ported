/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.world.registry;

import com.sk89q.worldedit.internal.util.DeprecationUtil;
import com.sk89q.worldedit.internal.util.NonAbstractForCompatibility;

public interface ItemMaterial {
    @Deprecated(forRemoval=true)
    default public int getMaxStackSize() {
        return this.maxStackSize();
    }

    @NonAbstractForCompatibility(delegateName="getMaxStackSize", delegateParams={})
    default public int maxStackSize() {
        DeprecationUtil.checkDelegatingOverride(this.getClass());
        return this.getMaxStackSize();
    }

    @Deprecated(forRemoval=true)
    default public int getMaxDamage() {
        return this.maxDamage();
    }

    @NonAbstractForCompatibility(delegateName="getMaxDamage", delegateParams={})
    default public int maxDamage() {
        DeprecationUtil.checkDelegatingOverride(this.getClass());
        return this.getMaxDamage();
    }
}

