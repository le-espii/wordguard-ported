/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.material.PushReaction
 */
package com.sk89q.worldedit.neoforge;

import com.sk89q.worldedit.world.registry.BlockMaterial;
import com.sk89q.worldedit.world.registry.PassthroughBlockMaterial;
import javax.annotation.Nullable;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.PushReaction;

public class NeoForgeBlockMaterial
extends PassthroughBlockMaterial {
    private final BlockState block;

    public NeoForgeBlockMaterial(BlockState block, @Nullable BlockMaterial secondary) {
        super(secondary);
        this.block = block;
    }

    @Override
    public boolean isAir() {
        return this.block.isAir() || super.isAir();
    }

    @Override
    public boolean isOpaque() {
        return this.block.canOcclude();
    }

    @Override
    public boolean isLiquid() {
        return this.block.liquid();
    }

    @Override
    public boolean isSolid() {
        return this.block.isSolid();
    }

    @Override
    public boolean isFragileWhenPushed() {
        return this.block.getPistonPushReaction() == PushReaction.DESTROY;
    }

    @Override
    public boolean isUnpushable() {
        return this.block.getPistonPushReaction() == PushReaction.BLOCK;
    }

    @Override
    public boolean isMovementBlocker() {
        return this.block.blocksMotion();
    }

    @Override
    public boolean isBurnable() {
        return this.block.ignitedByLava();
    }

    @Override
    public boolean isToolRequired() {
        return !this.block.requiresCorrectToolForDrops();
    }

    @Override
    public boolean isReplacedDuringPlacement() {
        return this.block.canBeReplaced();
    }
}

