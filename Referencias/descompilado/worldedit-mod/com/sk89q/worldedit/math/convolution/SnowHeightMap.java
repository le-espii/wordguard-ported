/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ImmutableMap
 *  javax.annotation.Nullable
 */
package com.sk89q.worldedit.math.convolution;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.function.mask.Mask;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.math.convolution.HeightMapFilter;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.registry.state.Property;
import com.sk89q.worldedit.world.block.BlockState;
import com.sk89q.worldedit.world.block.BlockTypes;
import java.util.Map;
import javax.annotation.Nullable;

public class SnowHeightMap {
    private final float[] data;
    private final int width;
    private final int height;
    private final Region region;
    private final EditSession session;
    private final Property<Integer> layers;

    public SnowHeightMap(EditSession session, Region region, @Nullable Mask mask) {
        Preconditions.checkNotNull((Object)session);
        Preconditions.checkNotNull((Object)region);
        this.session = session;
        this.region = region;
        this.width = region.getWidth();
        this.height = region.getLength();
        this.layers = BlockTypes.SNOW.getProperty("layers");
        int minX = region.getMinimumPoint().x();
        int minY = region.getMinimumPoint().y();
        int minZ = region.getMinimumPoint().z();
        int maxY = region.getMaximumPoint().y();
        this.data = new float[this.width * this.height];
        for (int z = 0; z < this.height; ++z) {
            for (int x = 0; x < this.width; ++x) {
                int highestBlockY = session.getHighestTerrainBlock(x + minX, z + minZ, minY, maxY, mask);
                BlockState upper = session.getBlock(BlockVector3.at(x + minX, highestBlockY + 1, z + minZ));
                if (upper.getBlockType() == BlockTypes.SNOW) {
                    Integer amountLayers = upper.getState(this.layers);
                    this.data[z * this.width + x] = (float)(highestBlockY + 1) + ((float)amountLayers.intValue() - 1.0f) / 8.0f;
                    continue;
                }
                BlockState block = session.getBlock(BlockVector3.at(x + minX, highestBlockY, z + minZ));
                this.data[z * this.width + x] = block.getBlockType().getMaterial().isAir() ? (float)highestBlockY : (float)(highestBlockY + 1);
            }
        }
    }

    public float[] applyFilter(HeightMapFilter filter, int iterations) {
        int i;
        Preconditions.checkNotNull((Object)filter);
        float[] newData = (float[])this.data.clone();
        for (i = 0; i < iterations; ++i) {
            newData = filter.filter(newData, this.width, this.height, 0.0f);
        }
        for (i = 0; i < newData.length; ++i) {
            newData[i] = newData[i] + 0.0625f;
        }
        return newData;
    }

    public int applyChanges(float[] data, int layerBlocks) throws MaxChangedBlocksException {
        Preconditions.checkNotNull((Object)data);
        BlockVector3 minY = this.region.getMinimumPoint();
        int originX = minY.x();
        int originY = minY.y();
        int originZ = minY.z();
        int maxY = this.region.getMaximumPoint().y();
        BlockState fillerAir = BlockTypes.AIR.getDefaultState();
        BlockState fillerSnow = BlockTypes.SNOW_BLOCK.getDefaultState();
        int blocksChanged = 0;
        for (int z = 0; z < this.height; ++z) {
            for (int x = 0; x < this.width; ++x) {
                int y;
                int index = z * this.width + x;
                float curHeight = this.data[index];
                if (curHeight == (float)originY) continue;
                float newHeight = Math.min((float)maxY, data[index]);
                int xr = x + originX;
                int zr = z + originZ;
                double scale = (double)(curHeight - (float)originY) / (double)(newHeight - (float)originY);
                if (newHeight >= curHeight) {
                    BlockState existing = this.session.getBlock(BlockVector3.at((double)xr, curHeight, (double)zr));
                    if (existing.getBlockType().getMaterial().isLiquid()) continue;
                    this.setSnowLayer(xr, zr, newHeight);
                    ++blocksChanged;
                    for (int y2 = (int)Math.floor(newHeight - 1.0f - (float)originY); y2 >= 0; --y2) {
                        if ((double)y2 >= Math.floor(newHeight - 1.0f - (float)originY - (float)layerBlocks)) {
                            this.session.setBlock(BlockVector3.at(xr, originY + y2, zr), fillerSnow);
                        } else {
                            int copyFrom = (int)Math.floor((double)y2 * scale);
                            BlockState block = this.session.getBlock(BlockVector3.at(xr, originY + copyFrom, zr));
                            this.session.setBlock(BlockVector3.at(xr, originY + y2, zr), block);
                        }
                        ++blocksChanged;
                    }
                    continue;
                }
                for (y = 0; y < (int)Math.floor(newHeight - (float)originY); ++y) {
                    if (y >= (int)Math.floor(newHeight - (float)originY - (float)layerBlocks)) {
                        this.session.setBlock(BlockVector3.at(xr, originY + y, zr), fillerSnow);
                    } else {
                        int copyFrom = (int)Math.floor((double)y * scale);
                        BlockState block = this.session.getBlock(BlockVector3.at(xr, originY + copyFrom, zr));
                        this.session.setBlock(BlockVector3.at(xr, originY + y, zr), block);
                    }
                    ++blocksChanged;
                }
                this.setSnowLayer(xr, zr, newHeight);
                ++blocksChanged;
                y = (int)Math.floor(newHeight + 1.0f);
                while ((double)y <= Math.floor(curHeight)) {
                    this.session.setBlock(BlockVector3.at(xr, y, zr), fillerAir);
                    ++blocksChanged;
                    ++y;
                }
            }
        }
        return blocksChanged;
    }

    private void setSnowLayer(int x, int z, float newHeight) throws MaxChangedBlocksException {
        int y = (int)Math.floor(newHeight);
        int numOfLayers = (int)((newHeight - (float)y) * 8.0f) + 1;
        this.session.setBlock(BlockVector3.at(x, y, z), BlockTypes.SNOW.getState((Map<Property<?>, Object>)ImmutableMap.of(this.layers, (Object)numOfLayers)));
    }
}

