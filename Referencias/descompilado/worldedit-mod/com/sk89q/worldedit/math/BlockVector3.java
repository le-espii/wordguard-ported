/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 */
package com.sk89q.worldedit.math;

import com.google.common.base.Preconditions;
import com.sk89q.worldedit.math.BitMath;
import com.sk89q.worldedit.math.BlockVector2;
import com.sk89q.worldedit.math.Vector3;
import java.util.Comparator;

public record BlockVector3(int x, int y, int z) {
    public static final BlockVector3 ZERO = new BlockVector3(0, 0, 0);
    public static final BlockVector3 UNIT_X = new BlockVector3(1, 0, 0);
    public static final BlockVector3 UNIT_Y = new BlockVector3(0, 1, 0);
    public static final BlockVector3 UNIT_Z = new BlockVector3(0, 0, 1);
    public static final BlockVector3 UNIT_MINUS_X = new BlockVector3(-1, 0, 0);
    public static final BlockVector3 UNIT_MINUS_Y = new BlockVector3(0, -1, 0);
    public static final BlockVector3 UNIT_MINUS_Z = new BlockVector3(0, 0, -1);
    public static final BlockVector3 ONE = new BlockVector3(1, 1, 1);
    private static final int WORLD_XZ_MINMAX = 30000000;
    private static final int WORLD_Y_MIN = -2048;
    private static final int WORLD_Y_MAX = 2047;
    private static final long BITS_26 = BitMath.mask(26);
    private static final long BITS_12 = BitMath.mask(12);

    public static BlockVector3 at(double x, double y, double z) {
        return BlockVector3.at((int)Math.floor(x), (int)Math.floor(y), (int)Math.floor(z));
    }

    public static BlockVector3 at(int x, int y, int z) {
        switch (y) {
            case 0: {
                if (x != 0 || z != 0) break;
                return ZERO;
            }
            case 1: {
                if (x != 1 || z != 1) break;
                return ONE;
            }
        }
        return new BlockVector3(x, y, z);
    }

    private static boolean isHorizontallyInBounds(int h) {
        return -30000000 <= h && h <= 30000000;
    }

    public static boolean isLongPackable(BlockVector3 location) {
        return BlockVector3.isHorizontallyInBounds(location.x) && BlockVector3.isHorizontallyInBounds(location.z) && -2048 <= location.y && location.y <= 2047;
    }

    public static void checkLongPackable(BlockVector3 location) {
        Preconditions.checkArgument((boolean)BlockVector3.isLongPackable(location), (String)"Location exceeds long packing limits: %s", (Object)((Object)location));
    }

    public static BlockVector3 fromLongPackedForm(long packed) {
        return BlockVector3.at(BitMath.unpackX(packed), BitMath.unpackY(packed), BitMath.unpackZ(packed));
    }

    public static Comparator<BlockVector3> sortByCoordsYzx() {
        return YzxOrderComparator.YZX_ORDER;
    }

    public long toLongPackedForm() {
        BlockVector3.checkLongPackable(this);
        return (long)this.x & BITS_26 | ((long)this.z & BITS_26) << 26 | ((long)this.y & BITS_12) << 52;
    }

    @Deprecated(forRemoval=true)
    public int getX() {
        return this.x;
    }

    @Deprecated(forRemoval=true)
    public int getBlockX() {
        return this.x;
    }

    public BlockVector3 withX(int x) {
        return BlockVector3.at(x, this.y, this.z);
    }

    @Deprecated(forRemoval=true)
    public int getY() {
        return this.y;
    }

    @Deprecated(forRemoval=true)
    public int getBlockY() {
        return this.y;
    }

    public BlockVector3 withY(int y) {
        return BlockVector3.at(this.x, y, this.z);
    }

    @Deprecated(forRemoval=true)
    public int getZ() {
        return this.z;
    }

    @Deprecated(forRemoval=true)
    public int getBlockZ() {
        return this.z;
    }

    public BlockVector3 withZ(int z) {
        return BlockVector3.at(this.x, this.y, z);
    }

    public BlockVector3 add(BlockVector3 other) {
        return this.add(other.x, other.y, other.z);
    }

    public BlockVector3 add(int x, int y, int z) {
        return BlockVector3.at(this.x + x, this.y + y, this.z + z);
    }

    public BlockVector3 add(BlockVector3 ... others) {
        int newX = this.x;
        int newY = this.y;
        int newZ = this.z;
        for (BlockVector3 other : others) {
            newX += other.x;
            newY += other.y;
            newZ += other.z;
        }
        return BlockVector3.at(newX, newY, newZ);
    }

    public BlockVector3 subtract(BlockVector3 other) {
        return this.subtract(other.x, other.y, other.z);
    }

    public BlockVector3 subtract(int x, int y, int z) {
        return BlockVector3.at(this.x - x, this.y - y, this.z - z);
    }

    public BlockVector3 subtract(BlockVector3 ... others) {
        int newX = this.x;
        int newY = this.y;
        int newZ = this.z;
        for (BlockVector3 other : others) {
            newX -= other.x;
            newY -= other.y;
            newZ -= other.z;
        }
        return BlockVector3.at(newX, newY, newZ);
    }

    public BlockVector3 multiply(BlockVector3 other) {
        return this.multiply(other.x, other.y, other.z);
    }

    public BlockVector3 multiply(int x, int y, int z) {
        return BlockVector3.at(this.x * x, this.y * y, this.z * z);
    }

