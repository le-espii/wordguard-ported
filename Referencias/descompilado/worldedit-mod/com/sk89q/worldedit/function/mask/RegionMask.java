/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 */
package com.sk89q.worldedit.function.mask;

import com.google.common.base.Preconditions;
import com.sk89q.worldedit.function.mask.AbstractMask;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.Region;

public class RegionMask
extends AbstractMask {
    private Region region;

    public RegionMask(Region region) {
        this.setRegion(region);
    }

    public Region getRegion() {
        return this.region;
    }

    public void setRegion(Region region) {
        Preconditions.checkNotNull((Object)region);
        this.region = region;
    }

    @Override
    public boolean test(BlockVector3 vector) {
        return this.region.contains(vector);
    }
}

