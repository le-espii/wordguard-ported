/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.sk89q.worldedit.command.tool.brush;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.command.tool.brush.Brush;
import com.sk89q.worldedit.function.mask.Mask;
import com.sk89q.worldedit.function.pattern.Pattern;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.math.Vector3;
import com.sk89q.worldedit.math.convolution.GaussianKernel;
import com.sk89q.worldedit.math.convolution.HeightMapFilter;
import com.sk89q.worldedit.math.convolution.SnowHeightMap;
import com.sk89q.worldedit.regions.CuboidRegion;
import javax.annotation.Nullable;

public class SnowSmoothBrush
implements Brush {
    private final Mask mask;
    private final int iterations;
    private final int snowBlockLayer;

    public SnowSmoothBrush(int iterations) {
        this(iterations, null);
    }

    public SnowSmoothBrush(int iterations, @Nullable Mask mask) {
        this(iterations, 1, mask);
    }

    public SnowSmoothBrush(int iterations, int snowBlockLayer, @Nullable Mask mask) {
        this.iterations = iterations;
        this.mask = mask;
        this.snowBlockLayer = snowBlockLayer;
    }

    @Override
    public void build(EditSession editSession, BlockVector3 position, Pattern pattern, double size) throws MaxChangedBlocksException {
        Vector3 posDouble = position.toVector3();
        BlockVector3 min = posDouble.subtract(size, size, size).toBlockPoint();
        BlockVector3 max = posDouble.add(size, size + 10.0, size).toBlockPoint();
        CuboidRegion region = new CuboidRegion(editSession.getWorld(), min, max);
        SnowHeightMap heightMap = new SnowHeightMap(editSession, region, this.mask);
        HeightMapFilter filter = new HeightMapFilter(new GaussianKernel(10, 1.0));
        float[] data = heightMap.applyFilter(filter, this.iterations);
        heightMap.applyChanges(data, this.snowBlockLayer);
    }
}

