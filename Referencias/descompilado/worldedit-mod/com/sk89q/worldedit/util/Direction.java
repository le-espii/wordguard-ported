/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.sk89q.worldedit.util;

import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.math.Vector3;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import javax.annotation.Nullable;

public enum Direction {
    NORTH(Vector3.at(0.0, 0.0, -1.0), Flag.CARDINAL),
    EAST(Vector3.at(1.0, 0.0, 0.0), Flag.CARDINAL),
    SOUTH(Vector3.at(0.0, 0.0, 1.0), Flag.CARDINAL),
    WEST(Vector3.at(-1.0, 0.0, 0.0), Flag.CARDINAL),
    UP(Vector3.at(0.0, 1.0, 0.0), Flag.UPRIGHT),
    DOWN(Vector3.at(0.0, -1.0, 0.0), Flag.UPRIGHT),
    NORTHEAST(Vector3.at(1.0, 0.0, -1.0), Flag.ORDINAL),
    NORTHWEST(Vector3.at(-1.0, 0.0, -1.0), Flag.ORDINAL),
    SOUTHEAST(Vector3.at(1.0, 0.0, 1.0), Flag.ORDINAL),
    SOUTHWEST(Vector3.at(-1.0, 0.0, 1.0), Flag.ORDINAL),
    WEST_NORTHWEST(Vector3.at(-Math.cos(0.39269908169872414), 0.0, -Math.sin(0.39269908169872414)), Flag.SECONDARY_ORDINAL),
    WEST_SOUTHWEST(Vector3.at(-Math.cos(0.39269908169872414), 0.0, Math.sin(0.39269908169872414)), Flag.SECONDARY_ORDINAL),
    NORTH_NORTHWEST(Vector3.at(-Math.sin(0.39269908169872414), 0.0, -Math.cos(0.39269908169872414)), Flag.SECONDARY_ORDINAL),
    NORTH_NORTHEAST(Vector3.at(Math.sin(0.39269908169872414), 0.0, -Math.cos(0.39269908169872414)), Flag.SECONDARY_ORDINAL),
    EAST_NORTHEAST(Vector3.at(Math.cos(0.39269908169872414), 0.0, -Math.sin(0.39269908169872414)), Flag.SECONDARY_ORDINAL),
    EAST_SOUTHEAST(Vector3.at(Math.cos(0.39269908169872414), 0.0, Math.sin(0.39269908169872414)), Flag.SECONDARY_ORDINAL),
    SOUTH_SOUTHEAST(Vector3.at(Math.sin(0.39269908169872414), 0.0, Math.cos(0.39269908169872414)), Flag.SECONDARY_ORDINAL),
    SOUTH_SOUTHWEST(Vector3.at(-Math.sin(0.39269908169872414), 0.0, Math.cos(0.39269908169872414)), Flag.SECONDARY_ORDINAL);

    private final Vector3 direction;
    private final int flags;
    private final BlockVector3 blockPoint;

    private Direction(Vector3 vector, int flags) {
        this.blockPoint = vector.toBlockPoint();
        this.direction = vector.normalize();
        this.flags = flags;
    }

    public boolean isCardinal() {
        return (this.flags & Flag.CARDINAL) > 0;
    }

    public boolean isOrdinal() {
        return (this.flags & Flag.ORDINAL) > 0;
    }

    public boolean isSecondaryOrdinal() {
        return (this.flags & Flag.SECONDARY_ORDINAL) > 0;
    }

    public boolean isUpright() {
        return (this.flags & Flag.UPRIGHT) > 0;
    }

    public Vector3 toVector() {
        return this.direction;
    }

    public BlockVector3 toBlockVector() {
        return this.blockPoint;
    }

    @Nullable
    public static Direction findClosest(Vector3 vector, int flags) {
        if ((flags & Flag.UPRIGHT) == 0) {
            vector = vector.withY(0.0);
        }
        vector = vector.normalize();
        Direction closest = null;
        double closestDot = -2.0;
        for (Direction direction : Direction.values()) {
            double dot;
            if ((~flags & direction.flags) > 0 || !((dot = direction.toVector().dot(vector)) >= closestDot)) continue;
            closest = direction;
            closestDot = dot;
        }
        return closest;
    }

    public static List<Direction> valuesOf(int flags) {
        ArrayList<Direction> directions = new ArrayList<Direction>();
        for (Direction direction : Direction.values()) {
            if ((~flags & direction.flags) != 0) continue;
            directions.add(direction);
        }
        return directions;
    }

    public static Optional<Direction> fromRotationIndex(int rotation) {
        return switch (rotation) {
            case 0 -> Optional.of(SOUTH);
            case 1 -> Optional.of(SOUTH_SOUTHWEST);
            case 2 -> Optional.of(SOUTHWEST);
            case 3 -> Optional.of(WEST_SOUTHWEST);
            case 4 -> Optional.of(WEST);
            case 5 -> Optional.of(WEST_NORTHWEST);
            case 6 -> Optional.of(NORTHWEST);
            case 7 -> Optional.of(NORTH_NORTHWEST);
            case 8 -> Optional.of(NORTH);
            case 9 -> Optional.of(NORTH_NORTHEAST);
            case 10 -> Optional.of(NORTHEAST);
            case 11 -> Optional.of(EAST_NORTHEAST);
            case 12 -> Optional.of(EAST);
            case 13 -> Optional.of(EAST_SOUTHEAST);
            case 14 -> Optional.of(SOUTHEAST);
            case 15 -> Optional.of(SOUTH_SOUTHEAST);
            default -> Optional.empty();
        };
    }

    public OptionalInt toRotationIndex() {
        return switch (this.ordinal()) {
            case 2 -> OptionalInt.of(0);
            case 17 -> OptionalInt.of(1);
            case 9 -> OptionalInt.of(2);
            case 11 -> OptionalInt.of(3);
            case 3 -> OptionalInt.of(4);
            case 10 -> OptionalInt.of(5);
            case 7 -> OptionalInt.of(6);
            case 12 -> OptionalInt.of(7);
            case 0 -> OptionalInt.of(8);
            case 13 -> OptionalInt.of(9);
            case 6 -> OptionalInt.of(10);
            case 14 -> OptionalInt.of(11);
            case 1 -> OptionalInt.of(12);
            case 15 -> OptionalInt.of(13);
            case 8 -> OptionalInt.of(14);
            case 16 -> OptionalInt.of(15);
            default -> OptionalInt.empty();
        };
    }

    public static final class Flag {
        public static int CARDINAL = 1;
        public static int ORDINAL = 2;
        public static int SECONDARY_ORDINAL = 4;
        public static int UPRIGHT = 8;
        public static int ALL = CARDINAL | ORDINAL | SECONDARY_ORDINAL | UPRIGHT;

        private Flag() {
        }
    }
}

