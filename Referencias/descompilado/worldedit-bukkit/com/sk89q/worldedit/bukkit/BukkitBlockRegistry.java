/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  org.bukkit.Material
 */
package com.sk89q.worldedit.bukkit;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.registry.state.Property;
import com.sk89q.worldedit.util.formatting.text.Component;
import com.sk89q.worldedit.world.block.BlockState;
import com.sk89q.worldedit.world.block.BlockType;
import com.sk89q.worldedit.world.registry.BlockMaterial;
import com.sk89q.worldedit.world.registry.BundledBlockRegistry;
import com.sk89q.worldedit.world.registry.PassthroughBlockMaterial;
import java.util.HashMap;
import java.util.Map;
import java.util.OptionalInt;
import javax.annotation.Nullable;
import org.bukkit.Material;

public class BukkitBlockRegistry
extends BundledBlockRegistry {
    private final Map<Material, BukkitBlockMaterial> materialMap = new HashMap<Material, BukkitBlockMaterial>();

    @Override
    public Component getRichName(BlockType blockType) {
        if (WorldEditPlugin.getInstance().getBukkitImplAdapter() != null) {
            return WorldEditPlugin.getInstance().getBukkitImplAdapter().getRichBlockName(blockType);
        }
        return super.getRichName(blockType);
    }

    @Override
    @Nullable
    public BlockMaterial getMaterial(BlockType blockType) {
        Material mat = BukkitAdapter.adapt(blockType);
        if (mat == null) {
            return null;
        }
        return this.materialMap.computeIfAbsent(mat, material -> new BukkitBlockMaterial(BukkitBlockRegistry.super.getMaterial(blockType), (Material)material));
    }

    @Override
    @Nullable
    public Map<String, ? extends Property<?>> getProperties(BlockType blockType) {
        if (WorldEditPlugin.getInstance().getBukkitImplAdapter() != null) {
            return WorldEditPlugin.getInstance().getBukkitImplAdapter().getProperties(blockType);
        }
        return super.getProperties(blockType);
    }

    @Override
    public OptionalInt getInternalBlockStateId(BlockState state) {
        if (WorldEditPlugin.getInstance().getBukkitImplAdapter() != null) {
            return WorldEditPlugin.getInstance().getBukkitImplAdapter().getInternalBlockStateId(state);
        }
        return OptionalInt.empty();
    }

    public static class BukkitBlockMaterial
    extends PassthroughBlockMaterial {
        private final Material material;

        public BukkitBlockMaterial(@Nullable BlockMaterial material, Material bukkitMaterial) {
            super(material);
            this.material = bukkitMaterial;
        }

        @Override
        public boolean isAir() {
            switch (this.material) {
                case AIR: 
                case CAVE_AIR: 
                case VOID_AIR: {
                    return true;
                }
            }
            return false;
        }

        @Override
        public boolean isSolid() {
            return this.material.isSolid();
        }

        @Override
        public boolean isBurnable() {
            return this.material.isBurnable();
        }

        @Override
        public boolean isTranslucent() {
            return this.material.isTransparent();
        }
    }
}

