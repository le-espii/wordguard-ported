/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.function.pattern;

import com.sk89q.worldedit.internal.util.DeprecationUtil;
import com.sk89q.worldedit.internal.util.NonAbstractForCompatibility;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.block.BaseBlock;

public interface Pattern {
    @Deprecated
    default public BaseBlock apply(BlockVector3 position) {
        return this.applyBlock(position);
    }

    @NonAbstractForCompatibility(delegateName="apply", delegateParams={BlockVector3.class})
    default public BaseBlock applyBlock(BlockVector3 position) {
        DeprecationUtil.checkDelegatingOverride(this.getClass());
        return this.apply(position);
    }
}

