/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.math;

import com.sk89q.worldedit.math.BlockVector2;
import com.sk89q.worldedit.math.Vector3;

public record Vector2(double x, double z) {
    public static final Vector2 ZERO = new Vector2(0.0, 0.0);
    public static final Vector2 UNIT_X = new Vector2(1.0, 0.0);
    public static final Vector2 UNIT_Z = new Vector2(0.0, 1.0);
    public static final Vector2 ONE = new Vector2(1.0, 1.0);

    public static Vector2 at(double x, double z) {
        int xTrunc = (int)x;
        switch (xTrunc) {
            case 0: {
                if (x != 0.0 || z != 0.0) break;
                return ZERO;
            }
            case 1: {
                if (x != 1.0 || z != 1.0) break;
                return ONE;
            }
        }
        return new Vector2(x, z);
    }

    @Deprecated(forRemoval=true)
    public double getX() {
        return this.x;
    }

    public int blockX() {
        return (int)Math.floor(this.x);
    }

    public Vector2 withX(double x) {
        return Vector2.at(x, this.z);
    }

    public int blockZ() {
        return (int)Math.floor(this.z);
    }

    @Deprecated(forRemoval=true)
    public double getZ() {
        return this.z;
    }

    public Vector2 withZ(double z) {
        return Vector2.at(this.x, z);
    }

    public Vector2 add(Vector2 other) {
        return this.add(other.x, other.z);
    }

    public Vector2 add(double x, double z) {
        return Vector2.at(this.x + x, this.z + z);
    }

    public Vector2 add(Vector2 ... others) {
        double newX = this.x;
        double newZ = this.z;
        for (Vector2 other : others) {
            newX += other.x;
            newZ += other.z;
        }
        return Vector2.at(newX, newZ);
    }

    public Vector2 subtract(Vector2 other) {
        return this.subtract(other.x, other.z);
    }

    public Vector2 subtract(double x, double z) {
        return Vector2.at(this.x - x, this.z - z);
    }

    public Vector2 subtract(Vector2 ... others) {
        double newX = this.x;
        double newZ = this.z;
        for (Vector2 other : others) {
            newX -= other.x;
            newZ -= other.z;
        }
        return Vector2.at(newX, newZ);
    }

    public Vector2 multiply(Vector2 other) {
        return this.multiply(other.x, other.z);
    }

    public Vector2 multiply(double x, double z) {
        return Vector2.at(this.x * x, this.z * z);
    }

    public Vector2 multiply(Vector2 ... others) {
        double newX = this.x;
        double newZ = this.z;
        for (Vector2 other : others) {
            newX *= other.x;
            newZ *= other.z;
        }
        return Vector2.at(newX, newZ);
    }

    public Vector2 multiply(double n) {
        return this.multiply(n, n);
    }

    public Vector2 divide(Vector2 other) {
        return this.divide(other.x, other.z);
    }

    public Vector2 divide(double x, double z) {
        return Vector2.at(this.x / x, this.z / z);
    }

    public Vector2 divide(double n) {
        return this.divide(n, n);
    }

    public double length() {
        return Math.sqrt(this.lengthSq());
    }

    public double lengthSq() {
        return this.x * this.x + this.z * this.z;
    }

    public double distance(Vector2 other) {
        return Math.sqrt(this.distanceSq(other));
    }

    public double distanceSq(Vector2 other) {
        double dx = other.x - this.x;
        double dz = other.z - this.z;
        return dx * dx + dz * dz;
    }

    public Vector2 normalize() {
        return this.divide(this.length());
    }

    public double dot(Vector2 other) {
        return this.x * other.x + this.z * other.z;
    }

    public boolean containedWithin(Vector2 min, Vector2 max) {
        return this.x >= min.x && this.x <= max.x && this.z >= min.z && this.z <= max.z;
    }

    public Vector2 floor() {
        return Vector2.at(Math.floor(this.x), Math.floor(this.z));
    }

    public Vector2 ceil() {
        return Vector2.at(Math.ceil(this.x), Math.ceil(this.z));
    }

    public Vector2 round() {
        return Vector2.at(Math.floor(this.x + 0.5), Math.floor(this.z + 0.5));
    }

    public Vector2 abs() {
        return Vector2.at(Math.abs(this.x), Math.abs(this.z));
    }

    public Vector2 transform2D(double angle, double aboutX, double aboutZ, double translateX, double translateZ) {
        angle = Math.toRadians(angle);
        double x = this.x - aboutX;
        double z = this.z - aboutZ;
        double cos = Math.cos(angle);
        double sin = Math.sin(angle);
        double x2 = x * cos - z * sin;
        double z2 = x * sin + z * cos;
        return new Vector2(x2 + aboutX + translateX, z2 + aboutZ + translateZ);
    }

    public Vector2 getMinimum(Vector2 v2) {
        return new Vector2(Math.min(this.x, v2.x), Math.min(this.z, v2.z));
    }

    public Vector2 getMaximum(Vector2 v2) {
        return new Vector2(Math.max(this.x, v2.x), Math.max(this.z, v2.z));
    }

    public static BlockVector2 toBlockPoint(double x, double z) {
        return BlockVector2.at(x, z);
    }

    public BlockVector2 toBlockPoint() {
        return Vector2.toBlockPoint(this.x, this.z);
    }

    public Vector3 toVector3() {
        return this.toVector3(0.0);
    }

    public Vector3 toVector3(double y) {
        return Vector3.at(this.x, y, this.z);
    }

    public String toString() {
        return "(" + this.x + ", " + this.z + ")";
    }

    public String toParserString() {
        return this.x + "," + this.z;
    }
}

