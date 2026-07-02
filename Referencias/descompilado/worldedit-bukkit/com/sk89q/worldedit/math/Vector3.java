/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ComparisonChain
 */
package com.sk89q.worldedit.math;

import com.google.common.base.Preconditions;
import com.google.common.collect.ComparisonChain;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.math.Vector2;
import java.util.Comparator;

public record Vector3(double x, double y, double z) {
    public static final Vector3 ZERO = new Vector3(0.0, 0.0, 0.0);
    public static final Vector3 UNIT_X = new Vector3(1.0, 0.0, 0.0);
    public static final Vector3 UNIT_Y = new Vector3(0.0, 1.0, 0.0);
    public static final Vector3 UNIT_Z = new Vector3(0.0, 0.0, 1.0);
    public static final Vector3 ONE = new Vector3(1.0, 1.0, 1.0);

    public static Vector3 at(double x, double y, double z) {
        int yTrunc = (int)y;
        switch (yTrunc) {
            case 0: {
                if (x != 0.0 || y != 0.0 || z != 0.0) break;
                return ZERO;
            }
            case 1: {
                if (x != 1.0 || y != 1.0 || z != 1.0) break;
                return ONE;
            }
        }
        return new Vector3(x, y, z);
    }

    public static Comparator<Vector3> sortByCoordsYzx() {
        return YzxOrderComparator.YZX_ORDER;
    }

    public int blockX() {
        return (int)Math.floor(this.x);
    }

    @Deprecated(forRemoval=true)
    public double getX() {
        return this.x;
    }

    public Vector3 withX(double x) {
        return Vector3.at(x, this.y, this.z);
    }

    public int blockY() {
        return (int)Math.floor(this.y);
    }

    @Deprecated(forRemoval=true)
    public double getY() {
        return this.y;
    }

    public Vector3 withY(double y) {
        return Vector3.at(this.x, y, this.z);
    }

    public int blockZ() {
        return (int)Math.floor(this.z);
    }

    @Deprecated(forRemoval=true)
    public double getZ() {
        return this.z;
    }

    public Vector3 withZ(double z) {
        return Vector3.at(this.x, this.y, z);
    }

    public Vector3 add(Vector3 other) {
        return this.add(other.x, other.y, other.z);
    }

    public Vector3 add(double x, double y, double z) {
        return Vector3.at(this.x + x, this.y + y, this.z + z);
    }

    public Vector3 add(Vector3 ... others) {
        double newX = this.x;
        double newY = this.y;
        double newZ = this.z;
        for (Vector3 other : others) {
            newX += other.x;
            newY += other.y;
            newZ += other.z;
        }
        return Vector3.at(newX, newY, newZ);
    }

    public Vector3 subtract(Vector3 other) {
        return this.subtract(other.x, other.y, other.z);
    }

    public Vector3 subtract(double x, double y, double z) {
        return Vector3.at(this.x - x, this.y - y, this.z - z);
    }

    public Vector3 subtract(Vector3 ... others) {
        double newX = this.x;
        double newY = this.y;
        double newZ = this.z;
        for (Vector3 other : others) {
            newX -= other.x;
            newY -= other.y;
            newZ -= other.z;
        }
        return Vector3.at(newX, newY, newZ);
    }

    public Vector3 multiply(Vector3 other) {
        return this.multiply(other.x, other.y, other.z);
    }

    public Vector3 multiply(double x, double y, double z) {
        return Vector3.at(this.x * x, this.y * y, this.z * z);
    }

    public Vector3 multiply(Vector3 ... others) {
        double newX = this.x;
        double newY = this.y;
        double newZ = this.z;
        for (Vector3 other : others) {
            newX *= other.x;
            newY *= other.y;
            newZ *= other.z;
        }
        return Vector3.at(newX, newY, newZ);
    }

    public Vector3 multiply(double n) {
        return this.multiply(n, n, n);
    }

    public Vector3 divide(Vector3 other) {
        return this.divide(other.x, other.y, other.z);
    }

    public Vector3 divide(double x, double y, double z) {
        return Vector3.at(this.x / x, this.y / y, this.z / z);
    }

    public Vector3 divide(double n) {
        return this.divide(n, n, n);
    }

    public double length() {
        return Math.sqrt(this.lengthSq());
    }

