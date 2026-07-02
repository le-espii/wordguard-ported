/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.math;

import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.math.Vector2;
import com.sk89q.worldedit.math.Vector3;
import java.util.Comparator;

public record BlockVector2(int x, int z) {
    public static final BlockVector2 ZERO = new BlockVector2(0, 0);
    public static final BlockVector2 UNIT_X = new BlockVector2(1, 0);
    public static final BlockVector2 UNIT_Z = new BlockVector2(0, 1);
    public static final BlockVector2 ONE = new BlockVector2(1, 1);
    public static final Comparator<BlockVector2> COMPARING_GRID_ARRANGEMENT = Comparator.comparingInt(BlockVector2::z).thenComparingInt(BlockVector2::x);

    public static BlockVector2 at(double x, double z) {
        return BlockVector2.at((int)Math.floor(x), (int)Math.floor(z));
    }

    public static BlockVector2 at(int x, int z) {
        switch (x) {
            case 0: {
                if (z != 0) break;
                return ZERO;
            }
            case 1: {
                if (z != 1) break;
                return ONE;
            }
        }
        return new BlockVector2(x, z);
    }

    @Deprecated(forRemoval=true)
    public int getX() {
        return this.x;
    }

    @Deprecated(forRemoval=true)
    public int getBlockX() {
        return this.x;
    }

    public BlockVector2 withX(int x) {
        return BlockVector2.at(x, this.z);
    }

    @Deprecated(forRemoval=true)
    public int getZ() {
        return this.z;
    }

    @Deprecated(forRemoval=true)
    public int getBlockZ() {
        return this.z;
    }

    public BlockVector2 withZ(int z) {
        return BlockVector2.at(this.x, z);
    }

    public BlockVector2 add(BlockVector2 other) {
        return this.add(other.x, other.z);
    }

    public BlockVector2 add(int x, int z) {
        return BlockVector2.at(this.x + x, this.z + z);
    }

    public BlockVector2 add(BlockVector2 ... others) {
        int newX = this.x;
        int newZ = this.z;
        for (BlockVector2 other : others) {
            newX += other.x;
            newZ += other.z;
        }
        return BlockVector2.at(newX, newZ);
    }

    public BlockVector2 subtract(BlockVector2 other) {
        return this.subtract(other.x, other.z);
    }

    public BlockVector2 subtract(int x, int z) {
        return BlockVector2.at(this.x - x, this.z - z);
    }

    public BlockVector2 subtract(BlockVector2 ... others) {
        int newX = this.x;
        int newZ = this.z;
        for (BlockVector2 other : others) {
            newX -= other.x;
            newZ -= other.z;
        }
        return BlockVector2.at(newX, newZ);
    }

    public BlockVector2 multiply(BlockVector2 other) {
        return this.multiply(other.x, other.z);
    }

    public BlockVector2 multiply(int x, int z) {
        return BlockVector2.at(this.x * x, this.z * z);
    }

    public BlockVector2 multiply(BlockVector2 ... others) {
        int newX = this.x;
        int newZ = this.z;
        for (BlockVector2 other : others) {
            newX *= other.x;
            newZ *= other.z;
        }
        return BlockVector2.at(newX, newZ);
    }

    public BlockVector2 multiply(int n) {
        return this.multiply(n, n);
    }

    public BlockVector2 divide(BlockVector2 other) {
        return this.divide(other.x, other.z);
    }

    public BlockVector2 divide(int x, int z) {
        return BlockVector2.at(this.x / x, this.z / z);
    }

    public BlockVector2 divide(int n) {
        return this.divide(n, n);
    }

    public BlockVector2 shr(int x, int z) {
        return BlockVector2.at(this.x >> x, this.z >> z);
    }

    public BlockVector2 shr(int n) {
        return this.shr(n, n);
    }

    public double length() {
        return Math.sqrt(this.lengthSq());
    }

    public int lengthSq() {
        return this.x * this.x + this.z * this.z;
    }

    public double distance(BlockVector2 other) {
        return Math.sqrt(this.distanceSq(other));
    }

    public int distanceSq(BlockVector2 other) {
        int dx = other.x - this.x;
        int dz = other.z - this.z;
        return dx * dx + dz * dz;
    }

    public BlockVector2 normalize() {
        double len = this.length();
        double x = (double)this.x / len;
        double z = (double)this.z / len;
        return BlockVector2.at(x, z);
    }

    public int dot(BlockVector2 other) {
        return this.x * other.x + this.z * other.z;
    }

    public boolean containedWithin(BlockVector2 min, BlockVector2 max) {
        return this.x >= min.x && this.x <= max.x && this.z >= min.z && this.z <= max.z;
    }

    public BlockVector2 floor() {
        return this;
    }

    public BlockVector2 ceil() {
        return this;
    }

    public BlockVector2 round() {
        return this;
    }

    public BlockVector2 abs() {
        return BlockVector2.at(Math.abs(this.x), Math.abs(this.z));
    }

    public BlockVector2 transform2D(double angle, double aboutX, double aboutZ, double translateX, double translateZ) {
        angle = Math.toRadians(angle);
        double x = (double)this.x - aboutX;
        double z = (double)this.z - aboutZ;
        double cos = Math.cos(angle);
        double sin = Math.sin(angle);
        double x2 = x * cos - z * sin;
        double z2 = x * sin + z * cos;
        return BlockVector2.at(x2 + aboutX + translateX, z2 + aboutZ + translateZ);
    }

    public BlockVector2 getMinimum(BlockVector2 v2) {
        return new BlockVector2(Math.min(this.x, v2.x), Math.min(this.z, v2.z));
    }

    public BlockVector2 getMaximum(BlockVector2 v2) {
        return new BlockVector2(Math.max(this.x, v2.x), Math.max(this.z, v2.z));
    }

    public Vector2 toVector2() {
        return Vector2.at(this.x, this.z);
    }

    public Vector3 toVector3() {
        return this.toVector3(0.0);
    }

    public Vector3 toVector3(double y) {
        return Vector3.at(this.x, y, this.z);
    }

    public BlockVector3 toBlockVector3() {
        return this.toBlockVector3(0);
    }

    public BlockVector3 toBlockVector3(int y) {
        return BlockVector3.at(this.x, y, this.z);
    }

    public String toString() {
        return "(" + this.x + ", " + this.z + ")";
    }

    public String toParserString() {
        return this.x + "," + this.z;
    }
}

