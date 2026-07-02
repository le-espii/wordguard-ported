/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.fabric;

import com.sk89q.worldedit.blocks.BaseItemStack;
import com.sk89q.worldedit.fabric.FabricAdapter;
import com.sk89q.worldedit.util.formatting.text.Component;
import com.sk89q.worldedit.util.formatting.text.TranslatableComponent;
import com.sk89q.worldedit.world.item.ItemType;
import com.sk89q.worldedit.world.registry.BundledItemRegistry;

public class FabricItemRegistry
extends BundledItemRegistry {
    @Override
    public Component getRichName(ItemType itemType) {
        return TranslatableComponent.of(FabricAdapter.adapt(itemType).method_7876());
    }

    @Override
    public Component getRichName(BaseItemStack itemStack) {
        return TranslatableComponent.of(FabricAdapter.adapt(itemStack).method_7922());
    }
}

