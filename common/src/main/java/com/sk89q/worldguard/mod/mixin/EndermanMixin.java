package com.sk89q.worldguard.mod.mixin;

import com.sk89q.worldguard.mod.CommonWorldGuardEvents;
import net.minecraft.world.entity.monster.EnderMan;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "net.minecraft.world.entity.monster.EnderMan$EndermanTakeBlockGoal")
public abstract class EndermanMixin {

    @Shadow @Final EnderMan enderman;

    @Inject(method = "canUse", at = @At("HEAD"), cancellable = true)
    private void onCanUse(CallbackInfoReturnable<Boolean> cir) {
        if (!CommonWorldGuardEvents.canEndermanGrief(this.enderman.level(), this.enderman.blockPosition())) {
            cir.setReturnValue(false);
        }
    }
}
