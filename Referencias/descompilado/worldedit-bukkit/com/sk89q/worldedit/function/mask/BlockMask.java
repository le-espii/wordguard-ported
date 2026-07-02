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
import com.sk89q.worldedit.world.block.BaseBlock;
import com.sk89q.worldedit.world.block.BlockState;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class BlockMask
extends AbstractExtentMask {
    private final Set<BaseBlock> blocks = new HashSet<BaseBlock>();

    public BlockMask(Extent extent, Collection<BaseBlock> blocks) {
        super(extent);
        Preconditions.checkNotNull(blocks);
        this.blocks.addAll(blocks);
    }

    public BlockMask(Extent extent, BaseBlock ... block) {
        this(extent, Arrays.asList((BaseBlock[])Preconditions.checkNotNull((Object)block)));
    }

    public void add(Collection<BaseBlock> blocks) {
        Preconditions.checkNotNull(blocks);
        this.blocks.addAll(blocks);
    }

    public void add(BaseBlock ... block) {
        this.add(Arrays.asList((BaseBlock[])Preconditions.checkNotNull((Object)block)));
    }

    public Collection<BaseBlock> getBlocks() {
        return this.blocks;
    }

    @Override
    public boolean test(BlockVector3 vector) {
        BlockState block = this.getExtent().getBlock(vector);
        for (BaseBlock testBlock : this.blocks) {
            if (!testBlock.equalsFuzzy(block)) continue;
            return true;
        }
        return false;
    }
}