    public BlockVector3 multiply(BlockVector3 ... others) {
        int newX = this.x;
        int newY = this.y;
        int newZ = this.z;
        for (BlockVector3 other : others) {
            newX *= other.x;
            newY *= other.y;
            newZ *= other.z;
        }
        return BlockVector3.at(newX, newY, newZ);
    }

    public BlockVector3 multiply(int n) {
        return this.multiply(n, n, n);
    }

    public BlockVector3 divide(BlockVector3 other) {
        return this.divide(other.x, other.y, other.z);
    }

    public BlockVector3 divide(int x, int y, int z) {
        return BlockVector3.at(this.x / x, this.y / y, this.z / z);
    }

    public BlockVector3 divide(int n) {
        return this.divide(n, n, n);
    }

    public BlockVector3 shr(int x, int y, int z) {
        return BlockVector3.at(this.x >> x, this.y >> y, this.z >> z);
    }

    public BlockVector3 shr(int n) {
        return this.shr(n, n, n);
    }

    public BlockVector3 shl(int x, int y, int z) {
        return BlockVector3.at(this.x << x, this.y << y, this.z << z);
    }

    public BlockVector3 shl(int n) {
        return this.shl(n, n, n);
    }

    public double length() {
        return Math.sqrt(this.lengthSq());
    }

    public int lengthSq() {
        return this.x * this.x + this.y * this.y + this.z * this.z;
    }

    public double distance(BlockVector3 other) {
        return Math.sqrt(this.distanceSq(other));
    }

    public int distanceSq(BlockVector3 other) {
        int dx = other.x - this.x;
        int dy = other.y - this.y;
        int dz = other.z - this.z;
        return dx * dx + dy * dy + dz * dz;
    }

    public BlockVector3 normalize() {
        double len = this.length();
        double x = (double)this.x / len;
        double y = (double)this.y / len;
        double z = (double)this.z / len;
        return BlockVector3.at(x, y, z);
    }

    public double dot(BlockVector3 other) {
        return this.x * other.x + this.y * other.y + this.z * other.z;
    }

    public BlockVector3 cross(BlockVector3 other) {
        return new BlockVector3(this.y * other.z - this.z * other.y, this.z * other.x - this.x * other.z, this.x * other.y - this.y * other.x);
    }

    public boolean containedWithin(BlockVector3 min, BlockVector3 max) {
        return this.x >= min.x && this.x <= max.x && this.y >= min.y && this.y <= max.y && this.z >= min.z && this.z <= max.z;
    }

    public BlockVector3 clampY(int min, int max) {
        Preconditions.checkArgument((min <= max ? 1 : 0) != 0, (Object)"minimum cannot be greater than maximum");
        if (this.y < min) {
            return BlockVector3.at(this.x, min, this.z);
        }
        if (this.y > max) {
            return BlockVector3.at(this.x, max, this.z);
        }
        return this;
    }

    public BlockVector3 floor() {
        return this;
    }

    public BlockVector3 ceil() {
        return this;
    }

    public BlockVector3 round() {
        return this;
    }

    public BlockVector3 abs() {
        return BlockVector3.at(Math.abs(this.x), Math.abs(this.y), Math.abs(this.z));
    }

    public BlockVector3 transform2D(double angle, double aboutX, double aboutZ, double translateX, double translateZ) {
        angle = Math.toRadians(angle);
        double x = (double)this.x - aboutX;
        double z = (double)this.z - aboutZ;
        double cos = Math.cos(angle);
        double sin = Math.sin(angle);
        double x2 = x * cos - z * sin;
        double z2 = x * sin + z * cos;
        return BlockVector3.at(x2 + aboutX + translateX, (double)this.y, z2 + aboutZ + translateZ);
    }

    public double toPitch() {
        double x = this.x;
        double z = this.z;
        if (x == 0.0 && z == 0.0) {
            return this.y > 0 ? -90.0 : 90.0;
        }
        double x2 = x * x;
        double z2 = z * z;
        double xz = Math.sqrt(x2 + z2);
        return Math.toDegrees(Math.atan((double)(-this.y) / xz));
    }

    public double toYaw() {
        double x = this.x;
        double z = this.z;
        double t = Math.atan2(-x, z);
        double tau = Math.PI * 2;
        return Math.toDegrees((t + tau) % tau);
    }

    public BlockVector3 getMinimum(BlockVector3 v2) {
        return new BlockVector3(Math.min(this.x, v2.x), Math.min(this.y, v2.y), Math.min(this.z, v2.z));
    }

    public BlockVector3 getMaximum(BlockVector3 v2) {
        return new BlockVector3(Math.max(this.x, v2.x), Math.max(this.y, v2.y), Math.max(this.z, v2.z));
    }

    public BlockVector2 toBlockVector2() {
        return BlockVector2.at(this.x, this.z);
    }

    public Vector3 toVector3() {
        return Vector3.at(this.x, this.y, this.z);
    }

    public String toString() {
        return "(" + this.x + ", " + this.y + ", " + this.z + ")";
    }

    public String toParserString() {
        return this.x + "," + this.y + "," + this.z;
    }

    private static final class YzxOrderComparator {
        private static final Comparator<BlockVector3> YZX_ORDER = Comparator.comparingInt(BlockVector3::y).thenComparingInt(BlockVector3::z).thenComparingInt(BlockVector3::x);

        private YzxOrderComparator() {
        }
    }
}

