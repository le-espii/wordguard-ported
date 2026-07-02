/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.world.registry;

import com.sk89q.worldedit.world.block.BlockType;
import com.sk89q.worldedit.world.registry.BlockCategoryRegistry;
import java.util.Collections;
import java.util.Set;

public class NullBlockCategoryRegistry
implements BlockCategoryRegistry {
    @Override
    public Set<BlockType> getCategorisedByName(String category) {
        return Collections.emptySet();
    }
}

