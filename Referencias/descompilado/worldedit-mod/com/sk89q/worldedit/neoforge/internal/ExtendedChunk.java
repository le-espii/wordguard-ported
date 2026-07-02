/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.minecraft.core.BlockPos
 *  net.minecraft.world.level.block.state.BlockState
 */
package com.sk89q.worldedit.neoforge.internal;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public interface ExtendedChunk {
    @Nullable
    public BlockState setBlockState(BlockPos var1, BlockState var2, boolean var3, boolean var4);
}

