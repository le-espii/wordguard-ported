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
import com.sk89q.worldedit.math.convolution.HeightMap;
import com.sk89q.worldedit.math.convolution.HeightMapFilter;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.util.Location;
import javax.annotation.Nullable;

public class SmoothBrush
implements Brush {
    private final Mask mask;
    private final int iterations;

    public SmoothBrush(int iterations) {
        this(iterations, null);
    }

    public SmoothBrush(int iterations, @Nullable Mask mask) {
        this.iterations = iterations;
        this.mask = mask;
    }

    @Override
    public void build(EditSession editSession, BlockVector3 position, Pattern pattern, double size) throws MaxChangedBlocksException {
        Vector3 posDouble = position.toVector3();
        Location min = new Location(editSession.getWorld(), posDouble.subtract(size, size, size));
        BlockVector3 max = posDouble.add(size, size + 10.0, size).toBlockPoint();
        CuboidRegion region = new CuboidRegion(editSession.getWorld(), min.toVector().toBlockPoint(), max);
        HeightMap heightMap = new HeightMap(editSession, region, this.mask);
        HeightMapFilter filter = new HeightMapFilter(new GaussianKernel(5, 1.0));
        heightMap.applyFilter(filter, this.iterations);
    }
}

