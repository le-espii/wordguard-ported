/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.Material
 *  org.bukkit.NamespacedKey
 *  org.bukkit.Tag
 */
package com.sk89q.worldedit.bukkit;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.world.block.BlockType;
import com.sk89q.worldedit.world.registry.BlockCategoryRegistry;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Tag;

public class BukkitBlockCategoryRegistry
implements BlockCategoryRegistry {
    private Set<BlockType> getFromBukkitTag(Tag<Material> tag) {
        return tag.getValues().stream().map(BukkitAdapter::asBlockType).collect(Collectors.toSet());
    }

    @Override
    public Set<BlockType> getCategorisedByName(String category) {
        String key;
        String[] split = category.split(":");
        String namespace = split.length > 1 ? split[0] : "minecraft";
        Tag tag = Bukkit.getTag((String)"blocks", (NamespacedKey)new NamespacedKey(namespace, key = split.length > 1 ? split[1] : category), Material.class);
        if (tag == null) {
            return Collections.emptySet();
        }
        return this.getFromBukkitTag((Tag<Material>)tag);
    }
}

