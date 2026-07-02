/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.world.block;

import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.extension.platform.Capability;
import com.sk89q.worldedit.registry.Category;
import com.sk89q.worldedit.registry.Keyed;
import com.sk89q.worldedit.registry.NamespacedRegistry;
import com.sk89q.worldedit.world.block.BlockStateHolder;
import com.sk89q.worldedit.world.block.BlockType;

public class BlockCategory
extends Category<BlockType>
implements Keyed {
    public static final NamespacedRegistry<BlockCategory> REGISTRY = new NamespacedRegistry("block tag", true);

    public BlockCategory(String id) {
        super(id, () -> WorldEdit.getInstance().getPlatformManager().queryCapability(Capability.GAME_HOOKS).getRegistries().getBlockCategoryRegistry().getCategorisedByName(id));
    }

    @Override
    public <B extends BlockStateHolder<B>> boolean contains(B blockStateHolder) {
        return this.getAll().contains(blockStateHolder.getBlockType());
    }
}

