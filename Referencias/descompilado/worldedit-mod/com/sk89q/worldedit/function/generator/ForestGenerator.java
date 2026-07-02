/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.function.generator;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.function.RegionFunction;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.util.TreeGenerator;
import com.sk89q.worldedit.world.block.BlockState;
import com.sk89q.worldedit.world.block.BlockType;
import com.sk89q.worldedit.world.block.BlockTypes;

public class ForestGenerator
implements RegionFunction {
    private final TreeGenerator.TreeType treeType;
    private final EditSession editSession;

    public ForestGenerator(EditSession editSession, TreeGenerator.TreeType treeType) {
        this.editSession = editSession;
        this.treeType = treeType;
    }

    @Override
    public boolean apply(BlockVector3 position) throws WorldEditException {
        BlockState block = this.editSession.getBlock(position);
        BlockType t = block.getBlockType();
        if (t.getMaterial().isSolid()) {
            return this.treeType.generate(this.editSession, position.add(0, 1, 0));
        }
        if (t.getMaterial().isReplacedDuringPlacement()) {
            this.editSession.setBlock(position, BlockTypes.AIR.getDefaultState());
            this.editSession.getWorld().setBlock(position, BlockTypes.AIR.getDefaultState());
            boolean success = this.treeType.generate(this.editSession, position);
            if (!success) {
                this.editSession.setBlock(position, block);
            }
            return success;
        }
        return false;
    }
}

