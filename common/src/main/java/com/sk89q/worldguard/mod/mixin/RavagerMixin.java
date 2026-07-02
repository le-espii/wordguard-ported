package com.sk89q.worldguard.mod.mixin;

import com.sk89q.worldguard.mod.CommonWorldGuardEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.monster.Ravager;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Ravager.class)
public abstract class RavagerMixin {

    @Redirect(method = "aiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;destroyBlock(Lnet/minecraft/core/BlockPos;ZLnet/minecraft/world/entity/Entity;)Z"))
    private boolean onDestroyBlock(Level level, BlockPos pos, boolean drop, net.minecraft.world.entity.Entity entity) {
        if (!CommonWorldGuardEvents.canRavagerRavage(level, pos)) {
            return false; // Cancel destruction
        }
        return level.destroyBlock(pos, drop, entity);
    }
}
