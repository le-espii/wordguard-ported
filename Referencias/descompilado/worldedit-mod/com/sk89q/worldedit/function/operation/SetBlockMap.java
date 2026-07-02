/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 */
package com.sk89q.worldedit.function.operation;

import com.google.common.base.Preconditions;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.extent.Extent;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.RunContext;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.util.collection.BlockMap;
import com.sk89q.worldedit.world.block.BaseBlock;
import java.util.Map;

public class SetBlockMap
implements Operation {
    private final Extent extent;
    private final BlockMap<BaseBlock> blocks;

    public SetBlockMap(Extent extent, BlockMap<BaseBlock> blocks) {
        this.extent = (Extent)Preconditions.checkNotNull((Object)extent);
        this.blocks = (BlockMap)Preconditions.checkNotNull(blocks);
    }

    @Override
    public Operation resume(RunContext run) throws WorldEditException {
        for (Map.Entry<BlockVector3, BaseBlock> entry : this.blocks.entrySet()) {
            this.extent.setBlock(entry.getKey(), entry.getValue());
        }
        return null;
    }

    @Override
    public void cancel() {
    }
}

