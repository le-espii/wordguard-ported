package com.sk89q.worldguard.mod.mixin;

import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.mod.WorldGuardModCommon;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.FireBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Aplica Flags.FIRE_SPREAD (auditoría, hallazgo H1): antes de esto no había ningún gancho
 * de fuego más allá de Flags.LIGHTER al encender con pedernal y acero. checkBurnOut(...) es el
 * método privado que FireBlock#tick(...) llama por cada bloque vecino para decidir si se quema
 * (destruye) o se incendia; cancelarlo aquí evita que el fuego se propague o queme bloques
 * dentro de una región con fire-spread: deny, sin tocar el resto de la lógica de fuego
 * (decaimiento propio, apagado por lluvia, etc.), que sigue funcionando con normalidad.
 */
@Mixin(FireBlock.class)
public abstract class FireBlockMixin {

    @Inject(method = "checkBurnOut", at = @At("HEAD"), cancellable = true)
    private void worldguard$onCheckBurnOut(Level level, BlockPos pos, int odds, RandomSource random, int age, CallbackInfo ci) {
        if (WorldGuardModCommon.getAdapter() == null) return;
        if (level.isClientSide()) return;

        // Interruptor duro de config.yml (fire.disable-all-fire-spread), independiente del
        // flag de región: antes de C2, config.yml no tenía ningún efecto.
        com.sk89q.worldguard.config.WorldConfiguration wcfg =
            WorldGuard.getInstance().getPlatform().getGlobalStateManager().get(
                WorldGuardModCommon.getAdapter().adaptWorld(level));
        if (wcfg.disableFireSpread) {
            ci.cancel();
            return;
        }

        com.sk89q.worldedit.world.World world = WorldGuardModCommon.getAdapter().adaptWorld(level);
        com.sk89q.worldedit.util.Location weLoc = new com.sk89q.worldedit.util.Location(
            world,
            com.sk89q.worldedit.math.BlockVector3.at(pos.getX(), pos.getY(), pos.getZ()).toVector3()
        );

        RegionQuery query = WorldGuard.getInstance().getPlatform().getRegionContainer().createQuery();
        if (query.queryState(weLoc, null, Flags.FIRE_SPREAD) == StateFlag.State.DENY) {
            ci.cancel();
        }
    }
}
