/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  javax.annotation.Nullable
 */
package com.sk89q.worldedit.regions;

import com.google.common.base.Preconditions;
import com.sk89q.worldedit.math.BlockVector2;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.math.Vector3;
import com.sk89q.worldedit.math.transform.Identity;
import com.sk89q.worldedit.math.transform.Transform;
import com.sk89q.worldedit.regions.AbstractRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.regions.RegionOperationException;
import com.sk89q.worldedit.util.formatting.text.TranslatableComponent;
import com.sk89q.worldedit.world.World;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;

public class TransformRegion
extends AbstractRegion {
    private final Region region;
    private Transform transform = new Identity();

    public TransformRegion(Region region, Transform transform) {
        this(null, region, transform);
    }

    public TransformRegion(@Nullable World world, Region region, Transform transform) {
        super(world);
        Preconditions.checkNotNull((Object)region);
        Preconditions.checkNotNull((Object)transform);
        this.region = region;
        this.transform = transform;
    }

    public Region getRegion() {
        return this.region;
    }

    public Transform getTransform() {
        return this.transform;
    }

    public void setTransform(Transform transform) {
        Preconditions.checkNotNull((Object)transform);
        this.transform = transform;
    }

    @Override
    public BlockVector3 getMinimumPoint() {
        return this.transform.apply(this.region.getMinimumPoint().toVector3()).toBlockPoint();
    }

    @Override
    public BlockVector3 getMaximumPoint() {
        return this.transform.apply(this.region.getMaximumPoint().toVector3()).toBlockPoint();
    }

    @Override
    public Vector3 getCenter() {
        return this.transform.apply(this.region.getCenter());
    }

    @Override
    public long getVolume() {
        return this.region.getVolume();
    }

    @Override
    public int getWidth() {
        return this.getMaximumPoint().subtract(this.getMinimumPoint()).x() + 1;
    }

    @Override
    public int getHeight() {
        return this.getMaximumPoint().subtract(this.getMinimumPoint()).y() + 1;
    }

    @Override
    public int getLength() {
        return this.getMaximumPoint().subtract(this.getMinimumPoint()).z() + 1;
    }

    @Override
    public void expand(BlockVector3 ... changes) throws RegionOperationException {
        throw new RegionOperationException(TranslatableComponent.of("worldedit.selection.transform.error.cannot-expand"));
    }

    @Override
    public void contract(BlockVector3 ... changes) throws RegionOperationException {
        throw new RegionOperationException(TranslatableComponent.of("worldedit.selection.transform.error.cannot-contract"));
    }

    @Override
    public void shift(BlockVector3 change) throws RegionOperationException {
        throw new RegionOperationException(TranslatableComponent.of("worldedit.selection.transform.error.cannot-change"));
    }

    @Override
    public boolean contains(BlockVector3 position) {
        return this.region.contains(this.transform.inverse().apply(position.toVector3()).toBlockPoint());
    }

    @Override
    public List<BlockVector2> polygonize(int maxPoints) {
        List<BlockVector2> origPoints = this.region.polygonize(maxPoints);
        ArrayList<BlockVector2> transformedPoints = new ArrayList<BlockVector2>();
        for (BlockVector2 vector : origPoints) {
            transformedPoints.add(this.transform.apply(vector.toVector3(0.0)).toVector2().toBlockPoint());
        }
        return transformedPoints;
    }

    @Override
    public Iterator<BlockVector3> iterator() {
        final Iterator it = this.region.iterator();
        return new Iterator<BlockVector3>(){

            @Override
            public boolean hasNext() {
                return it.hasNext();
            }

            @Override
            public BlockVector3 next() {
                BlockVector3 next = (BlockVector3)((Object)it.next());
                if (next != null) {
                    return TransformRegion.this.transform.apply(next.toVector3()).toBlockPoint();
                }
                return null;
            }

            @Override
            public void remove() {
                it.remove();
            }
        };
    }
}

