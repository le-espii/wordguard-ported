/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_2248
 *  net.minecraft.class_2680
 *  net.minecraft.class_2769
 */
package com.sk89q.worldedit.fabric;

import com.sk89q.worldedit.fabric.FabricAdapter;
import com.sk89q.worldedit.fabric.FabricBlockMaterial;
import com.sk89q.worldedit.fabric.internal.FabricTransmogrifier;
import com.sk89q.worldedit.registry.state.Property;
import com.sk89q.worldedit.util.formatting.text.Component;
import com.sk89q.worldedit.util.formatting.text.TranslatableComponent;
import com.sk89q.worldedit.world.block.BlockState;
import com.sk89q.worldedit.world.block.BlockType;
import com.sk89q.worldedit.world.registry.BlockMaterial;
import com.sk89q.worldedit.world.registry.BundledBlockRegistry;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.OptionalInt;
import java.util.TreeMap;
import net.minecraft.class_2248;
import net.minecraft.class_2680;
import net.minecraft.class_2769;

public class FabricBlockRegistry
extends BundledBlockRegistry {
    private final Map<class_2680, FabricBlockMaterial> materialMap = new HashMap<class_2680, FabricBlockMaterial>();

    @Override
    public Component getRichName(BlockType blockType) {
        return TranslatableComponent.of(FabricAdapter.adapt(blockType).method_9539());
    }

    @Override
    public BlockMaterial getMaterial(BlockType blockType) {
        class_2248 block = FabricAdapter.adapt(blockType);
        return this.materialMap.computeIfAbsent(block.method_9564(), m -> new FabricBlockMaterial((class_2680)m, super.getMaterial(blockType)));
    }

    @Override
    public Map<String, ? extends Property<?>> getProperties(BlockType blockType) {
        class_2248 block = FabricAdapter.adapt(blockType);
        TreeMap map = new TreeMap();
        Collection propertyKeys = block.method_9564().method_28501();
        for (class_2769 key : propertyKeys) {
            map.put(key.method_11899(), FabricTransmogrifier.transmogToWorldEditProperty(key));
        }
        return map;
    }

    @Override
    public OptionalInt getInternalBlockStateId(BlockState state) {
        class_2680 equivalent = FabricAdapter.adapt(state);
        return OptionalInt.of(class_2248.method_9507((class_2680)equivalent));
    }
}

