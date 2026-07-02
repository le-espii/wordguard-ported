/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.HashMultimap
 *  com.google.common.collect.Multimap
 *  com.sk89q.worldedit.bukkit.BukkitAdapter
 *  org.bukkit.Material
 *  org.bukkit.block.Block
 *  org.bukkit.block.BlockState
 *  org.bukkit.inventory.ItemStack
 */
package com.sk89q.worldguard.bukkit.internal;

import com.google.common.base.Preconditions;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.blacklist.target.BlockTarget;
import com.sk89q.worldguard.blacklist.target.ItemTarget;
import com.sk89q.worldguard.blacklist.target.Target;
import com.sk89q.worldguard.blacklist.target.TargetMatcher;
import java.util.Collection;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.inventory.ItemStack;

public class TargetMatcherSet {
    private final Multimap<String, TargetMatcher> entries = HashMultimap.create();

    public boolean add(TargetMatcher matcher) {
        Preconditions.checkNotNull((Object)matcher);
        return this.entries.put((Object)matcher.getMatchedTypeId(), (Object)matcher);
    }

    public boolean test(Target target) {
        Collection matchers = this.entries.get((Object)target.getTypeId());
        for (TargetMatcher matcher : matchers) {
            if (!matcher.test(target)) continue;
            return true;
        }
        return false;
    }

    public boolean test(Material material) {
        if (material.isBlock()) {
            return this.test(new BlockTarget(BukkitAdapter.asBlockType((Material)material)));
        }
        return this.test(new ItemTarget(BukkitAdapter.asItemType((Material)material)));
    }

    public boolean test(Block block) {
        return this.test(new BlockTarget(BukkitAdapter.asBlockType((Material)block.getType())));
    }

    public boolean test(BlockState state) {
        return this.test(new BlockTarget(BukkitAdapter.asBlockType((Material)state.getType())));
    }

    public boolean test(ItemStack itemStack) {
        return this.test(new ItemTarget(BukkitAdapter.asItemType((Material)itemStack.getType())));
    }

    public String toString() {
        return this.entries.toString();
    }
}

