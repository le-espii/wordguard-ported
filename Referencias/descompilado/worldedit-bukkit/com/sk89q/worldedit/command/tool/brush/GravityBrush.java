/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.command.tool.brush;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.command.tool.brush.Brush;
import com.sk89q.worldedit.function.pattern.Pattern;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.util.LocatedBlock;
import com.sk89q.worldedit.util.collection.LocatedBlockList;
import com.sk89q.worldedit.world.block.BaseBlock;
import com.sk89q.worldedit.world.block.BlockTypes;
import java.util.LinkedHashSet;

public class GravityBrush
implements Brush {
    private final boolean overrideHeight;
    private final int heightOffset;

    public GravityBrush(Integer heightOffset) {
        this.overrideHeight = heightOffset != null;
        this.heightOffset = heightOffset != null ? heightOffset : Integer.MIN_VALUE;
    }

    @Override
    public void build(EditSession editSession, BlockVector3 position, Pattern pattern, double size) throws MaxChangedBlocksException {
        double sizeOffset = this.overrideHeight ? (double)this.heightOffset : size;
        double yMax = Math.min((double)position.y() + sizeOffset, (double)editSession.getWorld().getMaxY());
        double yMin = Math.max((double)position.y() - sizeOffset, (double)editSession.getWorld().getMinY());
        LocatedBlockList column = new LocatedBlockList();
        LinkedHashSet<BlockVector3> removedBlocks = new LinkedHashSet<BlockVector3>();
        for (double x = (double)position.x() - size; x <= (double)position.x() + size; x += 1.0) {
            for (double z = (double)position.z() - size; z <= (double)position.z() + size; z += 1.0) {
                BlockVector3 lowestAir = null;
                for (double y = yMin; y <= yMax; y += 1.0) {
                    BlockVector3 pt = BlockVector3.at(x, y, z);
                    BaseBlock block = editSession.getFullBlock(pt);
                    if (block.getBlockType().getMaterial().isAir()) {
                        if (lowestAir != null) continue;
                        lowestAir = pt;
                        continue;
                    }
                    if (lowestAir == null) continue;
                    BlockVector3 newPos = lowestAir;
                    lowestAir = lowestAir.add(0, 1, 0);
                    removedBlocks.remove((Object)newPos);
                    column.add(newPos, block);
                    removedBlocks.add(pt);
                }
                for (LocatedBlock block : column) {
                    editSession.setBlock(block.location(), block.block());
                }
                for (BlockVector3 removedBlock : removedBlocks) {
                    editSession.setBlock(removedBlock, BlockTypes.AIR.getDefaultState());
                }
                column.clear();
                removedBlocks.clear();
            }
        }
    }
}

