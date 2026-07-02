/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sk89q.worldedit.world.block.BlockType
 */
package com.sk89q.worldguard.blacklist.target;

import com.sk89q.worldedit.world.block.BlockType;
import com.sk89q.worldguard.blacklist.target.Target;
import com.sk89q.worldguard.blacklist.target.TargetMatcher;

public class BlockMatcher
implements TargetMatcher {
    private final BlockType type;

    public BlockMatcher(BlockType type) {
        this.type = type;
    }

    @Override
    public String getMatchedTypeId() {
        return this.type.getId();
    }

    @Override
    public boolean test(Target target) {
        return target.getTypeId().equals(this.getMatchedTypeId());
    }
}

