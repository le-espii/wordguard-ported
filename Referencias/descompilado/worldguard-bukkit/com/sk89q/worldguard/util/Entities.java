/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sk89q.worldedit.entity.Entity
 *  com.sk89q.worldedit.entity.metadata.EntityProperties
 */
package com.sk89q.worldguard.util;

import com.sk89q.worldedit.entity.Entity;
import com.sk89q.worldedit.entity.metadata.EntityProperties;

public class Entities {
    private Entities() {
    }

    public static boolean isIntensiveEntity(Entity entity) {
        EntityProperties properties = (EntityProperties)entity.getFacet(EntityProperties.class);
        return properties != null && (properties.isItem() || properties.isTNT() || properties.isExperienceOrb() || properties.isFallingBlock() || properties.isLiving() && !properties.isTamed() && !properties.isPlayerDerived() && !properties.isArmorStand());
    }
}

