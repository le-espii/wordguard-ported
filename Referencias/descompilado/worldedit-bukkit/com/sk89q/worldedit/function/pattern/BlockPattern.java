/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 */
package com.sk89q.worldedit.function.pattern;

import com.google.common.base.Preconditions;
import com.sk89q.worldedit.function.pattern.AbstractPattern;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.block.BaseBlock;
import com.sk89q.worldedit.world.block.BlockStateHolder;

@Deprecated
public class BlockPattern
extends AbstractPattern {
    private BaseBlock block;

    public BlockPattern(BlockStateHolder<?> block) {
        this.setBlock(block);
    }

    public BaseBlock getBlock() {
        return this.block;
    }

    public void setBlock(BlockStateHolder<?> block) {
        Preconditions.checkNotNull(block);
        this.block = block.toBaseBlock();
    }

    @Override
    public BaseBlock applyBlock(BlockVector3 position) {
        return this.block;
    }
}

