/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.sk89q.worldedit.extent;

import com.sk89q.worldedit.entity.BaseEntity;
import com.sk89q.worldedit.entity.Entity;
import com.sk89q.worldedit.extent.InputExtent;
import com.sk89q.worldedit.extent.OutputExtent;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.util.Location;
import java.util.List;
import javax.annotation.Nullable;

public interface Extent
extends InputExtent,
OutputExtent {
    public BlockVector3 getMinimumPoint();

    public BlockVector3 getMaximumPoint();

    public List<? extends Entity> getEntities(Region var1);

    public List<? extends Entity> getEntities();

    @Nullable
    public Entity createEntity(Location var1, BaseEntity var2);
}

