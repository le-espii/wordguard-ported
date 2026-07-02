/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  javax.annotation.Nullable
 */
package com.sk89q.worldedit.regions.selector;

import com.google.common.base.Preconditions;
import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.extension.platform.Actor;
import com.sk89q.worldedit.internal.cui.CUIRegion;
import com.sk89q.worldedit.internal.cui.SelectionPointEvent;
import com.sk89q.worldedit.internal.cui.SelectionPolygonEvent;
import com.sk89q.worldedit.internal.cui.SelectionShapeEvent;
import com.sk89q.worldedit.math.BlockVector2;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.ConvexPolyhedralRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.regions.RegionSelector;
import com.sk89q.worldedit.regions.polyhedron.Triangle;
import com.sk89q.worldedit.regions.selector.limit.SelectorLimits;
import com.sk89q.worldedit.util.formatting.text.Component;
import com.sk89q.worldedit.util.formatting.text.TextComponent;
import com.sk89q.worldedit.util.formatting.text.TranslatableComponent;
import com.sk89q.worldedit.world.World;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;

public class ConvexPolyhedralRegionSelector
implements RegionSelector,
CUIRegion {
    private final transient ConvexPolyhedralRegion region;
    private transient BlockVector3 pos1;

    public ConvexPolyhedralRegionSelector() {
        this((World)null);
    }

    public ConvexPolyhedralRegionSelector(@Nullable World world) {
        this.region = new ConvexPolyhedralRegion(world);
    }

    public ConvexPolyhedralRegionSelector(RegionSelector oldSelector) {
        Preconditions.checkNotNull((Object)oldSelector);
        if (oldSelector instanceof ConvexPolyhedralRegionSelector) {
            ConvexPolyhedralRegionSelector convexPolyhedralRegionSelector = (ConvexPolyhedralRegionSelector)oldSelector;
            this.pos1 = convexPolyhedralRegionSelector.pos1;
            this.region = new ConvexPolyhedralRegion(convexPolyhedralRegionSelector.region);
        } else {
            Region oldRegion;
            try {
                oldRegion = oldSelector.getRegion();
            }
            catch (IncompleteRegionException e) {
                this.region = new ConvexPolyhedralRegion(oldSelector.getIncompleteRegion().getWorld());
                return;
            }
            int minY = oldRegion.getMinimumPoint().y();
            int maxY = oldRegion.getMaximumPoint().y();
            this.region = new ConvexPolyhedralRegion(oldRegion.getWorld());
            for (BlockVector2 pt : new ArrayList<BlockVector2>(oldRegion.polygonize(Integer.MAX_VALUE))) {
                this.region.addVertex(pt.toBlockVector3(minY));
                this.region.addVertex(pt.toBlockVector3(maxY));
            }
            this.learnChanges();
        }
    }

    @Override
    @Nullable
    public World getWorld() {
        return this.region.getWorld();
    }

    @Override
    public void setWorld(@Nullable World world) {
        this.region.setWorld(world);
    }

    @Override
    public boolean selectPrimary(BlockVector3 position, SelectorLimits limits) {
        Preconditions.checkNotNull((Object)((Object)position));
        this.clear();
        this.pos1 = position;
        return this.region.addVertex(position);
    }

    @Override
    public boolean selectSecondary(BlockVector3 position, SelectorLimits limits) {
        Preconditions.checkNotNull((Object)((Object)position));
        Optional<Integer> vertexLimit = limits.getPolyhedronVertexLimit();
        if (vertexLimit.isPresent() && this.region.getVertices().size() > vertexLimit.get()) {
            return false;
        }
        return this.region.addVertex(position);
    }

    @Override
    public BlockVector3 getPrimaryPosition() throws IncompleteRegionException {
        return this.pos1;
    }

    @Override
    public Region getRegion() throws IncompleteRegionException {
        if (!this.region.isDefined()) {
            throw new IncompleteRegionException();
        }
        return this.region;
    }

    @Override
    public Region getIncompleteRegion() {
        return this.region;
    }

    @Override
    public boolean isDefined() {
        return this.region.isDefined();
    }

    @Override
    public long getVolume() {
        return this.region.getVolume();
    }

    @Override
    public void learnChanges() {
        this.pos1 = this.region.getVertices().iterator().next();
    }

    @Override
    public void clear() {
        this.region.clear();
    }

    @Override
    public String getTypeName() {
        return "Convex Polyhedron";
    }

    @Override
    public List<Component> getSelectionInfoLines() {
        ArrayList<Component> ret = new ArrayList<Component>();
        ret.add(TranslatableComponent.of("worldedit.selection.convex.info.vertices", TextComponent.of(this.region.getVertices().size())));
        ret.add(TranslatableComponent.of("worldedit.selection.convex.info.triangles", TextComponent.of(this.region.getTriangles().size())));
        return ret;
    }

    @Override
    public void explainPrimarySelection(Actor player, LocalSession session, BlockVector3 pos) {
        Preconditions.checkNotNull((Object)player);
        Preconditions.checkNotNull((Object)session);
        Preconditions.checkNotNull((Object)((Object)pos));
        session.dispatchCUIEvent(player, new SelectionShapeEvent(this.getTypeID()));
        session.describeCUI(player);
        player.printInfo(TranslatableComponent.of("worldedit.selection.convex.explain.primary", TextComponent.of(pos.toString())));
    }

    @Override
    public void explainSecondarySelection(Actor player, LocalSession session, BlockVector3 pos) {
        Preconditions.checkNotNull((Object)player);
        Preconditions.checkNotNull((Object)session);
        Preconditions.checkNotNull((Object)((Object)pos));
        session.describeCUI(player);
        player.printInfo(TranslatableComponent.of("worldedit.selection.convex.explain.secondary", TextComponent.of(pos.toString())));
    }

    @Override
    public void explainRegionAdjust(Actor player, LocalSession session) {
        Preconditions.checkNotNull((Object)player);
        Preconditions.checkNotNull((Object)session);
        session.dispatchCUIEvent(player, new SelectionShapeEvent(this.getTypeID()));
        session.describeCUI(player);
    }

    @Override
    public int getProtocolVersion() {
        return 3;
    }

    @Override
    public String getTypeID() {
        return "polyhedron";
    }

    @Override
    public void describeCUI(LocalSession session, Actor player) {
        Preconditions.checkNotNull((Object)player);
        Preconditions.checkNotNull((Object)session);
        Collection<BlockVector3> vertices = this.region.getVertices();
        Collection<Triangle> triangles = this.region.getTriangles();
        HashMap<BlockVector3, Integer> vertexIds = new HashMap<BlockVector3, Integer>(vertices.size());
        int lastVertexId = -1;
        for (BlockVector3 vertex : vertices) {
            vertexIds.put(vertex, ++lastVertexId);
            session.dispatchCUIEvent(player, new SelectionPointEvent(lastVertexId, vertex, this.getVolume()));
        }
        for (Triangle triangle : triangles) {
            int[] v = new int[3];
            for (int i = 0; i < 3; ++i) {
                v[i] = (Integer)vertexIds.get((Object)triangle.getVertex(i).toBlockPoint());
            }
            session.dispatchCUIEvent(player, new SelectionPolygonEvent(v));
        }
    }

    @Override
    public String getLegacyTypeID() {
        return "cuboid";
    }

    @Override
    public void describeLegacyCUI(LocalSession session, Actor player) {
        Preconditions.checkNotNull((Object)player);
        Preconditions.checkNotNull((Object)session);
        if (this.isDefined()) {
            session.dispatchCUIEvent(player, new SelectionPointEvent(0, this.region.getMinimumPoint(), this.getVolume()));
            session.dispatchCUIEvent(player, new SelectionPointEvent(1, this.region.getMaximumPoint(), this.getVolume()));
        }
    }
}

