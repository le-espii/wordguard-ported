/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.internal.helper;

import com.sk89q.worldedit.util.Direction;

public final class MCDirections {
    private MCDirections() {
    }

    public static Direction fromHanging(int i) {
        return switch (i) {
            case 0 -> Direction.DOWN;
            case 1 -> Direction.UP;
            case 2 -> Direction.NORTH;
            case 3 -> Direction.SOUTH;
            case 4 -> Direction.WEST;
            case 5 -> Direction.EAST;
            default -> Direction.DOWN;
        };
    }

    public static int toHanging(Direction direction) {
        return switch (direction) {
            case Direction.DOWN -> 0;
            case Direction.UP -> 1;
            case Direction.NORTH -> 2;
            case Direction.SOUTH -> 3;
            case Direction.WEST -> 4;
            case Direction.EAST -> 5;
            default -> 0;
        };
    }

    public static Direction fromPre13Hanging(int i) {
        return MCDirections.fromHorizontalHanging(i);
    }

    public static Direction fromHorizontalHanging(int i) {
        return switch (i) {
            case 0 -> Direction.SOUTH;
            case 1 -> Direction.WEST;
            case 2 -> Direction.NORTH;
            case 3 -> Direction.EAST;
            default -> Direction.NORTH;
        };
    }

    public static int toHorizontalHanging(Direction direction) {
        return switch (direction) {
            case Direction.SOUTH -> 0;
            case Direction.WEST -> 1;
            case Direction.NORTH -> 2;
            case Direction.EAST -> 3;
            default -> 0;
        };
    }

    public static int fromLegacyHanging(byte i) {
        return switch (i) {
            case 0 -> 2;
            case 1 -> 1;
            case 2 -> 0;
            default -> 3;
        };
    }

    public static Direction fromRotation(int i) {
        return switch (i) {
            case 0 -> Direction.SOUTH;
            case 1 -> Direction.SOUTH_SOUTHWEST;
            case 2 -> Direction.SOUTHWEST;
            case 3 -> Direction.WEST_SOUTHWEST;
            case 4 -> Direction.WEST;
            case 5 -> Direction.WEST_NORTHWEST;
            case 6 -> Direction.NORTHWEST;
            case 7 -> Direction.NORTH_NORTHWEST;
            case 8 -> Direction.NORTH;
            case 9 -> Direction.NORTH_NORTHEAST;
            case 10 -> Direction.NORTHEAST;
            case 11 -> Direction.EAST_NORTHEAST;
            case 12 -> Direction.EAST;
            case 13 -> Direction.EAST_SOUTHEAST;
            case 14 -> Direction.SOUTHEAST;
            case 15 -> Direction.SOUTH_SOUTHEAST;
            default -> Direction.NORTH;
        };
    }

    public static int toRotation(Direction direction) {
        return switch (direction) {
            case Direction.SOUTH -> 0;
            case Direction.SOUTH_SOUTHWEST -> 1;
            case Direction.SOUTHWEST -> 2;
            case Direction.WEST_SOUTHWEST -> 3;
            case Direction.WEST -> 4;
            case Direction.WEST_NORTHWEST -> 5;
            case Direction.NORTHWEST -> 6;
            case Direction.NORTH_NORTHWEST -> 7;
            case Direction.NORTH -> 8;
            case Direction.NORTH_NORTHEAST -> 9;
            case Direction.NORTHEAST -> 10;
            case Direction.EAST_NORTHEAST -> 11;
            case Direction.EAST -> 12;
            case Direction.EAST_SOUTHEAST -> 13;
            case Direction.SOUTHEAST -> 14;
            case Direction.SOUTH_SOUTHEAST -> 15;
            default -> 0;
        };
    }
}

