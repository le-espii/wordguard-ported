/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.sk89q.worldedit.world.registry;

import com.sk89q.worldedit.registry.state.Property;
import com.sk89q.worldedit.util.formatting.text.Component;
import com.sk89q.worldedit.util.formatting.text.TextComponent;
import com.sk89q.worldedit.util.formatting.text.TranslatableComponent;
import com.sk89q.worldedit.util.translation.TranslationManager;
import com.sk89q.worldedit.world.block.BlockState;
import com.sk89q.worldedit.world.block.BlockType;
import com.sk89q.worldedit.world.registry.BlockMaterial;
import com.sk89q.worldedit.world.registry.BlockRegistry;
import com.sk89q.worldedit.world.registry.BundledBlockData;
import com.sk89q.worldedit.world.registry.PassthroughBlockMaterial;
import java.util.Collections;
import java.util.Map;
import java.util.OptionalInt;
import javax.annotation.Nullable;

public class BundledBlockRegistry
implements BlockRegistry {
    @Override
    public Component getRichName(BlockType blockType) {
        BundledBlockData.BlockEntry blockEntry = BundledBlockData.getInstance().findById(blockType.id());
        if (blockEntry != null) {
            return TextComponent.of(blockEntry.localizedName);
        }
        return TranslatableComponent.of(TranslationManager.makeTranslationKey("block", blockType.id()));
    }

    @Override
    @Nullable
    @Deprecated
    public String getName(BlockType blockType) {
        BundledBlockData.BlockEntry blockEntry = BundledBlockData.getInstance().findById(blockType.id());
        return blockEntry != null ? blockEntry.localizedName : null;
    }

    @Override
    @Nullable
    public BlockMaterial getMaterial(BlockType blockType) {
        return new PassthroughBlockMaterial(BundledBlockData.getInstance().getMaterialById(blockType.id()));
    }

    @Override
    @Nullable
    public Map<String, ? extends Property<?>> getProperties(BlockType blockType) {
        return Collections.emptyMap();
    }

    @Override
    public OptionalInt getInternalBlockStateId(BlockState state) {
        return OptionalInt.empty();
    }
}

