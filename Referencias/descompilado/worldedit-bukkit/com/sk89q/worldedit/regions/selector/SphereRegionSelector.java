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
import com.sk89q.worldedit.math.Vector3;
import com.sk89q.worldedit.regions.RegionSelector;
import com.sk89q.worldedit.regions.selector.EllipsoidRegionSelector;
import com.sk89q.worldedit.regions.selector.limit.SelectorLimits;
import com.sk89q.worldedit.util.formatting.text.TextComponent;
import com.sk89q.worldedit.util.formatting.text.TranslatableComponent;
import com.sk89q.worldedit.world.World;
import javax.annotation.Nullable;

public class SphereRegionSelector
extends EllipsoidRegionSelector {
    public SphereRegionSelector() {
    }

    public SphereRegionSelector(@Nullable World world) {
        super(world);
    }

    public SphereRegionSelector(RegionSelector oldSelector) {
        super(oldSelector);
        if (this.selectedRadius) {
            Vector3 radius = this.region.getRadius();
            double radiusScalar = Math.max(Math.max(radius.x(), radius.y()), radius.z());
            this.region.setRadius(Vector3.at(radiusScalar, radiusScalar, radiusScalar));
        }
    }

    public SphereRegionSelector(@Nullable World world, BlockVector3 center, int radius) {
        super(world, center, Vector3.at(radius, radius, radius));
    }

    @Override
    public boolean selectSecondary(BlockVector3 position, SelectorLimits limits) {
        if (!this.started) {
            return false;
        }
        double radiusScalar = Math.ceil(position.toVector3().distance(this.region.getCenter()));
        this.region.setRadius(Vector3.at(radiusScalar, radiusScalar, radiusScalar));
        this.selectedRadius = true;
        return true;
    }

    @Override
    public void explainSecondarySelection(Actor player, LocalSession session, BlockVector3 pos) {
        if (this.isDefined()) {
            player.printInfo(TranslatableComponent.of("worldedit.selection.sphere.explain.secondary-defined", TextComponent.of(this.region.getRadius().x()), TextComponent.of(this.region.getVolume())));
        } else {
            player.printInfo(TranslatableComponent.of("worldedit.selection.sphere.explain.secondary", TextComponent.of(this.region.getRadius().x())));
        }
        session.describeCUI(player);
    }

    @Override
    public String getTypeName() {
        return "sphere";
    }
}

