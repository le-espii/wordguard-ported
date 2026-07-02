/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 */
package com.sk89q.worldedit.util;

import com.google.common.base.Preconditions;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.block.BaseBlock;

public record LocatedBlock(BlockVector3 location, BaseBlock block) {
    public LocatedBlock {
        Preconditions.checkNotNull((Object)((Object)location));
        Preconditions.checkNotNull((Object)block);
    }

    @Deprecated(forRemoval=true)
    public BlockVector3 getLocation() {
        return this.location;
    }

    @Deprecated(forRemoval=true)
    public BaseBlock getBlock() {
        return this.block;
    }
}

