/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.sk89q.worldedit.bukkit.BukkitAdapter
 *  org.bukkit.Material
 *  org.bukkit.block.Block
 *  org.bukkit.inventory.ItemStack
 */
package com.sk89q.worldguard.bukkit;

import com.google.common.base.Preconditions;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.blacklist.target.BlockTarget;
import com.sk89q.worldguard.blacklist.target.ItemTarget;
import com.sk89q.worldguard.blacklist.target.Target;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

public class BukkitUtil {
    private BukkitUtil() {
    }

    public static boolean isWaterPotion(ItemStack item) {
        return (item.getDurability() & 0x3F) == 0;
    }

    public static int getPotionEffectBits(ItemStack item) {
        return item.getDurability() & 0x3F;
    }

    public static Target createTarget(Block block) {
        Preconditions.checkNotNull((Object)block);
        Preconditions.checkNotNull((Object)block.getType());
        return BukkitUtil.createTarget(block.getType());
    }

    public static Target createTarget(ItemStack item) {
        Preconditions.checkNotNull((Object)item);
        Preconditions.checkNotNull((Object)item.getType());
        return BukkitUtil.createTarget(item.getType());
    }

    public static Target createTarget(Material material) {
        Preconditions.checkNotNull((Object)material);
        if (material.isBlock()) {
            return new BlockTarget(BukkitAdapter.asBlockType((Material)material));
        }
        return new ItemTarget(BukkitAdapter.asItemType((Material)material));
    }
}

