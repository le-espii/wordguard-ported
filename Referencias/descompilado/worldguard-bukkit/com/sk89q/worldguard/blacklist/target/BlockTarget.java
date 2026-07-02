/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sk89q.worldedit.world.block.BlockType
 */
package com.sk89q.worldguard.blacklist.target;

import com.sk89q.worldedit.world.block.BlockType;
import com.sk89q.worldguard.blacklist.target.Target;

public class BlockTarget
implements Target {
    private BlockType type;

    public BlockTarget(BlockType type) {
        this.type = type;
    }

    @Override
    public String getTypeId() {
        return this.type.getId();
    }

    @Override
    public String getFriendlyName() {
        return this.type.getName();
    }
}

