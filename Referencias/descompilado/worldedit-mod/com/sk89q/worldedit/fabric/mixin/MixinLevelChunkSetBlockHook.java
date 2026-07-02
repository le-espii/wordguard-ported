/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.minecraft.class_1923
 *  net.minecraft.class_1937
 *  net.minecraft.class_1959
 *  net.minecraft.class_2338
 *  net.minecraft.class_2378
 *  net.minecraft.class_2680
 *  net.minecraft.class_2791
 *  net.minecraft.class_2818
 *  net.minecraft.class_2826
 *  net.minecraft.class_2843
 *  net.minecraft.class_5539
 *  net.minecraft.class_6749
 *  net.minecraft.server.MinecraftServer
 *  org.jetbrains.annotations.Nullable
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Redirect
 *  org.spongepowered.asm.mixin.injection.Slice
 */
package com.sk89q.worldedit.fabric.mixin;

import com.sk89q.worldedit.fabric.internal.ExtendedChunk;
import net.minecraft.class_1923;
import net.minecraft.class_1937;
import net.minecraft.class_1959;
import net.minecraft.class_2338;
import net.minecraft.class_2378;
import net.minecraft.class_2680;
import net.minecraft.class_2791;
import net.minecraft.class_2818;
import net.minecraft.class_2826;
import net.minecraft.class_2843;
import net.minecraft.class_5539;
import net.minecraft.class_6749;
import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(value={class_2818.class})
public abstract class MixinLevelChunkSetBlockHook
extends class_2791
implements ExtendedChunk {
    @Unique
    private boolean shouldUpdate = true;

    public MixinLevelChunkSetBlockHook(class_1923 chunkPos, class_2843 upgradeData, class_5539 levelHeightAccessor, class_2378<class_1959> registry, long l, @Nullable class_2826[] levelChunkSections, @Nullable class_6749 blendingData) {
        super(chunkPos, upgradeData, levelHeightAccessor, registry, l, levelChunkSections, blendingData);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    @Unique
    @javax.annotation.Nullable
    public class_2680 setBlockState(class_2338 pos, class_2680 state, boolean moved, boolean update) {
        this.shouldUpdate = update;
        try {
            class_2680 class_26802 = this.method_12010(pos, state, moved);
            return class_26802;
        }
        finally {
            this.shouldUpdate = true;
        }
    }

    @Redirect(method={"setBlockState"}, slice=@Slice(from=@At(value="INVOKE", target="Lnet/minecraft/world/level/block/state/BlockState;is(Lnet/minecraft/world/level/block/Block;)Z")), at=@At(value="INVOKE", target="Lnet/minecraft/world/level/block/state/BlockState;onPlace(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Z)V"))
    public void setBlockStateHook(class_2680 target, class_1937 world, class_2338 pos, class_2680 old, boolean move) {
        MinecraftServer server = world.method_8503();
        boolean localShouldUpdate = server == null || Thread.currentThread() != server.method_3777() ? true : this.shouldUpdate;
        if (localShouldUpdate) {
            target.method_26182(world, pos, old, move);
        }
    }
}

