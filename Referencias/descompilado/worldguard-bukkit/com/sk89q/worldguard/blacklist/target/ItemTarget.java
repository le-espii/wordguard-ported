/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sk89q.worldedit.world.item.ItemType
 */
package com.sk89q.worldguard.blacklist.target;

import com.sk89q.worldedit.world.item.ItemType;
import com.sk89q.worldguard.blacklist.target.Target;

public class ItemTarget
implements Target {
    private ItemType type;

    public ItemTarget(ItemType type) {
        this.type = type;
    }

    @Override
    public String getTypeId() {
        return this.type.getId();
    }

    @Override
    public String getFriendlyName() {
        return this.type.getName();
    }
}

