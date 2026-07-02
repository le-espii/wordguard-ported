/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 */
package com.sk89q.worldedit.neoforge;

import com.sk89q.worldedit.neoforge.NeoForgeAdapter;
import com.sk89q.worldedit.neoforge.NeoForgeBlockMaterial;
import com.sk89q.worldedit.neoforge.internal.NeoForgeTransmogrifier;
import com.sk89q.worldedit.registry.state.Property;
import com.sk89q.worldedit.util.formatting.text.Component;
import com.sk89q.worldedit.util.formatting.text.TranslatableComponent;
import com.sk89q.worldedit.world.block.BlockType;
import com.sk89q.worldedit.world.registry.BlockMaterial;
import com.sk89q.worldedit.world.registry.BundledBlockRegistry;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.OptionalInt;
import java.util.TreeMap;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class NeoForgeBlockRegistry
extends BundledBlockRegistry {
    private final Map<BlockState, NeoForgeBlockMaterial> materialMap = new HashMap<BlockState, NeoForgeBlockMaterial>();

    @Override
    public Component getRichName(BlockType blockType) {
        return TranslatableComponent.of(NeoForgeAdapter.adapt(blockType).getDescriptionId());
    }

    @Override
    public BlockMaterial getMaterial(BlockType blockType) {
        Block block = NeoForgeAdapter.adapt(blockType);
        if (block == null) {
            return super.getMaterial(blockType);
        }
        return this.materialMap.computeIfAbsent(block.defaultBlockState(), s -> new NeoForgeBlockMaterial((BlockState)s, super.getMaterial(blockType)));
    }

    @Override
    public Map<String, ? extends Property<?>> getProperties(BlockType blockType) {
        Block block = NeoForgeAdapter.adapt(blockType);
        TreeMap map = new TreeMap();
        Collection propertyKeys = block.defaultBlockState().getProperties();
        for (net.minecraft.world.level.block.state.properties.Property key : propertyKeys) {
            map.put(key.getName(), NeoForgeTransmogrifier.transmogToWorldEditProperty(key));
        }
        return map;
    }

    @Override
    public OptionalInt getInternalBlockStateId(com.sk89q.worldedit.world.block.BlockState state) {
        BlockState equivalent = NeoForgeAdapter.adapt(state);
        return OptionalInt.of(Block.getId((BlockState)equivalent));
    }
}

