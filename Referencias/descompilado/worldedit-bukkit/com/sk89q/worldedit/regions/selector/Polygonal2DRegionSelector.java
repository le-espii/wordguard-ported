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
import com.sk89q.worldedit.internal.cui.SelectionMinMaxEvent;
import com.sk89q.worldedit.internal.cui.SelectionPoint2DEvent;
import com.sk89q.worldedit.internal.cui.SelectionShapeEvent;
import com.sk89q.worldedit.math.BlockVector2;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.Polygonal2DRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.regions.RegionSelector;
import com.sk89q.worldedit.regions.selector.limit.SelectorLimits;
import com.sk89q.worldedit.util.formatting.text.Component;
import com.sk89q.worldedit.util.formatting.text.TextComponent;
import com.sk89q.worldedit.util.formatting.text.TranslatableComponent;
import com.sk89q.worldedit.world.World;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;

public class Polygonal2DRegionSelector
implements RegionSelector,
CUIRegion {
    private transient BlockVector3 pos1;
    private transient Polygonal2DRegion region;

    public Polygonal2DRegionSelector() {
        this((World)null);
    }

    public Polygonal2DRegionSelector(@Nullable World world) {
        this.region = new Polygonal2DRegion(world);
    }

    public Polygonal2DRegionSelector(RegionSelector oldSelector) {
        this(((RegionSelector)Preconditions.checkNotNull((Object)oldSelector)).getIncompleteRegion().getWorld());
        if (oldSelector instanceof Polygonal2DRegionSelector) {
            Polygonal2DRegionSelector polygonal2DRegionSelector = (Polygonal2DRegionSelector)oldSelector;
            this.pos1 = polygonal2DRegionSelector.pos1;
            this.region = new Polygonal2DRegion(polygonal2DRegionSelector.region);
        } else {
            Region oldRegion;
            try {
                oldRegion = oldSelector.getRegion();
            }
            catch (IncompleteRegionException e) {
                return;
            }
            int minY = oldRegion.getMinimumPoint().y();
            int maxY = oldRegion.getMaximumPoint().y();
            List<BlockVector2> points = oldRegion.polygonize(Integer.MAX_VALUE);
            this.pos1 = points.get(0).toBlockVector3(minY);
            this.region = new Polygonal2DRegion(oldRegion.getWorld(), points, minY, maxY);
        }
    }

    public Polygonal2DRegionSelector(@Nullable World world, List<BlockVector2> points, int minY, int maxY) {
        Preconditions.checkNotNull(points);
        BlockVector2 pos2D = points.get(0);
        this.pos1 = BlockVector3.at(pos2D.x(), minY, pos2D.z());
        this.region = new Polygonal2DRegion(world, points, minY, maxY);
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
        if (position.equals((Object)this.pos1)) {
            return false;
        }
        this.pos1 = position;
        this.region = new Polygonal2DRegion(this.region.getWorld());
        this.region.addPoint(position);
        this.region.expandY(position.y());
        return true;
    }

    @Override
    public boolean selectSecondary(BlockVector3 position, SelectorLimits limits) {
        if (this.region.size() > 0) {
            List<BlockVector2> points = this.region.getPoints();
            BlockVector2 lastPoint = points.get(this.region.size() - 1);
            if (lastPoint.x() == position.x() && lastPoint.z() == position.z()) {
                return false;
            }
            Optional<Integer> vertexLimit = limits.getPolygonVertexLimit();
            if (vertexLimit.isPresent() && points.size() > vertexLimit.get()) {
                return false;
            }
        }
        this.region.addPoint(position);
        this.region.expandY(position.y());
        return true;
    }

    @Override
    public void explainPrimarySelection(Actor player, LocalSession session, BlockVector3 pos) {
        player.printInfo(TranslatableComponent.of("worldedit.selection.polygon2d.explain.primary", TextComponent.of(pos.toString())));
        session.dispatchCUIEvent(player, new SelectionShapeEvent(this.getTypeID()));
        session.dispatchCUIEvent(player, new SelectionPoint2DEvent(0, pos, this.getVolume()));
        session.dispatchCUIEvent(player, new SelectionMinMaxEvent(this.region.getMinimumY(), this.region.getMaximumY()));
    }

    @Override
    public void explainSecondarySelection(Actor player, LocalSession session, BlockVector3 pos) {
        player.printInfo(TranslatableComponent.of("worldedit.selection.polygon2d.explain.secondary", TextComponent.of(this.region.size()), TextComponent.of(pos.toString())));
        session.dispatchCUIEvent(player, new SelectionPoint2DEvent(this.region.size() - 1, pos, this.getVolume()));
        session.dispatchCUIEvent(player, new SelectionMinMaxEvent(this.region.getMinimumY(), this.region.getMaximumY()));
    }

    @Override
    public void explainRegionAdjust(Actor player, LocalSession session) {
        session.dispatchCUIEvent(player, new SelectionShapeEvent(this.getTypeID()));
        this.describeCUI(session, player);
    }

    @Override
    public BlockVector3 getPrimaryPosition() throws IncompleteRegionException {
        if (this.pos1 == null) {
            throw new IncompleteRegionException();
        }
        return this.pos1;
    }

    @Override
    public Polygonal2DRegion getRegion() throws IncompleteRegionException {
        if (!this.isDefined()) {
            throw new IncompleteRegionException();
        }
        return this.region;
    }

    @Override
    public Polygonal2DRegion getIncompleteRegion() {
        return this.region;
    }

    @Override
    public boolean isDefined() {
        return this.region.size() > 2;
    }

    @Override
    public void learnChanges() {
        BlockVector2 pt = this.region.getPoints().get(0);
        this.pos1 = BlockVector3.at(pt.x(), this.region.getMinimumPoint().y(), pt.z());
    }

    @Override
    public void clear() {
        this.pos1 = null;
        this.region = new Polygonal2DRegion(this.region.getWorld());
    }

    @Override
    public String getTypeName() {
        return "2Dx1D polygon";
    }

    @Override
    public List<Component> getSelectionInfoLines() {
        return Collections.singletonList(TranslatableComponent.of("worldedit.selection.polygon2d.info", TextComponent.of(this.region.size())));
    }

    @Override
    public long getVolume() {
        return this.region.getVolume();
    }

    public int getPointCount() {
        return this.region.getPoints().size();
    }

    @Override
    public void describeCUI(LocalSession session, Actor player) {
        List<BlockVector2> points = this.region.getPoints();
        for (int id = 0; id < points.size(); ++id) {
            session.dispatchCUIEvent(player, new SelectionPoint2DEvent(id, points.get(id), this.getVolume()));
        }
        session.dispatchCUIEvent(player, new SelectionMinMaxEvent(this.region.getMinimumY(), this.region.getMaximumY()));
    }

    @Override
    public void describeLegacyCUI(LocalSession session, Actor player) {
        this.describeCUI(session, player);
    }

    @Override
    public int getProtocolVersion() {
        return 0;
    }

    @Override
    public String getTypeID() {
        return "polygon2d";
    }

    @Override
    public String getLegacyTypeID() {
        return "polygon2d";
    }
}

