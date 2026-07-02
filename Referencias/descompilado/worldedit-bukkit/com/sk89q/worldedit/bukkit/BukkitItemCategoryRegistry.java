/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  org.bukkit.Bukkit
 *  org.bukkit.Material
 *  org.bukkit.NamespacedKey
 *  org.bukkit.Tag
 */
package com.sk89q.worldedit.bukkit;

import com.google.common.base.Preconditions;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.world.item.ItemType;
import com.sk89q.worldedit.world.registry.ItemCategoryRegistry;
import java.util.Set;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Tag;

public class BukkitItemCategoryRegistry
implements ItemCategoryRegistry {
    private Set<ItemType> getFromBukkitTag(Tag<Material> tag) {
        return tag.getValues().stream().map(BukkitAdapter::asItemType).collect(Collectors.toSet());
    }

    @Override
    public Set<ItemType> getCategorisedByName(String category) {
        String[] split = category.split(":");
        String namespace = split.length > 1 ? split[0] : "minecraft";
        String key = split.length > 1 ? split[1] : category;
        Tag tag = Bukkit.getTag((String)"items", (NamespacedKey)new NamespacedKey(namespace, key), Material.class);
        Preconditions.checkNotNull((Object)tag);
        return this.getFromBukkitTag((Tag<Material>)tag);
    }
}

