/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.regions;

import com.sk89q.worldedit.regions.AbstractRegion;
import com.sk89q.worldedit.regions.FlatRegion;
import com.sk89q.worldedit.world.World;

public abstract class AbstractFlatRegion
extends AbstractRegion
implements FlatRegion {
    protected AbstractFlatRegion(World world) {
        super(world);
    }

    @Override
    public int getMinimumY() {
        return this.getMinimumPoint().y();
    }

    @Override
    public int getMaximumY() {
        return this.getMaximumPoint().y();
    }
}

