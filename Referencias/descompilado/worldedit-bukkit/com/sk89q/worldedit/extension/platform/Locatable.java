/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.extension.platform;

import com.sk89q.worldedit.extent.Extent;
import com.sk89q.worldedit.math.Vector3;
import com.sk89q.worldedit.util.Location;

public interface Locatable {
    public Location getLocation();

    default public Location getBlockLocation() {
        Location location = this.getLocation();
        return location.setPosition(location.toVector().floor());
    }

    public boolean setLocation(Location var1);

    @Deprecated
    default public void setPosition(Vector3 pos) {
        this.trySetPosition(pos);
    }

    default public boolean trySetPosition(Vector3 pos) {
        return this.setLocation(new Location(this.getExtent(), pos));
    }

    public Extent getExtent();
}

