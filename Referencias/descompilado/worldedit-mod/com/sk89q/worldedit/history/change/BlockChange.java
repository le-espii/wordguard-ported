/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 */
package com.sk89q.worldedit.history.change;

import com.google.common.base.Preconditions;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.extent.Extent;
import com.sk89q.worldedit.history.UndoContext;
import com.sk89q.worldedit.history.change.Change;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.block.BaseBlock;
import com.sk89q.worldedit.world.block.BlockStateHolder;

public record BlockChange(BlockVector3 position, BaseBlock previous, BaseBlock current) implements Change
{
    public BlockChange {
        Preconditions.checkNotNull((Object)((Object)position));
        Preconditions.checkNotNull((Object)previous);
        Preconditions.checkNotNull((Object)current);
    }

    public <BP extends BlockStateHolder<BP>, BC extends BlockStateHolder<BC>> BlockChange(BlockVector3 position, BP previous, BC current) {
        this(position, previous.toBaseBlock(), current.toBaseBlock());
    }

    @Deprecated(forRemoval=true)
    public BlockVector3 getPosition() {
        return this.position;
    }

    @Deprecated(forRemoval=true)
    public BaseBlock getPrevious() {
        return this.previous;
    }

    @Deprecated(forRemoval=true)
    public BaseBlock getCurrent() {
        return this.current;
    }

    @Override
    public void undo(UndoContext context) throws WorldEditException {
        ((Extent)Preconditions.checkNotNull((Object)context.getExtent())).setBlock(this.position, this.previous);
    }

    @Override
    public void redo(UndoContext context) throws WorldEditException {
        ((Extent)Preconditions.checkNotNull((Object)context.getExtent())).setBlock(this.position, this.current);
    }
}

