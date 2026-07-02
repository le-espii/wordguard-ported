/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.sk89q.worldedit.world.registry;

import com.sk89q.worldedit.registry.state.Property;
import com.sk89q.worldedit.util.formatting.text.Component;
import com.sk89q.worldedit.world.block.BlockState;
import com.sk89q.worldedit.world.block.BlockType;
import com.sk89q.worldedit.world.registry.BlockMaterial;
import java.util.Map;
import java.util.OptionalInt;
import javax.annotation.Nullable;

public interface BlockRegistry {
    public Component getRichName(BlockType var1);

    @Deprecated
    @Nullable
    default public String getName(BlockType blockType) {
        return this.getRichName(blockType).toString();
    }

    @Nullable
    public BlockMaterial getMaterial(BlockType var1);

    public Map<String, ? extends Property<?>> getProperties(BlockType var1);

    public OptionalInt getInternalBlockStateId(BlockState var1);
}

