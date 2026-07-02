/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.regions.shape;

import com.sk89q.worldedit.extent.Extent;
import com.sk89q.worldedit.internal.expression.ExpressionEnvironment;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.math.Vector3;
import com.sk89q.worldedit.world.registry.LegacyMapper;

public class WorldEditExpressionEnvironment
implements ExpressionEnvironment {
    private final Vector3 unit;
    private final Vector3 zero2;
    private Vector3 current = Vector3.ZERO;
    private final Extent extent;

    public WorldEditExpressionEnvironment(Extent extent, Vector3 unit, Vector3 zero) {
        this.extent = extent;
        this.unit = unit;
        this.zero2 = zero.add(0.5, 0.5, 0.5);
    }

    public BlockVector3 toWorld(double x, double y, double z) {
        return Vector3.at(x, y, z).multiply(this.unit).add(this.zero2).toBlockPoint();
    }

    public Vector3 toWorldRel(double x, double y, double z) {
        return this.current.add(x, y, z);
    }

    private int getLegacy(BlockVector3 position, int index) {
        int[] legacy = LegacyMapper.getInstance().getLegacyFromBlock(this.extent.getBlock(position).toImmutableState());
        return legacy == null ? 0 : legacy[index];
    }

    @Override
    public int getBlockType(double x, double y, double z) {
        return this.getLegacy(this.toWorld(x, y, z), 0);
    }

    @Override
    public int getBlockData(double x, double y, double z) {
        return this.getLegacy(this.toWorld(x, y, z), 1);
    }

    @Override
    public int getBlockTypeAbs(double x, double y, double z) {
        return this.getLegacy(BlockVector3.at(x, y, z), 0);
    }

    @Override
    public int getBlockDataAbs(double x, double y, double z) {
        return this.getLegacy(BlockVector3.at(x, y, z), 1);
    }

    @Override
    public int getBlockTypeRel(double x, double y, double z) {
        return this.getLegacy(this.toWorldRel(x, y, z).toBlockPoint(), 0);
    }

    @Override
    public int getBlockDataRel(double x, double y, double z) {
        return this.getLegacy(this.toWorldRel(x, y, z).toBlockPoint(), 1);
    }

    public void setCurrentBlock(Vector3 current) {
        this.current = current;
    }
}

