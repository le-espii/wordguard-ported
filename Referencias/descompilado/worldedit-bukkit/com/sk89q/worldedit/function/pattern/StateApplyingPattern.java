/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 */
package com.sk89q.worldedit.function.pattern;

import com.google.common.collect.Maps;
import com.sk89q.worldedit.blocks.Blocks;
import com.sk89q.worldedit.extent.Extent;
import com.sk89q.worldedit.function.pattern.AbstractExtentPattern;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.registry.state.Property;
import com.sk89q.worldedit.world.block.BaseBlock;
import com.sk89q.worldedit.world.block.BlockState;
import com.sk89q.worldedit.world.block.BlockStateHolder;
import com.sk89q.worldedit.world.block.BlockType;
import java.util.Map;

public class StateApplyingPattern
extends AbstractExtentPattern {
    private final Map<String, String> states;
    private final Map<BlockType, Map<Property<Object>, Object>> cache = Maps.newHashMap();

    public StateApplyingPattern(Extent extent, Map<String, String> statesToSet) {
        super(extent);
        this.states = statesToSet;
    }

    @Override
    public BaseBlock applyBlock(BlockVector3 position) {
        BlockStateHolder<BlockState> block = this.getExtent().getBlock(position);
        for (Map.Entry entry : this.cache.computeIfAbsent(block.getBlockType(), b -> Blocks.resolveProperties(this.states, b)).entrySet()) {
            block = block.with((Property)entry.getKey(), entry.getValue());
        }
        return block.toBaseBlock();
    }
}

