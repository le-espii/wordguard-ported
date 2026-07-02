package com.sk89q.worldguard.mod.mixin;

import com.sk89q.worldguard.mod.CommonWorldGuardEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FarmBlock.class)
public abstract class FarmBlockMixin {

    @Inject(method = "turnToDirt", at = @At("HEAD"), cancellable = true)
    private static void onTurnToDirt(Entity entity, BlockState state, Level level, BlockPos pos, CallbackInfo ci) {
        if (!CommonWorldGuardEvents.canTrampleCrops(level, pos)) {
            ci.cancel();
        }
    }
}
