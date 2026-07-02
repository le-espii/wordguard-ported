/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 *  net.minecraft.core.Holder
 *  net.minecraft.core.HolderSet$ListBacked
 *  net.minecraft.core.registries.Registries
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.tags.TagKey
 *  net.neoforged.neoforge.server.ServerLifecycleHooks
 */
package com.sk89q.worldedit.neoforge;

import com.google.common.collect.ImmutableSet;
import com.sk89q.worldedit.neoforge.NeoForgeAdapter;
import com.sk89q.worldedit.world.item.ItemType;
import com.sk89q.worldedit.world.registry.ItemCategoryRegistry;
import java.util.Set;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

public class NeoForgeItemCategoryRegistry
implements ItemCategoryRegistry {
    @Override
    public Set<ItemType> getCategorisedByName(String category) {
        return (Set)ServerLifecycleHooks.getCurrentServer().registryAccess().registryOrThrow(Registries.ITEM).getTag(TagKey.create((ResourceKey)Registries.ITEM, (ResourceLocation)ResourceLocation.parse((String)category))).stream().flatMap(HolderSet.ListBacked::stream).map(Holder::value).map(NeoForgeAdapter::adapt).collect(ImmutableSet.toImmutableSet());
    }
}

