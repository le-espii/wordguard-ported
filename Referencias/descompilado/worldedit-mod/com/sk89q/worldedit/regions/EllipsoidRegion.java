/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.regions;

import com.sk89q.worldedit.math.BlockVector2;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.math.Vector3;
import com.sk89q.worldedit.regions.AbstractRegion;
import com.sk89q.worldedit.regions.RegionOperationException;
import com.sk89q.worldedit.util.formatting.text.TranslatableComponent;
import com.sk89q.worldedit.world.World;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashSet;
import java.util.Set;

public class EllipsoidRegion
extends AbstractRegion {
    private BlockVector3 center;
    private Vector3 radius;
    private static final BigDecimal ELLIPSOID_BASE_MULTIPLIER = BigDecimal.valueOf(4.1887902047863905);

    public EllipsoidRegion(BlockVector3 pos1, Vector3 pos2) {
        this(null, pos1, pos2);
    }

    public EllipsoidRegion(World world, BlockVector3 center, Vector3 radius) {
        super(world);
        this.center = center;
        this.setRadius(radius);
    }

    public EllipsoidRegion(EllipsoidRegion ellipsoidRegion) {
        this(ellipsoidRegion.world, ellipsoidRegion.center, ellipsoidRegion.getRadius());
    }

    @Override
    public BlockVector3 getMinimumPoint() {
        return this.center.toVector3().subtract(this.getRadius()).toBlockPoint();
    }

    @Override
    public BlockVector3 getMaximumPoint() {
        return this.center.toVector3().add(this.getRadius()).toBlockPoint();
    }

    @Override
    public long getVolume() {
        return ELLIPSOID_BASE_MULTIPLIER.multiply(BigDecimal.valueOf(this.radius.x())).multiply(BigDecimal.valueOf(this.radius.y())).multiply(BigDecimal.valueOf(this.radius.z())).setScale(0, RoundingMode.FLOOR).longValue();
    }

    @Override
    public int getWidth() {
        return (int)(2.0 * this.radius.x());
    }

    @Override
    public int getHeight() {
        return (int)(2.0 * this.radius.y());
    }

    @Override
    public int getLength() {
        return (int)(2.0 * this.radius.z());
    }

    private BlockVector3 calculateDiff(BlockVector3 ... changes) throws RegionOperationException {
        BlockVector3 diff = BlockVector3.ZERO.add(changes);
        if ((diff.x() & 1) + (diff.y() & 1) + (diff.z() & 1) != 0) {
            throw new RegionOperationException(TranslatableComponent.of("worldedit.selection.ellipsoid.error.even-horizontal"));
        }
        return diff.divide(2).floor();
    }

    private Vector3 calculateChanges(BlockVector3 ... changes) {
        Vector3 total = Vector3.ZERO;
        for (BlockVector3 change : changes) {
            total = total.add(change.abs().toVector3());
        }
        return total.divide(2.0).floor();
    }

    @Override
    public void expand(BlockVector3 ... changes) throws RegionOperationException {
        this.center = this.center.add(this.calculateDiff(changes));
        this.radius = this.radius.add(this.calculateChanges(changes));
    }

    @Override
    public void contract(BlockVector3 ... changes) throws RegionOperationException {
        this.center = this.center.subtract(this.calculateDiff(changes));
        Vector3 newRadius = this.radius.subtract(this.calculateChanges(changes));
        this.radius = Vector3.at(1.5, 1.5, 1.5).getMaximum(newRadius);
    }

    @Override
    public void shift(BlockVector3 change) throws RegionOperationException {
        this.center = this.center.add(change);
    }

    @Override
    public Vector3 getCenter() {
        return this.center.toVector3();
    }

    public void setCenter(BlockVector3 center) {
        this.center = center;
    }

    public Vector3 getRadius() {
        return this.radius.subtract(0.5, 0.5, 0.5);
    }

    public void setRadius(Vector3 radius) {
        this.radius = radius.add(0.5, 0.5, 0.5);
    }

    @Override
    public Set<BlockVector2> getChunks() {
        HashSet<BlockVector2> chunks = new HashSet<BlockVector2>();
        BlockVector3 min = this.getMinimumPoint();
        BlockVector3 max = this.getMaximumPoint();
        int centerY = this.center.y();
        for (int x = min.x(); x <= max.x(); ++x) {
            for (int z = min.z(); z <= max.z(); ++z) {
                if (!this.contains(BlockVector3.at(x, centerY, z))) continue;
                chunks.add(BlockVector2.at(x >> 4, z >> 4));
            }
        }
        return chunks;
    }

    @Override
    public boolean contains(BlockVector3 position) {
        return position.subtract(this.center).toVector3().divide(this.radius).lengthSq() <= 1.0;
    }

    public String toString() {
        return String.valueOf((Object)this.center) + " - " + String.valueOf((Object)this.getRadius());
    }

    public void extendRadius(Vector3 minRadius) {
        this.setRadius(minRadius.getMaximum(this.getRadius()));
    }

    @Override
    public EllipsoidRegion clone() {
        return (EllipsoidRegion)super.clone();
    }
}

