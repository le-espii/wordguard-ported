/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldguard.protection.regions;

public enum RegionType {
    CUBOID("cuboid"),
    POLYGON("poly2d"),
    GLOBAL("global");

    private final String name;

    private RegionType(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }
}

