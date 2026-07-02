/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.sk89q.worldedit.world.registry;

import com.sk89q.worldedit.util.formatting.text.Component;
import com.sk89q.worldedit.util.formatting.text.TextComponent;
import com.sk89q.worldedit.util.formatting.text.TranslatableComponent;
import com.sk89q.worldedit.util.translation.TranslationManager;
import com.sk89q.worldedit.world.item.ItemType;
import com.sk89q.worldedit.world.registry.BundledItemData;
import com.sk89q.worldedit.world.registry.ItemMaterial;
import com.sk89q.worldedit.world.registry.ItemRegistry;
import com.sk89q.worldedit.world.registry.PassthroughItemMaterial;
import javax.annotation.Nullable;

public class BundledItemRegistry
implements ItemRegistry {
    private BundledItemData.ItemEntry getEntryById(ItemType itemType) {
        return BundledItemData.getInstance().findById(itemType.id());
    }

    @Override
    public Component getRichName(ItemType itemType) {
        BundledItemData.ItemEntry itemEntry = this.getEntryById(itemType);
        if (itemEntry != null && !itemEntry.localizedName.equals("Air")) {
            return TextComponent.of(itemEntry.localizedName);
        }
        return TranslatableComponent.of(TranslationManager.makeTranslationKey("item", itemType.id()));
    }

    @Override
    @Nullable
    @Deprecated
    public String getName(ItemType itemType) {
        BundledItemData.ItemEntry itemEntry = this.getEntryById(itemType);
        if (itemEntry != null) {
            String localized = itemEntry.localizedName;
            if (localized.equals("Air")) {
                String id = itemType.id();
                int c = id.indexOf(58);
                return c < 0 ? id : id.substring(c + 1);
            }
            return localized;
        }
        return null;
    }

    @Override
    @Nullable
    public ItemMaterial getMaterial(ItemType itemType) {
        return new PassthroughItemMaterial(BundledItemData.getInstance().getMaterialById(itemType.id()));
    }
}

