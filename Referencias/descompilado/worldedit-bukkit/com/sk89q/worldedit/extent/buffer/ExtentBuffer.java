/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 */
package com.sk89q.worldedit.extent.buffer;

import com.google.common.base.Preconditions;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.extent.AbstractBufferingExtent;
import com.sk89q.worldedit.extent.Extent;
import com.sk89q.worldedit.function.mask.Mask;
import com.sk89q.worldedit.function.mask.Masks;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.util.collection.BlockMap;
import com.sk89q.worldedit.world.block.BaseBlock;
import com.sk89q.worldedit.world.block.BlockStateHolder;
import java.util.Map;

public class ExtentBuffer
extends AbstractBufferingExtent {
    private final Map<BlockVector3, BaseBlock> buffer = BlockMap.create();
    private final Mask mask;

    public ExtentBuffer(Extent delegate) {
        this(delegate, Masks.alwaysTrue());
    }

    public ExtentBuffer(Extent delegate, Mask mask) {
        super(delegate);
        Preconditions.checkNotNull((Object)delegate);
        Preconditions.checkNotNull((Object)mask);
        this.mask = mask;
    }

    @Override
    protected BaseBlock getBufferedFullBlock(BlockVector3 position) {
        if (this.mask.test(position)) {
            return this.buffer.computeIfAbsent(position, pos -> this.getExtent().getFullBlock((BlockVector3)((Object)pos)));
        }
        return null;
    }

    @Override
    public <T extends BlockStateHolder<T>> boolean setBlock(BlockVector3 location, T block) throws WorldEditException {
        if (this.mask.test(location)) {
            this.buffer.put(location, block.toBaseBlock());
            return true;
        }
        return false;
    }
}

