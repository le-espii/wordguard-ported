/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.function.pattern;

import com.sk89q.worldedit.extent.Extent;
import com.sk89q.worldedit.function.pattern.AbstractExtentPattern;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.registry.state.Property;
import com.sk89q.worldedit.world.block.BaseBlock;
import com.sk89q.worldedit.world.block.BlockTypes;

public class WaterloggedRemover
extends AbstractExtentPattern {
    public WaterloggedRemover(Extent extent) {
        super(extent);
    }

    @Override
    public BaseBlock applyBlock(BlockVector3 position) {
        BaseBlock block = this.getExtent().getFullBlock(position);
        Property prop = block.getBlockType().getPropertyMap().getOrDefault("waterlogged", null);
        if (prop != null) {
            return block.with(prop, (Object)false);
        }
        return BlockTypes.AIR.getDefaultState().toBaseBlock();
    }
}

