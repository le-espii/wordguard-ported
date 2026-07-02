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
import com.sk89q.worldedit.internal.cui.SelectionEllipsoidPointEvent;
import com.sk89q.worldedit.internal.cui.SelectionPointEvent;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.math.Vector3;
import com.sk89q.worldedit.regions.EllipsoidRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.regions.RegionSelector;
import com.sk89q.worldedit.regions.selector.limit.SelectorLimits;
import com.sk89q.worldedit.util.formatting.text.Component;
import com.sk89q.worldedit.util.formatting.text.TextComponent;
import com.sk89q.worldedit.util.formatting.text.TranslatableComponent;
import com.sk89q.worldedit.util.formatting.text.event.ClickEvent;
import com.sk89q.worldedit.util.formatting.text.event.HoverEvent;
import com.sk89q.worldedit.world.World;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;

public class EllipsoidRegionSelector
implements RegionSelector,
CUIRegion {
    protected transient EllipsoidRegion region;
    protected transient boolean started = false;
    protected transient boolean selectedRadius = false;

    public EllipsoidRegionSelector() {
        this((World)null);
    }

    public EllipsoidRegionSelector(@Nullable World world) {
        this.region = new EllipsoidRegion(world, BlockVector3.ZERO, Vector3.ZERO);
    }

    public EllipsoidRegionSelector(RegionSelector oldSelector) {
        this(((RegionSelector)Preconditions.checkNotNull((Object)oldSelector)).getIncompleteRegion().getWorld());
        if (oldSelector instanceof EllipsoidRegionSelector) {
            EllipsoidRegionSelector ellipsoidRegionSelector = (EllipsoidRegionSelector)oldSelector;
            this.region = new EllipsoidRegion(ellipsoidRegionSelector.getIncompleteRegion());
            this.started = ellipsoidRegionSelector.started;
            this.selectedRadius = ellipsoidRegionSelector.selectedRadius;
        } else {
            Region oldRegion;
            try {
                oldRegion = oldSelector.getRegion();
            }
            catch (IncompleteRegionException e) {
                return;
            }
            BlockVector3 pos1 = oldRegion.getMinimumPoint();
            BlockVector3 pos2 = oldRegion.getMaximumPoint();
            BlockVector3 center = pos1.add(pos2).divide(2).floor();
            this.region.setCenter(center);
            this.region.setRadius(pos2.subtract(center).toVector3());
            this.started = true;
            this.selectedRadius = true;
        }
    }

    public EllipsoidRegionSelector(@Nullable World world, BlockVector3 center, Vector3 radius) {
        this(world);
        this.region.setCenter(center);
        this.region.setRadius(radius);
        this.started = true;
        this.selectedRadius = true;
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
        if (this.started && position.equals((Object)this.region.getCenter().toBlockPoint()) && !this.selectedRadius) {
            return false;
        }
        this.region.setCenter(position);
        this.region.setRadius(Vector3.ZERO);
        this.started = true;
        this.selectedRadius = false;
        return true;
    }

    @Override
    public boolean selectSecondary(BlockVector3 position, SelectorLimits limits) {
        if (!this.started) {
            return false;
        }
        Vector3 diff = position.toVector3().subtract(this.region.getCenter());
        Vector3 minRadius = diff.getMaximum(diff.multiply(-1.0));
        this.region.extendRadius(minRadius);
        this.selectedRadius = true;
        return true;
    }

    @Override
    public void explainPrimarySelection(Actor player, LocalSession session, BlockVector3 pos) {
        if (this.isDefined()) {
            player.printInfo(TranslatableComponent.of("worldedit.selection.ellipsoid.explain.primary-area", TextComponent.of(this.region.getCenter().toString()), TextComponent.of(this.region.getVolume())));
        } else {
            player.printInfo(TranslatableComponent.of("worldedit.selection.ellipsoid.explain.primary", TextComponent.of(this.region.getCenter().toString())));
        }
        session.describeCUI(player);
    }

    @Override
    public void explainSecondarySelection(Actor player, LocalSession session, BlockVector3 pos) {
        if (this.isDefined()) {
            player.printInfo(TranslatableComponent.of("worldedit.selection.ellipsoid.explain.secondary-area", TextComponent.of(this.region.getRadius().toString()), TextComponent.of(this.region.getVolume())));
        } else {
            player.printInfo(TranslatableComponent.of("worldedit.selection.ellipsoid.explain.secondary", TextComponent.of(this.region.getRadius().toString())));
        }
        session.describeCUI(player);
    }

    @Override
    public void explainRegionAdjust(Actor player, LocalSession session) {
        session.describeCUI(player);
    }

    @Override
    public boolean isDefined() {
        return this.selectedRadius;
    }

    @Override
    public EllipsoidRegion getRegion() throws IncompleteRegionException {
        if (!this.isDefined()) {
            throw new IncompleteRegionException();
        }
        return this.region;
    }

    @Override
    public EllipsoidRegion getIncompleteRegion() {
        return this.region;
    }

    @Override
    public void learnChanges() {
    }

    @Override
    public void clear() {
        this.region.setCenter(BlockVector3.ZERO);
        this.region.setRadius(Vector3.ZERO);
        this.started = false;
        this.selectedRadius = false;
    }

    @Override
    public String getTypeName() {
        return "ellipsoid";
    }

    @Override
    public List<Component> getSelectionInfoLines() {
        Vector3 radius;
        ArrayList<Component> lines = new ArrayList<Component>();
        Vector3 center = this.region.getCenter();
        if (center.lengthSq() > 0.0) {
            lines.add(TranslatableComponent.of("worldedit.selection.ellipsoid.info.center", ((TextComponent)TextComponent.of(center.toString()).clickEvent(ClickEvent.of(ClickEvent.Action.COPY_TO_CLIPBOARD, center.toParserString()))).hoverEvent(HoverEvent.of(HoverEvent.Action.SHOW_TEXT, TextComponent.of("Click to copy")))));
        }
        if ((radius = this.region.getRadius()).lengthSq() > 0.0) {
            lines.add(TranslatableComponent.of("worldedit.selection.ellipsoid.info.radius", TextComponent.of(radius.toString())));
        }
        return lines;
    }

    @Override
    public long getVolume() {
        return this.region.getVolume();
    }

    @Override
    public void describeCUI(LocalSession session, Actor player) {
        session.dispatchCUIEvent(player, new SelectionEllipsoidPointEvent(0, this.region.getCenter().toBlockPoint()));
        session.dispatchCUIEvent(player, new SelectionEllipsoidPointEvent(1, this.region.getRadius().toBlockPoint()));
    }

    @Override
    public void describeLegacyCUI(LocalSession session, Actor player) {
        session.dispatchCUIEvent(player, new SelectionPointEvent(0, this.region.getMinimumPoint(), this.getVolume()));
        session.dispatchCUIEvent(player, new SelectionPointEvent(1, this.region.getMaximumPoint(), this.getVolume()));
    }

    @Override
    public String getLegacyTypeID() {
        return "cuboid";
    }

    @Override
    public int getProtocolVersion() {
        return 1;
    }

    @Override
    public String getTypeID() {
        return "ellipsoid";
    }

    @Override
    public BlockVector3 getPrimaryPosition() throws IncompleteRegionException {
        return this.region.getCenter().toBlockPoint();
    }
}

