/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.neoforge;

import com.sk89q.worldedit.blocks.BaseItemStack;
import com.sk89q.worldedit.neoforge.NeoForgeAdapter;
import com.sk89q.worldedit.util.formatting.text.Component;
import com.sk89q.worldedit.util.formatting.text.TranslatableComponent;
import com.sk89q.worldedit.world.item.ItemType;
import com.sk89q.worldedit.world.registry.BundledItemRegistry;

public class NeoForgeItemRegistry
extends BundledItemRegistry {
    @Override
    public Component getRichName(ItemType itemType) {
        return TranslatableComponent.of(NeoForgeAdapter.adapt(itemType).getDescriptionId());
    }

    @Override
    public Component getRichName(BaseItemStack itemStack) {
        return TranslatableComponent.of(NeoForgeAdapter.adapt(itemStack).getDescriptionId());
    }
}

