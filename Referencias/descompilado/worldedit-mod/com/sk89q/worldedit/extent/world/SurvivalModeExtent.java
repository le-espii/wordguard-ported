/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 */
package com.sk89q.worldedit.extent.world;

import com.google.common.base.Preconditions;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.extent.AbstractDelegateExtent;
import com.sk89q.worldedit.extent.Extent;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldedit.world.block.BlockStateHolder;
import org.enginehub.linbus.tree.LinCompoundTag;

public class SurvivalModeExtent
extends AbstractDelegateExtent {
    private final World world;
    private boolean toolUse = false;
    private boolean stripNbt = false;

    public SurvivalModeExtent(Extent extent, World world) {
        super(extent);
        Preconditions.checkNotNull((Object)world);
        this.world = world;
    }

    public boolean hasToolUse() {
        return this.toolUse;
    }

    public void setToolUse(boolean toolUse) {
        this.toolUse = toolUse;
    }

    public boolean hasStripNbt() {
        return this.stripNbt;
    }

    public void setStripNbt(boolean stripNbt) {
        this.stripNbt = stripNbt;
    }

    public <B extends BlockStateHolder<B>> boolean setBlock(BlockVector3 location, B block) throws WorldEditException {
        if (this.toolUse && block.getBlockType().getMaterial().isAir()) {
            this.world.simulateBlockMine(location);
            return true;
        }
        if (this.stripNbt) {
            return super.setBlock(location, block.toBaseBlock((LinCompoundTag)null));
        }
        return super.setBlock(location, block);
    }
}

