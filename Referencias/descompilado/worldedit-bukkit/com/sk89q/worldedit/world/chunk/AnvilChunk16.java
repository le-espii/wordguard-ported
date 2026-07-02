/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.world.chunk;

import com.sk89q.jnbt.CompoundTag;
import com.sk89q.worldedit.world.DataException;
import com.sk89q.worldedit.world.block.BlockState;
import com.sk89q.worldedit.world.chunk.AnvilChunk13;
import com.sk89q.worldedit.world.chunk.PackedIntArrayReader;
import com.sk89q.worldedit.world.storage.InvalidFormatException;
import org.enginehub.linbus.tree.LinCompoundTag;

public class AnvilChunk16
extends AnvilChunk13 {
    @Deprecated
    public AnvilChunk16(CompoundTag tag) throws DataException {
        super(tag);
    }

    public AnvilChunk16(LinCompoundTag tag) throws DataException {
        super(tag);
    }

    @Override
    protected void readBlockStates(BlockState[] palette, long[] blockStatesSerialized, BlockState[] chunkSectionBlocks) throws InvalidFormatException {
        PackedIntArrayReader reader = new PackedIntArrayReader(blockStatesSerialized);
        for (int blockPos = 0; blockPos < chunkSectionBlocks.length; ++blockPos) {
            int index = reader.get(blockPos);
            if (index >= palette.length) {
                throw new InvalidFormatException("Invalid block state table entry: " + index);
            }
            chunkSectionBlocks[blockPos] = palette[index];
        }
    }
}

