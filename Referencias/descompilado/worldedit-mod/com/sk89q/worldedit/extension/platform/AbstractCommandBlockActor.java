/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.extension.platform;

import com.sk89q.worldedit.extension.platform.AbstractNonPlayerActor;
import com.sk89q.worldedit.extension.platform.Locatable;
import com.sk89q.worldedit.extent.Extent;
import com.sk89q.worldedit.util.Location;

public abstract class AbstractCommandBlockActor
extends AbstractNonPlayerActor
implements Locatable {
    protected static final String UUID_PREFIX = "CMD";
    private final Location location;

    public AbstractCommandBlockActor(Location location) {
        this.location = location;
    }

    @Override
    public Location getLocation() {
        return this.location;
    }

    @Override
    public boolean setLocation(Location location) {
        return false;
    }

    @Override
    public Extent getExtent() {
        return this.location.getExtent();
    }
}

