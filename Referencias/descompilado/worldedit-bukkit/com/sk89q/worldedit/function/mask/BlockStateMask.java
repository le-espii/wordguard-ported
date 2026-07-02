/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  javax.annotation.Nullable
 */
package com.sk89q.worldedit.function.mask;

import com.google.common.collect.Maps;
import com.sk89q.worldedit.blocks.Blocks;
import com.sk89q.worldedit.extent.Extent;
import com.sk89q.worldedit.function.mask.AbstractExtentMask;
import com.sk89q.worldedit.function.mask.Mask2D;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.registry.state.Property;
import com.sk89q.worldedit.world.block.BlockState;
import com.sk89q.worldedit.world.block.BlockType;
import java.util.Map;
import java.util.Objects;
import javax.annotation.Nullable;

public class BlockStateMask
extends AbstractExtentMask {
    private final Map<String, String> states;
    private final boolean strict;
    private final Map<BlockType, Map<Property<Object>, Object>> cache = Maps.newHashMap();

    public BlockStateMask(Extent extent, Map<String, String> states, boolean strict) {
        super(extent);
        this.states = states;
        this.strict = strict;
    }

    @Override
    public boolean test(BlockVector3 vector) {
        BlockState block = this.getExtent().getBlock(vector);
        Map checkProps = this.cache.computeIfAbsent(block.getBlockType(), b -> Blocks.resolveProperties(this.states, b));
        if (this.strict && checkProps.isEmpty()) {
            return false;
        }
        for (Map.Entry entry : checkProps.entrySet()) {
            if (Objects.equals(block.getState((Property)entry.getKey()), entry.getValue())) continue;
            return false;
        }
        return true;
    }

    @Override
    @Nullable
    public Mask2D toMask2D() {
        return null;
    }
}

