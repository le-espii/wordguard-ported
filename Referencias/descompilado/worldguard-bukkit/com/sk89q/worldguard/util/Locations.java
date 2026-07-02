/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sk89q.worldedit.util.Location
 */
package com.sk89q.worldguard.util;

import com.sk89q.worldedit.util.Location;

public final class Locations {
    private Locations() {
    }

    public static boolean isDifferentBlock(Location a, Location b) {
        return a.getBlockX() != b.getBlockX() || a.getBlockY() != b.getBlockY() || a.getBlockZ() != b.getBlockZ();
    }
}

