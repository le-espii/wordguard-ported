package com.sk89q.worldguard.neoforge.mixin;

import com.sk89q.worldguard.mod.CommonWorldGuardEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.FireBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Aplica Flags.FIRE_SPREAD (auditoría, hallazgo H1). Variante NeoForge de
 * checkBurnOut(...): NeoForge parchea este método en su propio jar de juego añadiéndole un
 * Direction final (API de inflamabilidad por cara) que vanilla/Fabric no tiene. Ver
 * {@link CommonWorldGuardEvents#checkFireBurnOut} para el porqué de que este mixin viva
 * aquí y no en el módulo common: un solo FireBlockMixin compartido no puede tener dos
 * firmas de handler para el mismo método (Mixin valida el descriptor exacto y aborta el
 * arranque si no coincide, incluso con require = 0), así que cada plataforma tiene su
 * propio mixin fino con la firma exacta de su propio jar de juego.
 */
@Mixin(FireBlock.class)
public abstract class FireBlockMixin {

    @Inject(method = "checkBurnOut", at = @At("HEAD"), cancellable = true)
    private void worldguard$onCheckBurnOut(Level level, BlockPos pos, int odds, RandomSource random, int age, Direction face, CallbackInfo ci) {
        if (!CommonWorldGuardEvents.checkFireBurnOut(level, pos)) {
            ci.cancel();
        }
    }
}