    public double lengthSq() {
        return this.x * this.x + this.y * this.y + this.z * this.z;
    }

    public double distance(Vector3 other) {
        return Math.sqrt(this.distanceSq(other));
    }

    public double distanceSq(Vector3 other) {
        double dx = other.x - this.x;
        double dy = other.y - this.y;
        double dz = other.z - this.z;
        return dx * dx + dy * dy + dz * dz;
    }

    public Vector3 normalize() {
        return this.divide(this.length());
    }

    public double dot(Vector3 other) {
        return this.x * other.x + this.y * other.y + this.z * other.z;
    }

    public Vector3 cross(Vector3 other) {
        return new Vector3(this.y * other.z - this.z * other.y, this.z * other.x - this.x * other.z, this.x * other.y - this.y * other.x);
    }

    public boolean containedWithin(Vector3 min, Vector3 max) {
        return this.x >= min.x && this.x <= max.x && this.y >= min.y && this.y <= max.y && this.z >= min.z && this.z <= max.z;
    }

    public Vector3 clampY(int min, int max) {
        Preconditions.checkArgument((min <= max ? 1 : 0) != 0, (Object)"minimum cannot be greater than maximum");
        if (this.y < (double)min) {
            return Vector3.at(this.x, min, this.z);
        }
        if (this.y > (double)max) {
            return Vector3.at(this.x, max, this.z);
        }
        return this;
    }

    public Vector3 floor() {
        return Vector3.at(Math.floor(this.x), Math.floor(this.y), Math.floor(this.z));
    }

    public Vector3 ceil() {
        return Vector3.at(Math.ceil(this.x), Math.ceil(this.y), Math.ceil(this.z));
    }

    public Vector3 round() {
        return Vector3.at(Math.floor(this.x + 0.5), Math.floor(this.y + 0.5), Math.floor(this.z + 0.5));
    }

    public Vector3 abs() {
        return Vector3.at(Math.abs(this.x), Math.abs(this.y), Math.abs(this.z));
    }

    public Vector3 transform2D(double angle, double aboutX, double aboutZ, double translateX, double translateZ) {
        angle = Math.toRadians(angle);
        double x = this.x - aboutX;
        double z = this.z - aboutZ;
        double cos = Math.cos(angle);
        double sin = Math.sin(angle);
        double x2 = x * cos - z * sin;
        double z2 = x * sin + z * cos;
        return new Vector3(x2 + aboutX + translateX, this.y, z2 + aboutZ + translateZ);
    }

    public double toPitch() {
        double x = this.x;
        double z = this.z;
        if (x == 0.0 && z == 0.0) {
            return this.y > 0.0 ? -90.0 : 90.0;
        }
        double x2 = x * x;
        double z2 = z * z;
        double xz = Math.sqrt(x2 + z2);
        return Math.toDegrees(Math.atan(-this.y / xz));
    }

    public double toYaw() {
        double x = this.x;
        double z = this.z;
        double t = Math.atan2(-x, z);
        double tau = Math.PI * 2;
        return Math.toDegrees((t + tau) % tau);
    }

    public Vector3 getMinimum(Vector3 v2) {
        return new Vector3(Math.min(this.x, v2.x), Math.min(this.y, v2.y), Math.min(this.z, v2.z));
    }

    public Vector3 getMaximum(Vector3 v2) {
        return new Vector3(Math.max(this.x, v2.x), Math.max(this.y, v2.y), Math.max(this.z, v2.z));
    }

    public static BlockVector3 toBlockPoint(double x, double y, double z) {
        return BlockVector3.at(x, y, z);
    }

    public BlockVector3 toBlockPoint() {
        return Vector3.toBlockPoint(this.x, this.y, this.z);
    }

    public Vector2 toVector2() {
        return Vector2.at(this.x, this.z);
    }

    public String toString() {
        return "(" + this.x + ", " + this.y + ", " + this.z + ")";
    }

    public String toParserString() {
        return this.x + "," + this.y + "," + this.z;
    }

    private static final class YzxOrderComparator {
        private static final Comparator<Vector3> YZX_ORDER = (a, b) -> ComparisonChain.start().compare(a.y, b.y).compare(a.z, b.z).compare(a.x, b.x).result();

        private YzxOrderComparator() {
        }
    }
}

