/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.Iterators
 */
package com.sk89q.worldedit.regions;

import com.google.common.base.Preconditions;
import com.google.common.collect.Iterators;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.AbstractRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.regions.RegionOperationException;
import com.sk89q.worldedit.util.formatting.text.TranslatableComponent;
import com.sk89q.worldedit.world.World;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class RegionIntersection
extends AbstractRegion {
    private final List<Region> regions = new ArrayList<Region>();

    public RegionIntersection(List<Region> regions) {
        this(null, regions);
    }

    public RegionIntersection(Region ... regions) {
        this((World)null, regions);
    }

    public RegionIntersection(World world, List<Region> regions) {
        super(world);
        Preconditions.checkNotNull(regions);
        Preconditions.checkArgument((!regions.isEmpty() ? 1 : 0) != 0, (Object)"empty region list is not supported");
        this.regions.addAll(regions);
    }

    public RegionIntersection(World world, Region ... regions) {
        super(world);
        Preconditions.checkNotNull((Object)regions);
        Preconditions.checkArgument((regions.length > 0 ? 1 : 0) != 0, (Object)"empty region list is not supported");
        Collections.addAll(this.regions, regions);
    }

    @Override
    public BlockVector3 getMinimumPoint() {
        BlockVector3 minimum = this.regions.get(0).getMinimumPoint();
        for (int i = 1; i < this.regions.size(); ++i) {
            minimum = this.regions.get(i).getMinimumPoint().getMinimum(minimum);
        }
        return minimum;
    }

    @Override
    public BlockVector3 getMaximumPoint() {
        BlockVector3 maximum = this.regions.get(0).getMaximumPoint();
        for (int i = 1; i < this.regions.size(); ++i) {
            maximum = this.regions.get(i).getMaximumPoint().getMaximum(maximum);
        }
        return maximum;
    }

    @Override
    public void expand(BlockVector3 ... changes) throws RegionOperationException {
        Preconditions.checkNotNull((Object)changes);
        throw new RegionOperationException(TranslatableComponent.of("worldedit.selection.intersection.error.cannot-expand"));
    }

    @Override
    public void contract(BlockVector3 ... changes) throws RegionOperationException {
        Preconditions.checkNotNull((Object)changes);
        throw new RegionOperationException(TranslatableComponent.of("worldedit.selection.intersection.error.cannot-contract"));
    }

    @Override
    public boolean contains(BlockVector3 position) {
        Preconditions.checkNotNull((Object)((Object)position));
        for (Region region : this.regions) {
            if (!region.contains(position)) continue;
            return true;
        }
        return false;
    }

    @Override
    public Iterator<BlockVector3> iterator() {
        return Iterators.concat((Iterator)Iterators.transform(this.regions.iterator(), Iterable::iterator));
    }
}

