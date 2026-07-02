/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  javax.annotation.Nullable
 */
package com.sk89q.worldedit.math.convolution;

import com.google.common.base.Preconditions;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.function.mask.Mask;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.math.convolution.HeightMapFilter;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.world.block.BlockState;
import com.sk89q.worldedit.world.block.BlockTypes;
import javax.annotation.Nullable;

public class HeightMap {
    private final int[] data;
    private final int width;
    private final int height;
    private final Region region;
    private final EditSession session;

    public HeightMap(EditSession session, Region region, @Nullable Mask mask) {
        Preconditions.checkNotNull((Object)session);
        Preconditions.checkNotNull((Object)region);
        this.session = session;
        this.region = region;
        this.width = region.getWidth();
        this.height = region.getLength();
        int minX = region.getMinimumPoint().x();
        int minY = region.getMinimumPoint().y();
        int minZ = region.getMinimumPoint().z();
        int maxY = region.getMaximumPoint().y();
        this.data = new int[this.width * this.height];
        for (int z = 0; z < this.height; ++z) {
            for (int x = 0; x < this.width; ++x) {
                this.data[z * this.width + x] = session.getHighestTerrainBlock(x + minX, z + minZ, minY, maxY, mask);
            }
        }
    }

    public int applyFilter(HeightMapFilter filter, int iterations) throws MaxChangedBlocksException {
        Preconditions.checkNotNull((Object)filter);
        int[] newData = new int[this.data.length];
        System.arraycopy(this.data, 0, newData, 0, this.data.length);
        for (int i = 0; i < iterations; ++i) {
            newData = filter.filter(newData, this.width, this.height, 0.5f);
        }
        return this.apply(newData);
    }

    public int apply(int[] data) throws MaxChangedBlocksException {
        Preconditions.checkNotNull((Object)data);
        BlockVector3 minY = this.region.getMinimumPoint();
        int originX = minY.x();
        int originY = minY.y();
        int originZ = minY.z();
        int maxY = this.region.getMaximumPoint().y();
        BlockState fillerAir = BlockTypes.AIR.getDefaultState();
        int blocksChanged = 0;
        for (int z = 0; z < this.height; ++z) {
            for (int x = 0; x < this.width; ++x) {
                int y;
                int index = z * this.width + x;
                int curHeight = this.data[index];
                int newHeight = Math.min(maxY, data[index]);
                int xr = x + originX;
                int zr = z + originZ;
                double scale = (double)(curHeight - originY) / (double)(newHeight - originY);
                if (newHeight > curHeight) {
                    BlockState existing = this.session.getBlock(BlockVector3.at(xr, curHeight, zr));
                    if (existing.getBlockType().getMaterial().isLiquid()) continue;
                    this.session.setBlock(BlockVector3.at(xr, newHeight, zr), existing);
                    ++blocksChanged;
                    for (int y2 = newHeight - 1 - originY; y2 >= 0; --y2) {
                        int copyFrom = (int)Math.floor((double)y2 * scale);
                        this.session.setBlock(BlockVector3.at(xr, originY + y2, zr), this.session.getBlock(BlockVector3.at(xr, originY + copyFrom, zr)));
                        ++blocksChanged;
                    }
                    continue;
                }
                if (curHeight <= newHeight) continue;
                for (y = 0; y < newHeight - originY; ++y) {
                    int copyFrom = (int)Math.floor((double)y * scale);
                    this.session.setBlock(BlockVector3.at(xr, originY + y, zr), this.session.getBlock(BlockVector3.at(xr, originY + copyFrom, zr)));
                    ++blocksChanged;
                }
                this.session.setBlock(BlockVector3.at(xr, newHeight, zr), this.session.getBlock(BlockVector3.at(xr, curHeight, zr)));
                ++blocksChanged;
                for (y = newHeight + 1; y <= curHeight; ++y) {
                    this.session.setBlock(BlockVector3.at(xr, y, zr), fillerAir);
                    ++blocksChanged;
                }
            }
        }
        return blocksChanged;
    }
}

