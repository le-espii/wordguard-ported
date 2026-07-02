package com.sk89q.worldguard.mod.mixin;

import com.sk89q.worldguard.mod.CommonWorldGuardEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.biome.Biome;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Biome.class)
public abstract class BiomeMixin {

    @Inject(method = "shouldFreeze", at = @At("HEAD"), cancellable = true)
    private void onShouldFreeze(LevelReader levelReader, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        if (levelReader instanceof Level) {
            Level level = (Level) levelReader;
            if (!CommonWorldGuardEvents.canFreeze(level, pos)) {
                cir.setReturnValue(false);
            }
        }
    }

    @Inject(method = "shouldSnow", at = @At("HEAD"), cancellable = true)
    private void onShouldSnow(LevelReader levelReader, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        if (levelReader instanceof Level) {
            Level level = (Level) levelReader;
            if (!CommonWorldGuardEvents.canSnow(level, pos)) {
                cir.setReturnValue(false);
            }
        }
    }
}
