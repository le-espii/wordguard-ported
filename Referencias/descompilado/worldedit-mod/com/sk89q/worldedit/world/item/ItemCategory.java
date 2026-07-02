/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.world.item;

import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.blocks.BaseItem;
import com.sk89q.worldedit.extension.platform.Capability;
import com.sk89q.worldedit.registry.Category;
import com.sk89q.worldedit.registry.Keyed;
import com.sk89q.worldedit.registry.NamespacedRegistry;
import com.sk89q.worldedit.world.item.ItemType;

public class ItemCategory
extends Category<ItemType>
implements Keyed {
    public static final NamespacedRegistry<ItemCategory> REGISTRY = new NamespacedRegistry("item tag");

    public ItemCategory(String id) {
        super(id, () -> WorldEdit.getInstance().getPlatformManager().queryCapability(Capability.GAME_HOOKS).getRegistries().getItemCategoryRegistry().getCategorisedByName(id));
    }

    @Override
    public boolean contains(BaseItem baseItem) {
        return this.getAll().contains(baseItem.getType());
    }
}

