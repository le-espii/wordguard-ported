/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 */
package com.sk89q.worldedit.session;

import com.google.common.base.Preconditions;
import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.extension.platform.Actor;
import com.sk89q.worldedit.extension.platform.Locatable;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.RegionSelector;

public enum PlacementType {
    WORLD("worldedit.toggleplace.world", "worldedit.toggleplace.world-offset"){

        @Override
        public BlockVector3 getPlacementPosition(RegionSelector selector, Actor actor) throws IncompleteRegionException {
            return BlockVector3.ZERO;
        }
    }
    ,
    PLAYER("worldedit.toggleplace.player", "worldedit.toggleplace.player-offset"){

        @Override
        public BlockVector3 getPlacementPosition(RegionSelector selector, Actor actor) throws IncompleteRegionException {
            if (!this.canBeUsedBy(actor)) {
                throw new IncompleteRegionException();
            }
            return ((Locatable)((Object)actor)).getBlockLocation().toVector().toBlockPoint();
        }

        @Override
        public boolean canBeUsedBy(Actor actor) {
            Preconditions.checkNotNull((Object)actor);
            return actor instanceof Locatable;
        }
    }
    ,
    HERE(null, null){

        @Override
        public BlockVector3 getPlacementPosition(RegionSelector selector, Actor actor) throws IncompleteRegionException {
            throw new IllegalStateException("PlacementType.HERE cannot be used. Use PLAYER or WORLD instead.");
        }

        @Override
        public boolean canBeUsedBy(Actor actor) {
            return PLAYER.canBeUsedBy(actor);
        }

        @Override
        public String getTranslationKey() {
            throw new IllegalStateException("PlacementType.HERE cannot be used. Use PLAYER or WORLD instead.");
        }

        @Override
        public String getTranslationKeyWithOffset() {
            throw new IllegalStateException("PlacementType.HERE cannot be used. Use PLAYER or WORLD instead.");
        }
    }
    ,
    POS1("worldedit.toggleplace.pos1", "worldedit.toggleplace.pos1-offset"){

        @Override
        public BlockVector3 getPlacementPosition(RegionSelector selector, Actor actor) throws IncompleteRegionException {
            return selector.getPrimaryPosition();
        }
    }
    ,
    MIN("worldedit.toggleplace.min", "worldedit.toggleplace.min-offset"){

        @Override
        public BlockVector3 getPlacementPosition(RegionSelector selector, Actor actor) throws IncompleteRegionException {
            return selector.getRegion().getMinimumPoint();
        }
    }
    ,
    MAX("worldedit.toggleplace.max", "worldedit.toggleplace.max-offset"){

        @Override
        public BlockVector3 getPlacementPosition(RegionSelector selector, Actor actor) throws IncompleteRegionException {
            return selector.getRegion().getMaximumPoint();
        }
    };

    private final String translationKey;
    private final String translationKeyWithOffset;

    private PlacementType(String translationKey, String translationKeyWithOffset) {
        this.translationKey = translationKey;
        this.translationKeyWithOffset = translationKeyWithOffset;
    }

    public abstract BlockVector3 getPlacementPosition(RegionSelector var1, Actor var2) throws IncompleteRegionException;

    public boolean canBeUsedBy(Actor actor) {
        return true;
    }

    public String getTranslationKey() {
        return this.translationKey;
    }

    public String getTranslationKeyWithOffset() {
        return this.translationKeyWithOffset;
    }
}

