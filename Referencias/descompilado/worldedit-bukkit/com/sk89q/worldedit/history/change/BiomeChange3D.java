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
import com.sk89q.worldedit.world.biome.BiomeType;

public record BiomeChange3D(BlockVector3 position, BiomeType previous, BiomeType current) implements Change
{
    public BiomeChange3D {
        Preconditions.checkNotNull((Object)((Object)position));
        Preconditions.checkNotNull((Object)previous);
        Preconditions.checkNotNull((Object)current);
    }

    @Deprecated(forRemoval=true)
    public BlockVector3 getPosition() {
        return this.position;
    }

    @Deprecated(forRemoval=true)
    public BiomeType getPrevious() {
        return this.previous;
    }

    @Deprecated(forRemoval=true)
    public BiomeType getCurrent() {
        return this.current;
    }

    @Override
    public void undo(UndoContext context) throws WorldEditException {
        ((Extent)Preconditions.checkNotNull((Object)context.getExtent())).setBiome(this.position, this.previous);
    }

    @Override
    public void redo(UndoContext context) throws WorldEditException {
        ((Extent)Preconditions.checkNotNull((Object)context.getExtent())).setBiome(this.position, this.current);
    }
}

