/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.sk89q.worldedit.extent;

import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.extent.AbstractDelegateExtent;
import com.sk89q.worldedit.extent.Extent;
import com.sk89q.worldedit.internal.util.DeprecationUtil;
import com.sk89q.worldedit.internal.util.NonAbstractForCompatibility;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.block.BaseBlock;
import com.sk89q.worldedit.world.block.BlockState;
import com.sk89q.worldedit.world.block.BlockStateHolder;
import java.util.Optional;
import javax.annotation.Nullable;

public abstract class AbstractBufferingExtent
extends AbstractDelegateExtent {
    protected AbstractBufferingExtent(Extent extent) {
        super(extent);
    }

    @Override
    public abstract <T extends BlockStateHolder<T>> boolean setBlock(BlockVector3 var1, T var2) throws WorldEditException;

    protected final <T extends BlockStateHolder<T>> boolean setDelegateBlock(BlockVector3 location, T block) throws WorldEditException {
        return super.setBlock(location, block);
    }

    @Override
    public BlockState getBlock(BlockVector3 position) {
        BaseBlock block = this.getBufferedFullBlock(position);
        if (block == null) {
            return super.getBlock(position);
        }
        return block.toImmutableState();
    }

    @Override
    public BaseBlock getFullBlock(BlockVector3 position) {
        BaseBlock block = this.getBufferedFullBlock(position);
        if (block == null) {
            return super.getFullBlock(position);
        }
        return block;
    }

    @Deprecated
    protected Optional<BaseBlock> getBufferedBlock(BlockVector3 position) {
        return Optional.ofNullable(this.getBufferedFullBlock(position));
    }

    @NonAbstractForCompatibility(delegateName="getBufferedBlock", delegateParams={BlockVector3.class})
    @Nullable
    protected BaseBlock getBufferedFullBlock(BlockVector3 position) {
        DeprecationUtil.checkDelegatingOverride(this.getClass());
        return this.getBufferedBlock(position).orElse(null);
    }
}

