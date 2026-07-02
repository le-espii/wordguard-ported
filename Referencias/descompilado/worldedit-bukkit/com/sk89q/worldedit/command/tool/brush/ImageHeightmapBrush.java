/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.command.tool.brush;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.command.tool.brush.Brush;
import com.sk89q.worldedit.function.pattern.Pattern;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.util.asset.holder.ImageHeightmap;
import com.sk89q.worldedit.world.block.BlockState;
import com.sk89q.worldedit.world.block.BlockTypes;
import java.util.concurrent.ThreadLocalRandom;

public class ImageHeightmapBrush
implements Brush {
    private final ImageHeightmap heightmap;
    private final double intensity;
    private final boolean erase;
    private final boolean flatten;
    private final boolean randomize;

    public ImageHeightmapBrush(ImageHeightmap heightmap, double intensity, boolean erase, boolean flatten, boolean randomize) {
        this.heightmap = heightmap;
        this.intensity = intensity;
        this.erase = erase;
        this.flatten = flatten;
        this.randomize = randomize;
    }

    @Override
    public void build(EditSession editSession, BlockVector3 position, Pattern pattern, double doubleSize) throws MaxChangedBlocksException {
        int size = (int)Math.ceil(doubleSize);
        double random = this.randomize ? ThreadLocalRandom.current().nextDouble() : 0.0;
        for (int offX = -size; offX <= size; ++offX) {
            for (int offZ = -size; offZ <= size; ++offZ) {
                int posX = position.x() + offX;
                int posZ = position.z() + offZ;
                int posY = editSession.getHighestTerrainBlock(posX, posZ, editSession.getMinimumPoint().y(), editSession.getMaximumPoint().y(), editSession.getMask());
                BlockVector3 block = BlockVector3.at(posX, posY, posZ);
                if (editSession.getMask() != null && !editSession.getMask().test(block)) continue;
                double height = this.heightmap.getHeightAt(offX + size, offZ + size, size * 2 + 1) * this.intensity;
                if (this.randomize && random > 1.0 - height % 1.0) {
                    height += 1.0;
                }
                BlockState baseBlock = this.erase ? null : editSession.getBlock(block);
                int y = 0;
                while ((double)y < height) {
                    if (this.erase) {
                        if (!this.flatten || block.y() - y > position.y()) {
                            editSession.setBlock(block.withY(block.y() - y), BlockTypes.AIR.getDefaultState());
                        }
                    } else if (!this.flatten || block.y() + y <= position.y()) {
                        editSession.setBlock(block.withY(block.y() + y), baseBlock);
                    }
                    ++y;
                }
            }
        }
    }
}

