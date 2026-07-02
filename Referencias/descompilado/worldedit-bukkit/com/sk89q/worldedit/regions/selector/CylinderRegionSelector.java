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
import com.sk89q.worldedit.internal.cui.SelectionCylinderEvent;
import com.sk89q.worldedit.internal.cui.SelectionMinMaxEvent;
import com.sk89q.worldedit.internal.cui.SelectionPointEvent;
import com.sk89q.worldedit.math.BlockVector2;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.math.Vector2;
import com.sk89q.worldedit.math.Vector3;
import com.sk89q.worldedit.regions.CylinderRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.regions.RegionSelector;
import com.sk89q.worldedit.regions.selector.limit.SelectorLimits;
import com.sk89q.worldedit.util.formatting.text.Component;
import com.sk89q.worldedit.util.formatting.text.TextComponent;
import com.sk89q.worldedit.util.formatting.text.TranslatableComponent;
import com.sk89q.worldedit.util.formatting.text.event.ClickEvent;
import com.sk89q.worldedit.util.formatting.text.event.HoverEvent;
import com.sk89q.worldedit.world.World;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;

public class CylinderRegionSelector
implements RegionSelector,
CUIRegion {
    protected static final transient NumberFormat NUMBER_FORMAT = (NumberFormat)NumberFormat.getInstance().clone();
    protected transient CylinderRegion region;
    protected transient boolean selectedCenter;
    protected transient boolean selectedRadius;

    public CylinderRegionSelector() {
        this((World)null);
    }

    public CylinderRegionSelector(@Nullable World world) {
        this.region = new CylinderRegion(world);
    }

    public CylinderRegionSelector(RegionSelector oldSelector) {
        this(((RegionSelector)Preconditions.checkNotNull((Object)oldSelector)).getIncompleteRegion().getWorld());
        if (oldSelector instanceof CylinderRegionSelector) {
            CylinderRegionSelector cylSelector = (CylinderRegionSelector)oldSelector;
            this.region = new CylinderRegion(cylSelector.region);
            this.selectedCenter = cylSelector.selectedCenter;
            this.selectedRadius = cylSelector.selectedRadius;
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
            this.region.setCenter(center.toBlockVector2());
            this.region.setRadius(pos2.toBlockVector2().subtract(center.toBlockVector2()).toVector2());
            this.region.setMaximumY(Math.max(pos1.y(), pos2.y()));
            this.region.setMinimumY(Math.min(pos1.y(), pos2.y()));
            this.selectedCenter = true;
            this.selectedRadius = true;
        }
    }

    public CylinderRegionSelector(@Nullable World world, BlockVector2 center, Vector2 radius, int minY, int maxY) {
        this(world);
        this.region.setCenter(center);
        this.region.setRadius(radius);
        this.region.setMinimumY(Math.min(minY, maxY));
        this.region.setMaximumY(Math.max(minY, maxY));
        this.selectedCenter = true;
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
        if (this.selectedCenter && position.equals((Object)this.region.getCenter().toBlockPoint()) && !this.selectedRadius) {
            return false;
        }
        this.region = new CylinderRegion(this.region.getWorld());
        this.region.setCenter(position.toBlockVector2());
        this.region.setY(position.y());
        this.selectedCenter = true;
        this.selectedRadius = false;
        return true;
    }

    @Override
    public boolean selectSecondary(BlockVector3 position, SelectorLimits limits) {
        if (!this.selectedCenter) {
            return true;
        }
        Vector2 diff = position.toVector3().subtract(this.region.getCenter()).toVector2();
        Vector2 minRadius = diff.getMaximum(diff.multiply(-1.0));
        this.region.extendRadius(minRadius);
        this.region.setY(position.y());
        this.selectedRadius = true;
        return true;
    }

    @Override
    public void explainPrimarySelection(Actor player, LocalSession session, BlockVector3 pos) {
        player.printInfo(TranslatableComponent.of("worldedit.selection.cylinder.explain.primary", TextComponent.of(pos.toString())));
        session.describeCUI(player);
    }

    @Override
    public void explainSecondarySelection(Actor player, LocalSession session, BlockVector3 pos) {
        if (!this.selectedCenter) {
            player.printError(TranslatableComponent.of("worldedit.selection.cylinder.explain.secondary-missing"));
            return;
        }
        player.printInfo(TranslatableComponent.of("worldedit.selection.cylinder.explain.secondary", TextComponent.of(NUMBER_FORMAT.format(this.region.getRadius().x())), TextComponent.of(NUMBER_FORMAT.format(this.region.getRadius().z())), TextComponent.of(this.region.getVolume())));
        session.describeCUI(player);
    }

    @Override
    public void explainRegionAdjust(Actor player, LocalSession session) {
        session.describeCUI(player);
    }

    @Override
    public BlockVector3 getPrimaryPosition() throws IncompleteRegionException {
        if (!this.isDefined()) {
            throw new IncompleteRegionException();
        }
        return this.region.getCenter().toBlockPoint();
    }

    @Override
    public CylinderRegion getRegion() throws IncompleteRegionException {
        if (!this.isDefined()) {
            throw new IncompleteRegionException();
        }
        return this.region;
    }

    @Override
    public CylinderRegion getIncompleteRegion() {
        return this.region;
    }

    @Override
    public boolean isDefined() {
        return this.selectedRadius;
    }

    @Override
    public void learnChanges() {
    }

    @Override
    public void clear() {
        this.region = new CylinderRegion(this.region.getWorld());
        this.selectedCenter = false;
        this.selectedRadius = false;
    }

    @Override
    public String getTypeName() {
        return "Cylinder";
    }

    @Override
    public List<Component> getSelectionInfoLines() {
        ArrayList<Component> lines = new ArrayList<Component>();
        if (!this.region.getCenter().equals((Object)Vector3.ZERO)) {
            Vector3 center = this.region.getCenter();
            lines.add(TranslatableComponent.of("worldedit.selection.cylinder.info.center", ((TextComponent)TextComponent.of(center.toString()).clickEvent(ClickEvent.of(ClickEvent.Action.COPY_TO_CLIPBOARD, center.toParserString()))).hoverEvent(HoverEvent.of(HoverEvent.Action.SHOW_TEXT, TextComponent.of("Click to copy")))));
        }
        if (!this.region.getRadius().equals((Object)Vector2.ZERO)) {
            lines.add(TranslatableComponent.of("worldedit.selection.cylinder.info.radius", TextComponent.of(this.region.getRadius().toString())));
        }
        return lines;
    }

    @Override
    public long getVolume() {
        return this.region.getVolume();
    }

    @Override
    public void describeCUI(LocalSession session, Actor player) {
        session.dispatchCUIEvent(player, new SelectionCylinderEvent(this.region.getCenter().toBlockPoint(), this.region.getRadius()));
        session.dispatchCUIEvent(player, new SelectionMinMaxEvent(this.region.getMinimumY(), this.region.getMaximumY()));
    }

    @Override
    public void describeLegacyCUI(LocalSession session, Actor player) {
        if (this.isDefined()) {
            session.dispatchCUIEvent(player, new SelectionPointEvent(0, this.region.getMinimumPoint(), this.getVolume()));
            session.dispatchCUIEvent(player, new SelectionPointEvent(1, this.region.getMaximumPoint(), this.getVolume()));
        }
    }

    @Override
    public int getProtocolVersion() {
        return 1;
    }

    @Override
    public String getTypeID() {
        return "cylinder";
    }

    @Override
    public String getLegacyTypeID() {
        return "cuboid";
    }

    static {
        NUMBER_FORMAT.setMaximumFractionDigits(3);
    }
}

