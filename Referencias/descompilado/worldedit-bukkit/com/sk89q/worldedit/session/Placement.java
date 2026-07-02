/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.session;

import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.extension.platform.Actor;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.RegionSelector;
import com.sk89q.worldedit.session.PlacementType;
import com.sk89q.worldedit.util.formatting.text.Component;
import com.sk89q.worldedit.util.formatting.text.TextComponent;
import com.sk89q.worldedit.util.formatting.text.TranslatableComponent;

public record Placement(PlacementType placementType, BlockVector3 offset) {
    public BlockVector3 getPlacementPosition(RegionSelector selector, Actor actor) throws IncompleteRegionException {
        return this.placementType.getPlacementPosition(selector, actor).add(this.offset);
    }

    public boolean canBeUsedBy(Actor actor) {
        return this.placementType.canBeUsedBy(actor);
    }

    public Component getInfo() {
        if (this.offset.equals((Object)BlockVector3.ZERO)) {
            return TranslatableComponent.of(this.placementType.getTranslationKey());
        }
        return TranslatableComponent.of(this.placementType.getTranslationKeyWithOffset(), TextComponent.of(this.offset.x()), TextComponent.of(this.offset.y()), TextComponent.of(this.offset.z()));
    }
}

