/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.regions.factory;

import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.Region;

public interface RegionFactory {
    public Region createCenteredAt(BlockVector3 var1, double var2);
}

