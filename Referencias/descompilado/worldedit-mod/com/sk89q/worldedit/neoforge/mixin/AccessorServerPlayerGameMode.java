/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.server.level.ServerPlayerGameMode
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.gen.Accessor
 */
package com.sk89q.worldedit.neoforge.mixin;

import net.minecraft.server.level.ServerPlayerGameMode;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value={ServerPlayerGameMode.class})
public interface AccessorServerPlayerGameMode {
    @Accessor(value="isDestroyingBlock")
    public boolean isDestroyingBlock();
}

