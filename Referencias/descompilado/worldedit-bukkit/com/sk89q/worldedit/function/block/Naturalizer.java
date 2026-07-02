/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 */
package com.sk89q.worldedit.function.block;

import com.google.common.base.Preconditions;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.extent.Extent;
import com.sk89q.worldedit.function.LayerFunction;
import com.sk89q.worldedit.function.mask.BlockTypeMask;
import com.sk89q.worldedit.function.mask.Mask;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.block.BlockState;
import com.sk89q.worldedit.world.block.BlockTypes;

public class Naturalizer
implements LayerFunction {
    private final EditSession editSession;
    private final Mask mask;
    private int affected = 0;

    public Naturalizer(EditSession editSession) {
        Preconditions.checkNotNull((Object)editSession);
        this.editSession = editSession;
        this.mask = new BlockTypeMask((Extent)editSession, BlockTypes.GRASS_BLOCK, BlockTypes.DIRT, BlockTypes.STONE);
    }

    public int getAffected() {
        return this.affected;
    }

    @Override
    public boolean isGround(BlockVector3 position) {
        return this.mask.test(position);
    }

    private BlockState getTargetBlock(int depth) {
        return switch (depth) {
            case 0 -> BlockTypes.GRASS_BLOCK.getDefaultState();
            case 1, 2, 3 -> BlockTypes.DIRT.getDefaultState();
            default -> BlockTypes.STONE.getDefaultState();
        };
    }

    private boolean naturalize(BlockVector3 position, int depth) throws WorldEditException {
        BlockState targetBlock;
        BlockState block = this.editSession.getBlock(position);
        if (block.equalsFuzzy(targetBlock = this.getTargetBlock(depth))) {
            return false;
        }
        return this.editSession.setBlock(position, targetBlock);
    }

    @Override
    public boolean apply(BlockVector3 position, int depth) throws WorldEditException {
        if (this.mask.test(position) && this.naturalize(position, depth)) {
            ++this.affected;
        }
        return true;
    }
}

