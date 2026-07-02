/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.extent.inventory;

import com.sk89q.worldedit.extent.inventory.BlockBagException;
import com.sk89q.worldedit.world.block.BlockType;

public class OutOfSpaceException
extends BlockBagException {
    private final BlockType type;

    public OutOfSpaceException(BlockType type) {
        this.type = type;
    }

    public BlockType getType() {
        return this.type;
    }
}

