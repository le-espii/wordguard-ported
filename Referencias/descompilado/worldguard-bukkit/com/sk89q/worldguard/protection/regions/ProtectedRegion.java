/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.Lists
 *  com.sk89q.worldedit.math.BlockVector2
 *  com.sk89q.worldedit.math.BlockVector3
 *  javax.annotation.Nullable
 */
package com.sk89q.worldguard.protection.regions;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.sk89q.worldedit.math.BlockVector2;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.regions.RegionType;
import com.sk89q.worldguard.util.ChangeTracked;
import com.sk89q.worldguard.util.Normal;
import java.awt.geom.Area;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.Pattern;
import javax.annotation.Nullable;

public abstract class ProtectedRegion
implements ChangeTracked,
Comparable<ProtectedRegion> {
    public static final String GLOBAL_REGION = "__global__";
    private static final Pattern VALID_ID_PATTERN = Pattern.compile("^[A-Za-z0-9_,'\\-\\+/]{1,}$");
    protected BlockVector3 min;
    protected BlockVector3 max;
    private final String id;
    private final boolean transientRegion;
    private int priority = 0;
    private ProtectedRegion parent;
    private DefaultDomain owners = new DefaultDomain();
    private DefaultDomain members = new DefaultDomain();
    private ConcurrentMap<Flag<?>, Object> flags = new ConcurrentHashMap();
    private boolean dirty = true;

    ProtectedRegion(String id, boolean transientRegion) {
        Preconditions.checkNotNull((Object)id);
        if (!ProtectedRegion.isValidId(id)) {
            throw new IllegalArgumentException("Invalid region ID: " + id);
        }
        this.id = Normal.normalize(id);
        this.transientRegion = transientRegion;
    }

    protected void setMinMaxPoints(List<BlockVector3> points) {
        int minX = points.get(0).x();
        int minY = points.get(0).y();
        int minZ = points.get(0).z();
        int maxX = minX;
        int maxY = minY;
        int maxZ = minZ;
        for (BlockVector3 v : points) {
            int x = v.x();
            int y = v.y();
            int z = v.z();
            if (x < minX) {
                minX = x;
            }
            if (y < minY) {
                minY = y;
            }
            if (z < minZ) {
                minZ = z;
            }
            if (x > maxX) {
                maxX = x;
            }
            if (y > maxY) {
                maxY = y;
            }
            if (z <= maxZ) continue;
            maxZ = z;
        }
        this.setDirty(true);
        this.min = BlockVector3.at((int)minX, (int)minY, (int)minZ);
        this.max = BlockVector3.at((int)maxX, (int)maxY, (int)maxZ);
    }

    public String getId() {
        return this.id;
    }

    public abstract boolean isPhysicalArea();

    public BlockVector3 getMinimumPoint() {
        return this.min;
    }

    public BlockVector3 getMaximumPoint() {
        return this.max;
    }

    public int getPriority() {
        return this.priority;
    }

    public void setPriority(int priority) {
        this.setDirty(true);
        this.priority = priority;
    }

    @Nullable
    public ProtectedRegion getParent() {
        return this.parent;
    }

    public void setParent(@Nullable ProtectedRegion parent) throws CircularInheritanceException {
        this.setDirty(true);
        if (parent == null) {
            this.parent = null;
            return;
        }
        if (parent == this) {
            throw new CircularInheritanceException();
        }
        for (ProtectedRegion p = parent.getParent(); p != null; p = p.getParent()) {
            if (p != this) continue;
            throw new CircularInheritanceException();
        }
        this.parent = parent;
    }

    public void clearParent() {
        this.setDirty(true);
        this.parent = null;
    }

    public DefaultDomain getOwners() {
        return this.owners;
    }

    public void setOwners(DefaultDomain owners) {
        Preconditions.checkNotNull((Object)owners);
        this.setDirty(true);
        this.owners = new DefaultDomain(owners);
    }

    public DefaultDomain getMembers() {
        return this.members;
    }

    public void setMembers(DefaultDomain members) {
        Preconditions.checkNotNull((Object)members);
        this.setDirty(true);
        this.members = new DefaultDomain(members);
    }

    public boolean hasMembersOrOwners() {
        return this.owners.size() > 0 || this.members.size() > 0;
    }

    public boolean isOwner(LocalPlayer player) {
        Preconditions.checkNotNull((Object)player);
        if (this.owners.contains(player)) {
            return true;
        }
        for (ProtectedRegion curParent = this.getParent(); curParent != null; curParent = curParent.getParent()) {
            if (!curParent.getOwners().contains(player)) continue;
            return true;
        }
        return false;
    }

    @Deprecated
    public boolean isOwner(String playerName) {
        Preconditions.checkNotNull((Object)playerName);
        if (this.owners.contains(playerName)) {
            return true;
        }
        for (ProtectedRegion curParent = this.getParent(); curParent != null; curParent = curParent.getParent()) {
            if (!curParent.getOwners().contains(playerName)) continue;
            return true;
        }
        return false;
    }

    public boolean isMember(LocalPlayer player) {
        Preconditions.checkNotNull((Object)player);
        if (this.isOwner(player)) {
            return true;
        }
        return this.isMemberOnly(player);
    }

    @Deprecated
    public boolean isMember(String playerName) {
        Preconditions.checkNotNull((Object)playerName);
        if (this.isOwner(playerName)) {
            return true;
        }
        if (this.members.contains(playerName)) {
            return true;
        }
        for (ProtectedRegion curParent = this.getParent(); curParent != null; curParent = curParent.getParent()) {
            if (!curParent.getMembers().contains(playerName)) continue;
            return true;
        }
        return false;
    }

    public boolean isMemberOnly(LocalPlayer player) {
        Preconditions.checkNotNull((Object)player);
        if (this.members.contains(player)) {
            return true;
        }
        for (ProtectedRegion curParent = this.getParent(); curParent != null; curParent = curParent.getParent()) {
            if (!curParent.getMembers().contains(player)) continue;
            return true;
        }
        return false;
    }

    @Nullable
    public <T extends Flag<V>, V> V getFlag(T flag) {
        Preconditions.checkNotNull(flag);
        Object obj = this.flags.get(flag);
        if (obj == null) {
            return null;
        }
        Object val = obj;
        return val;
    }

    public <T extends Flag<V>, V> void setFlag(T flag, @Nullable V val) {
        Preconditions.checkNotNull(flag);
        this.setDirty(true);
        if (val == null) {
            this.flags.remove(flag);
        } else {
            this.flags.put(flag, val);
        }
    }

    public Map<Flag<?>, Object> getFlags() {
        return this.flags;
    }

    public void setFlags(Map<Flag<?>, Object> flags) {
        Preconditions.checkNotNull(flags);
        this.setDirty(true);
        this.flags = new ConcurrentHashMap(flags);
    }

    public void copyFrom(ProtectedRegion other) {
        Preconditions.checkNotNull((Object)other);
        this.setMembers(other.getMembers());
        this.setOwners(other.getOwners());
        this.setFlags(other.getFlags());
        this.setPriority(other.getPriority());
        try {
            this.setParent(other.getParent());
        }
        catch (CircularInheritanceException circularInheritanceException) {
            // empty catch block
        }
    }

    public abstract List<BlockVector2> getPoints();

    public abstract int volume();

    public abstract boolean contains(BlockVector3 var1);

    public boolean contains(BlockVector2 position) {
        Preconditions.checkNotNull((Object)position);
        return this.contains(BlockVector3.at((int)position.x(), (int)this.min.y(), (int)position.z()));
    }

    public boolean contains(int x, int y, int z) {
        return this.contains(BlockVector3.at((int)x, (int)y, (int)z));
    }

    public boolean containsAny(List<BlockVector2> positions) {
        Preconditions.checkNotNull(positions);
        for (BlockVector2 pt : positions) {
            if (!this.contains(pt)) continue;
            return true;
        }
        return false;
    }

    public abstract RegionType getType();

    public List<ProtectedRegion> getIntersectingRegions(Collection<ProtectedRegion> regions) {
        Preconditions.checkNotNull(regions, (Object)"regions");
        ArrayList intersecting = Lists.newArrayList();
        Area thisArea = this.toArea();
        for (ProtectedRegion region : regions) {
            if (!region.isPhysicalArea() || !this.intersects(region, thisArea)) continue;
            intersecting.add(region);
        }
        return intersecting;
    }

    protected boolean intersects(ProtectedRegion region, Area thisArea) {
        if (this.intersectsBoundingBox(region)) {
            Area testArea = region.toArea();
            testArea.intersect(thisArea);
            return !testArea.isEmpty();
        }
        return false;
    }

    protected boolean intersectsBoundingBox(ProtectedRegion region) {
        BlockVector3 rMaxPoint = region.getMaximumPoint();
        BlockVector3 min = this.getMinimumPoint();
        if (rMaxPoint.x() < min.x()) {
            return false;
        }
        if (rMaxPoint.y() < min.y()) {
            return false;
        }
        if (rMaxPoint.z() < min.z()) {
            return false;
        }
        BlockVector3 rMinPoint = region.getMinimumPoint();
        BlockVector3 max = this.getMaximumPoint();
        if (rMinPoint.x() > max.x()) {
            return false;
        }
        if (rMinPoint.y() > max.y()) {
            return false;
        }
        return rMinPoint.z() <= max.z();
    }

    protected boolean intersectsEdges(ProtectedRegion region) {
        List<BlockVector2> pts1 = this.getPoints();
        List<BlockVector2> pts2 = region.getPoints();
        BlockVector2 lastPt1 = pts1.get(pts1.size() - 1);
        BlockVector2 lastPt2 = pts2.get(pts2.size() - 1);
        for (BlockVector2 aPts1 : pts1) {
            for (BlockVector2 aPts2 : pts2) {
                Line2D.Double line1 = new Line2D.Double(lastPt1.x(), lastPt1.z(), aPts1.x(), aPts1.z());
                if (line1.intersectsLine(lastPt2.x(), lastPt2.z(), aPts2.x(), aPts2.z())) {
                    return true;
                }
                lastPt2 = aPts2;
            }
            lastPt1 = aPts1;
        }
        return false;
    }

    abstract Area toArea();

    public boolean isTransient() {
        return this.transientRegion;
    }

    @Override
    public boolean isDirty() {
        if (this.isTransient()) {
            return false;
        }
        return this.dirty || this.owners.isDirty() || this.members.isDirty();
    }

    @Override
    public void setDirty(boolean dirty) {
        this.dirty = dirty;
        this.owners.setDirty(dirty);
        this.members.setDirty(dirty);
    }

    @Override
    public int compareTo(ProtectedRegion other) {
        if (this.getPriority() > other.getPriority()) {
            return -1;
        }
        if (this.getPriority() < other.getPriority()) {
            return 1;
        }
        return this.getId().compareTo(other.getId());
    }

    public int hashCode() {
        return this.id.hashCode();
    }

    public String toString() {
        return "ProtectedRegion{id='" + this.id + "', type='" + String.valueOf((Object)this.getType()) + "'}";
    }

    public static boolean isValidId(String id) {
        Preconditions.checkNotNull((Object)id);
        return VALID_ID_PATTERN.matcher(id).matches();
    }

    public static class CircularInheritanceException
    extends Exception {
    }
}

