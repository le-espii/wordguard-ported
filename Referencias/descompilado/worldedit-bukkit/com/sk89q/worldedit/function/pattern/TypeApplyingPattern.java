/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.function.pattern;

import com.sk89q.worldedit.extent.Extent;
import com.sk89q.worldedit.function.pattern.AbstractExtentPattern;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.registry.state.Property;
import com.sk89q.worldedit.world.block.BaseBlock;
import com.sk89q.worldedit.world.block.BlockState;
import com.sk89q.worldedit.world.block.BlockStateHolder;
import java.util.Map;

public class TypeApplyingPattern
extends AbstractExtentPattern {
    private final BlockState blockState;

    public TypeApplyingPattern(Extent extent, BlockState blockState) {
        super(extent);
        this.blockState = blockState;
    }

    @Override
    public BaseBlock applyBlock(BlockVector3 position) {
        BlockState oldBlock = this.getExtent().getBlock(position);
        BlockStateHolder<BlockState> newBlock = this.blockState;
        for (Map.Entry<Property<?>, Object> entry : oldBlock.getStates().entrySet()) {
            Property<?> prop = entry.getKey();
            newBlock = newBlock.with(prop, entry.getValue());
        }
        return newBlock.toBaseBlock();
    }
}

