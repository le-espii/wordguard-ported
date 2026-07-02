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
import com.sk89q.worldedit.internal.cui.SelectionShapeEvent;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
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

public class CuboidRegionSelector
implements RegionSelector,
CUIRegion {
    protected transient BlockVector3 position1;
    protected transient BlockVector3 position2;
    protected transient CuboidRegion region;

    public CuboidRegionSelector() {
        this((World)null);
    }

    public CuboidRegionSelector(@Nullable World world) {
        this.region = new CuboidRegion(world, BlockVector3.ZERO, BlockVector3.ZERO);
    }

    public CuboidRegionSelector(RegionSelector oldSelector) {
        this(((RegionSelector)Preconditions.checkNotNull((Object)oldSelector)).getIncompleteRegion().getWorld());
        if (oldSelector instanceof CuboidRegionSelector) {
            CuboidRegionSelector cuboidRegionSelector = (CuboidRegionSelector)oldSelector;
            this.position1 = cuboidRegionSelector.position1;
            this.position2 = cuboidRegionSelector.position2;
        } else {
            Region oldRegion;
            try {
                oldRegion = oldSelector.getRegion();
            }
            catch (IncompleteRegionException e) {
                return;
            }
            this.position1 = oldRegion.getMinimumPoint();
            this.position2 = oldRegion.getMaximumPoint();
        }
        this.region.setPos1(this.position1);
        this.region.setPos2(this.position2);
    }

    public CuboidRegionSelector(@Nullable World world, BlockVector3 position1, BlockVector3 position2) {
        this(world);
        Preconditions.checkNotNull((Object)((Object)position1));
        Preconditions.checkNotNull((Object)((Object)position2));
        this.position1 = position1;
        this.position2 = position2;
        this.region.setPos1(position1);
        this.region.setPos2(position2);
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
        if (position.equals((Object)this.position1)) {
            return false;
        }
        this.position1 = position;
        this.region.setPos1(this.position1);
        return true;
    }

    @Override
    public boolean selectSecondary(BlockVector3 position, SelectorLimits limits) {
        Preconditions.checkNotNull((Object)((Object)position));
        if (position.equals((Object)this.position2)) {
            return false;
        }
        this.position2 = position;
        this.region.setPos2(this.position2);
        return true;
    }

    @Override
    public void explainPrimarySelection(Actor player, LocalSession session, BlockVector3 pos) {
        Preconditions.checkNotNull((Object)player);
        Preconditions.checkNotNull((Object)session);
        Preconditions.checkNotNull((Object)((Object)pos));
        if (this.position1 != null && this.position2 != null) {
            player.printInfo(TranslatableComponent.of("worldedit.selection.cuboid.explain.primary-area", TextComponent.of(this.position1.toString()), TextComponent.of(this.region.getVolume())));
        } else if (this.position1 != null) {
            player.printInfo(TranslatableComponent.of("worldedit.selection.cuboid.explain.primary", TextComponent.of(this.position1.toString())));
        }
        session.dispatchCUIEvent(player, new SelectionPointEvent(0, pos, this.getVolume()));
    }

    @Override
    public void explainSecondarySelection(Actor player, LocalSession session, BlockVector3 pos) {
        Preconditions.checkNotNull((Object)player);
        Preconditions.checkNotNull((Object)session);
        Preconditions.checkNotNull((Object)((Object)pos));
        if (this.position1 != null && this.position2 != null) {
            player.printInfo(TranslatableComponent.of("worldedit.selection.cuboid.explain.secondary-area", TextComponent.of(this.position2.toString()), TextComponent.of(this.region.getVolume())));
        } else if (this.position2 != null) {
            player.printInfo(TranslatableComponent.of("worldedit.selection.cuboid.explain.secondary", TextComponent.of(this.position2.toString())));
        }
        session.dispatchCUIEvent(player, new SelectionPointEvent(1, pos, this.getVolume()));
    }

    @Override
    public void explainRegionAdjust(Actor player, LocalSession session) {
        Preconditions.checkNotNull((Object)player);
        Preconditions.checkNotNull((Object)session);
        session.dispatchCUIEvent(player, new SelectionShapeEvent(this.getTypeID()));
        if (this.position1 != null) {
            session.dispatchCUIEvent(player, new SelectionPointEvent(0, this.position1, this.getVolume()));
        }
        if (this.position2 != null) {
            session.dispatchCUIEvent(player, new SelectionPointEvent(1, this.position2, this.getVolume()));
        }
    }

    @Override
    public BlockVector3 getPrimaryPosition() throws IncompleteRegionException {
        if (this.position1 == null) {
            throw new IncompleteRegionException();
        }
        return this.position1;
    }

    @Override
    public boolean isDefined() {
        return this.position1 != null && this.position2 != null;
    }

    @Override
    public CuboidRegion getRegion() throws IncompleteRegionException {
        if (this.position1 == null || this.position2 == null) {
            throw new IncompleteRegionException();
        }
        return this.region;
    }

    @Override
    public CuboidRegion getIncompleteRegion() {
        return this.region;
    }

    @Override
    public void learnChanges() {
        this.position1 = this.region.getPos1();
        this.position2 = this.region.getPos2();
    }

    @Override
    public void clear() {
        this.position1 = null;
        this.position2 = null;
        this.region.setPos1(BlockVector3.ZERO);
        this.region.setPos2(BlockVector3.ZERO);
    }

    @Override
    public String getTypeName() {
        return "cuboid";
    }

    @Override
    public List<Component> getSelectionInfoLines() {
        ArrayList<Component> lines = new ArrayList<Component>();
        if (this.position1 != null) {
            lines.add(TranslatableComponent.of("worldedit.selection.cuboid.info.pos1", ((TextComponent)TextComponent.of(this.position1.toString()).clickEvent(ClickEvent.of(ClickEvent.Action.COPY_TO_CLIPBOARD, this.position1.toParserString()))).hoverEvent(HoverEvent.of(HoverEvent.Action.SHOW_TEXT, TextComponent.of("Click to copy")))));
        }
        if (this.position2 != null) {
            lines.add(TranslatableComponent.of("worldedit.selection.cuboid.info.pos2", ((TextComponent)TextComponent.of(this.position2.toString()).clickEvent(ClickEvent.of(ClickEvent.Action.COPY_TO_CLIPBOARD, this.position2.toParserString()))).hoverEvent(HoverEvent.of(HoverEvent.Action.SHOW_TEXT, TextComponent.of("Click to copy")))));
        }
        return lines;
    }

    @Override
    public long getVolume() {
        if (this.position1 == null) {
            return -1L;
        }
        if (this.position2 == null) {
            return -1L;
        }
        return this.region.getVolume();
    }

    @Override
    public void describeCUI(LocalSession session, Actor player) {
        if (this.position1 != null) {
            session.dispatchCUIEvent(player, new SelectionPointEvent(0, this.position1, this.getVolume()));
        }
        if (this.position2 != null) {
            session.dispatchCUIEvent(player, new SelectionPointEvent(1, this.position2, this.getVolume()));
        }
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
        return "cuboid";
    }

    @Override
    public String getLegacyTypeID() {
        return "cuboid";
    }
}

