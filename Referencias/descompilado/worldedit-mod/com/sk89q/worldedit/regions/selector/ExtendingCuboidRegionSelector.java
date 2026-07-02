/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.sk89q.worldedit.regions.selector;

import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.extension.platform.Actor;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.RegionSelector;
import com.sk89q.worldedit.regions.selector.CuboidRegionSelector;
import com.sk89q.worldedit.regions.selector.limit.SelectorLimits;
import com.sk89q.worldedit.util.formatting.text.TextComponent;
import com.sk89q.worldedit.util.formatting.text.TranslatableComponent;
import com.sk89q.worldedit.world.World;
import javax.annotation.Nullable;

public class ExtendingCuboidRegionSelector
extends CuboidRegionSelector {
    public ExtendingCuboidRegionSelector() {
        super((World)null);
    }

    public ExtendingCuboidRegionSelector(@Nullable World world) {
        super(world);
    }

    public ExtendingCuboidRegionSelector(RegionSelector oldSelector) {
        super(oldSelector);
        if (this.position1 == null || this.position2 == null) {
            return;
        }
        this.position1 = this.region.getMinimumPoint();
        this.position2 = this.region.getMaximumPoint();
        this.region.setPos1(this.position1);
        this.region.setPos2(this.position2);
    }

    public ExtendingCuboidRegionSelector(@Nullable World world, BlockVector3 position1, BlockVector3 position2) {
        this(world);
        position1 = position1.getMinimum(position2);
        position2 = position1.getMaximum(position2);
        this.region.setPos1(position1);
        this.region.setPos2(position2);
    }

    @Override
    public boolean selectPrimary(BlockVector3 position, SelectorLimits limits) {
        if (position.equals((Object)this.position1) && position.equals((Object)this.position2)) {
            return false;
        }
        this.position1 = this.position2 = position;
        this.region.setPos1(this.position1);
        this.region.setPos2(this.position2);
        return true;
    }

    @Override
    public boolean selectSecondary(BlockVector3 position, SelectorLimits limits) {
        if (this.position1 == null || this.position2 == null) {
            return this.selectPrimary(position, limits);
        }
        if (this.region.contains(position)) {
            return false;
        }
        double x1 = Math.min(position.x(), this.position1.x());
        double y1 = Math.min(position.y(), this.position1.y());
        double z1 = Math.min(position.z(), this.position1.z());
        double x2 = Math.max(position.x(), this.position2.x());
        double y2 = Math.max(position.y(), this.position2.y());
        double z2 = Math.max(position.z(), this.position2.z());
        BlockVector3 o1 = this.position1;
        BlockVector3 o2 = this.position2;
        this.position1 = BlockVector3.at(x1, y1, z1);
        this.position2 = BlockVector3.at(x2, y2, z2);
        this.region.setPos1(this.position1);
        this.region.setPos2(this.position2);
        assert (this.region.contains(o1));
        assert (this.region.contains(o2));
        assert (this.region.contains(position));
        return true;
    }

    @Override
    public void explainPrimarySelection(Actor player, LocalSession session, BlockVector3 pos) {
        player.printInfo(TranslatableComponent.of("worldedit.selection.extend.explain.primary", TextComponent.of(pos.toString()), TextComponent.of(this.region.getVolume())));
        this.explainRegionAdjust(player, session);
    }

    @Override
    public void explainSecondarySelection(Actor player, LocalSession session, BlockVector3 pos) {
        player.printInfo(TranslatableComponent.of("worldedit.selection.extend.explain.secondary", TextComponent.of(pos.toString()), TextComponent.of(this.region.getVolume())));
        this.explainRegionAdjust(player, session);
    }
}

