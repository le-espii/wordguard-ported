/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 */
package com.sk89q.worldedit.util;

import com.google.common.base.Preconditions;
import com.sk89q.worldedit.extent.Extent;
import com.sk89q.worldedit.math.Vector3;
import com.sk89q.worldedit.util.Direction;

public class Location {
    private final Extent extent;
    private final Vector3 position;
    private final float pitch;
    private final float yaw;

    public Location(Extent extent) {
        this(extent, Vector3.ZERO, 0.0f, 90.0f);
    }

    public Location(Extent extent, double x, double y, double z) {
        this(extent, Vector3.at(x, y, z), 0.0f, 90.0f);
    }

    public Location(Extent extent, Vector3 position) {
        this(extent, position, 0.0f, 90.0f);
    }

    public Location(Extent extent, double x, double y, double z, Vector3 direction) {
        this(extent, Vector3.at(x, y, z), direction);
    }

    public Location(Extent extent, double x, double y, double z, float yaw, float pitch) {
        this(extent, Vector3.at(x, y, z), yaw, pitch);
    }

    public Location(Extent extent, Vector3 position, Vector3 direction) {
        this(extent, position, (float)direction.toYaw(), (float)direction.toPitch());
    }

    public Location(Extent extent, Vector3 position, float yaw, float pitch) {
        Preconditions.checkNotNull((Object)extent);
        Preconditions.checkNotNull((Object)((Object)position));
        this.extent = extent;
        this.position = position;
        this.pitch = pitch;
        this.yaw = yaw;
    }

    public Extent getExtent() {
        return this.extent;
    }

    public Location setExtent(Extent extent) {
        return new Location(extent, this.position, this.getDirection());
    }

    public float getYaw() {
        return this.yaw;
    }

    public Location setYaw(float yaw) {
        return new Location(this.extent, this.position, yaw, this.pitch);
    }

    public float getPitch() {
        return this.pitch;
    }

    public Location setPitch(float pitch) {
        return new Location(this.extent, this.position, this.yaw, pitch);
    }

    public Location setDirection(float yaw, float pitch) {
        return new Location(this.extent, this.position, yaw, pitch);
    }

    public Vector3 getDirection() {
        if (Float.isNaN(this.getYaw()) && Float.isNaN(this.getPitch())) {
            return Vector3.ZERO;
        }
        double yaw = Math.toRadians(this.getYaw());
        double pitch = Math.toRadians(this.getPitch());
        double xz = Math.cos(pitch);
        return Vector3.at(-xz * Math.sin(yaw), -Math.sin(pitch), xz * Math.cos(yaw));
    }

    public Direction getDirectionEnum() {
        return Direction.findClosest(this.getDirection(), Direction.Flag.ALL);
    }

    public Location setDirection(Vector3 direction) {
        return new Location(this.extent, this.position, (float)direction.toYaw(), (float)direction.toPitch());
    }

    public Vector3 toVector() {
        return this.position;
    }

    public double getX() {
        return this.position.x();
    }

    public int getBlockX() {
        return this.position.blockX();
    }

    public Location setX(double x) {
        return new Location(this.extent, this.position.withX(x), this.yaw, this.pitch);
    }

    public double getY() {
        return this.position.y();
    }

    public int getBlockY() {
        return this.position.blockY();
    }

    public Location setY(double y) {
        return new Location(this.extent, this.position.withY(y), this.yaw, this.pitch);
    }

    public double getZ() {
        return this.position.z();
    }

    public int getBlockZ() {
        return this.position.blockZ();
    }

    public Location setZ(double z) {
        return new Location(this.extent, this.position.withZ(z), this.yaw, this.pitch);
    }

    public Location setPosition(Vector3 position) {
        return new Location(this.extent, position, this.yaw, this.pitch);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        Location location = (Location)o;
        if (Double.doubleToLongBits(this.pitch) != Double.doubleToLongBits(location.pitch)) {
            return false;
        }
        if (Double.doubleToLongBits(this.yaw) != Double.doubleToLongBits(location.yaw)) {
            return false;
        }
        if (!this.position.equals((Object)location.position)) {
            return false;
        }
        return this.extent.equals(location.extent);
    }

    public int hashCode() {
        int result = this.extent.hashCode();
        result = 31 * result + this.position.hashCode();
        result = 31 * result + Float.floatToIntBits(this.pitch);
        result = 31 * result + Float.floatToIntBits(this.yaw);
        return result;
    }
}

