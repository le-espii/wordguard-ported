/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 */
package com.sk89q.worldedit.function.mask;

import com.google.common.base.Preconditions;
import com.sk89q.worldedit.extent.Extent;
import com.sk89q.worldedit.function.mask.AbstractExtentMask;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.block.BlockType;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class BlockTypeMask
extends AbstractExtentMask {
    private final Set<BlockType> blocks = new HashSet<BlockType>();

    public BlockTypeMask(Extent extent, Collection<BlockType> blocks) {
        super(extent);
        Preconditions.checkNotNull(blocks);
        this.blocks.addAll(blocks);
    }

    public BlockTypeMask(Extent extent, BlockType ... block) {
        this(extent, Arrays.asList((BlockType[])Preconditions.checkNotNull((Object)block)));
    }

    public void add(Collection<BlockType> blocks) {
        Preconditions.checkNotNull(blocks);
        this.blocks.addAll(blocks);
    }

    public void add(BlockType ... block) {
        this.add(Arrays.asList((BlockType[])Preconditions.checkNotNull((Object)block)));
    }

    public Collection<BlockType> getBlocks() {
        return this.blocks;
    }

    @Override
    public boolean test(BlockVector3 vector) {
        return this.blocks.contains(this.getExtent().getBlock(vector).getBlockType());
    }
}

