/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.bukkit;

import com.sk89q.worldedit.blocks.BaseItemStack;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.util.formatting.text.Component;
import com.sk89q.worldedit.world.item.ItemType;
import com.sk89q.worldedit.world.registry.BundledItemRegistry;

class BukkitItemRegistry
extends BundledItemRegistry {
    BukkitItemRegistry() {
    }

    @Override
    public Component getRichName(ItemType itemType) {
        if (WorldEditPlugin.getInstance().getBukkitImplAdapter() != null) {
            return WorldEditPlugin.getInstance().getBukkitImplAdapter().getRichItemName(itemType);
        }
        return super.getRichName(itemType);
    }

    @Override
    public Component getRichName(BaseItemStack itemStack) {
        if (WorldEditPlugin.getInstance().getBukkitImplAdapter() != null) {
            return WorldEditPlugin.getInstance().getBukkitImplAdapter().getRichItemName(itemStack);
        }
        return super.getRichName(itemStack);
    }
}

