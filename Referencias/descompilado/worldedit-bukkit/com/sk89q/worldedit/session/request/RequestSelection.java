/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.session.request;

import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.math.BlockVector2;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.math.Vector3;
import com.sk89q.worldedit.regions.NullRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.regions.RegionOperationException;
import com.sk89q.worldedit.session.request.Request;
import com.sk89q.worldedit.world.World;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class RequestSelection
implements Region {
    protected Region getRegion() {
        LocalSession session = Request.request().getSession();
        World world = Request.request().getWorld();
        if (session != null && world != null) {
            try {
                return session.getSelection(world);
            }
            catch (IncompleteRegionException incompleteRegionException) {
                // empty catch block
            }
        }
        return new NullRegion();
    }

    @Override
    public BlockVector3 getMinimumPoint() {
        return this.getRegion().getMinimumPoint();
    }

    @Override
    public BlockVector3 getMaximumPoint() {
        return this.getRegion().getMaximumPoint();
    }

    @Override
    public Vector3 getCenter() {
        return this.getRegion().getCenter();
    }

    @Override
    public long getVolume() {
        return this.getRegion().getVolume();
    }

    @Override
    public int getWidth() {
        return this.getRegion().getWidth();
    }

    @Override
    public int getHeight() {
        return this.getRegion().getHeight();
    }

    @Override
    public int getLength() {
        return this.getRegion().getLength();
    }

    @Override
    public void expand(BlockVector3 ... changes) throws RegionOperationException {
        this.getRegion().expand(changes);
    }

    @Override
    public void contract(BlockVector3 ... changes) throws RegionOperationException {
        this.getRegion().contract(changes);
    }

    @Override
    public void shift(BlockVector3 change) throws RegionOperationException {
        this.getRegion().shift(change);
    }

    @Override
    public boolean contains(BlockVector3 position) {
        return this.getRegion().contains(position);
    }

    @Override
    public Set<BlockVector2> getChunks() {
        return this.getRegion().getChunks();
    }

    @Override
    public Set<BlockVector3> getChunkCubes() {
        return this.getRegion().getChunkCubes();
    }

    @Override
    public World getWorld() {
        return this.getRegion().getWorld();
    }

    @Override
    public void setWorld(World world) {
        this.getRegion().setWorld(world);
    }

    @Override
    public Region clone() {
        return this;
    }

    @Override
    public List<BlockVector2> polygonize(int maxPoints) {
        return this.getRegion().polygonize(maxPoints);
    }

    @Override
    public Iterator<BlockVector3> iterator() {
        return this.getRegion().iterator();
    }
}

