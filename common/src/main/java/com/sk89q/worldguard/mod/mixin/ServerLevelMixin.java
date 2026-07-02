package com.sk89q.worldguard.mod.mixin;

import com.sk89q.worldguard.mod.CommonWorldGuardEvents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerLevel.class)
public abstract class ServerLevelMixin {

    @Inject(method = "addFreshEntity", at = @At("HEAD"), cancellable = true)
    private void onAddFreshEntity(Entity entity, CallbackInfoReturnable<Boolean> cir) {
        ServerLevel level = (ServerLevel) (Object) this;
        if (!CommonWorldGuardEvents.canSpawnEntity(entity, level)) {
            cir.setReturnValue(false);
        }
    }
}
