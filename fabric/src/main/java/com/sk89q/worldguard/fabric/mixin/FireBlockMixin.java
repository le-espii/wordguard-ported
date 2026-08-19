package com.sk89q.worldguard.fabric.mixin;

import com.sk89q.worldguard.mod.CommonWorldGuardEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.FireBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Aplica Flags.FIRE_SPREAD (auditoría, hallazgo H1). Variante Fabric/vanilla de
 * checkBurnOut(...) (5 parámetros). Ver {@link CommonWorldGuardEvents#checkFireBurnOut}
 * para el porqué de que este mixin viva aquí y no en el módulo common: NeoForge parchea
 * este mismo método con un parámetro Direction extra que vanilla no tiene, así que cada
 * plataforma necesita su propio mixin fino con la firma exacta de su propio jar de juego.
 */
@Mixin(FireBlock.class)
public abstract class FireBlockMixin {

    @Inject(method = "checkBurnOut", at = @At("HEAD"), cancellable = true)
    private void worldguard$onCheckBurnOut(Level level, BlockPos pos, int odds, RandomSource random, int age, CallbackInfo ci) {
        if (!CommonWorldGuardEvents.checkFireBurnOut(level, pos)) {
            ci.cancel();
        }
    }
}
