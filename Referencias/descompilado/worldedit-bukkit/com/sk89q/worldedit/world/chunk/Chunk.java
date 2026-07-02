/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.world.chunk;

import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.DataException;
import com.sk89q.worldedit.world.block.BaseBlock;

public interface Chunk {
    public BaseBlock getBlock(BlockVector3 var1) throws DataException;
}

