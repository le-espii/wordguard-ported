/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.sk89q.worldedit.world.registry;

import com.sk89q.worldedit.blocks.BaseItemStack;
import com.sk89q.worldedit.util.formatting.text.Component;
import com.sk89q.worldedit.world.item.ItemType;
import com.sk89q.worldedit.world.registry.ItemMaterial;
import javax.annotation.Nullable;

public interface ItemRegistry {
    public Component getRichName(ItemType var1);

    default public Component getRichName(BaseItemStack itemStack) {
        return this.getRichName(itemStack.getType());
    }

    @Deprecated
    @Nullable
    default public String getName(ItemType itemType) {
        return this.getRichName(itemType).toString();
    }

    @Nullable
    public ItemMaterial getMaterial(ItemType var1);
}

