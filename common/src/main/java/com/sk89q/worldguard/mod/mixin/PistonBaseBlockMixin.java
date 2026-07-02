package com.sk89q.worldguard.mod.mixin;

import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import com.sk89q.worldguard.mod.WorldGuardModCommon;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.piston.PistonBaseBlock;
import net.minecraft.world.level.block.piston.PistonStructureResolver;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(PistonBaseBlock.class)
public abstract class PistonBaseBlockMixin {

    @Inject(method = "moveBlocks", at = @At("HEAD"), cancellable = true)
    private void onMoveBlocks(Level level, BlockPos pos, Direction direction, boolean extend, CallbackInfoReturnable<Boolean> cir) {
        if (WorldGuardModCommon.getAdapter() == null) return;

        // Resolver la estructura de bloques que el pistón intenta mover
        PistonStructureResolver resolver = new PistonStructureResolver(level, pos, direction, extend);
        if (!resolver.resolve()) {
            return; // Si el movimiento no es válido según las reglas del pistón, no hacemos nada
        }

        com.sk89q.worldedit.world.World world = WorldGuardModCommon.getAdapter().adaptWorld(level);
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionQuery query = container.createQuery();

        // 1. Verificar los bloques a empujar (toPush) y sus destinos futuros (toPush + direction)
        List<BlockPos> toPush = resolver.getToPush();
        for (BlockPos pushPos : toPush) {
            BlockPos targetPos = pushPos.relative(direction);
            
            com.sk89q.worldedit.util.Location wePushLoc = new com.sk89q.worldedit.util.Location(
                world, 
                com.sk89q.worldedit.math.BlockVector3.at(pushPos.getX(), pushPos.getY(), pushPos.getZ()).toVector3()
            );
            com.sk89q.worldedit.util.Location weTargetLoc = new com.sk89q.worldedit.util.Location(
                world, 
                com.sk89q.worldedit.math.BlockVector3.at(targetPos.getX(), targetPos.getY(), targetPos.getZ()).toVector3()
            );

            // Si el flag de pistones está denegado en el origen o el destino, se cancela
            boolean allowedPush = query.queryState(wePushLoc, null, Flags.PISTONS) != com.sk89q.worldguard.protection.flags.StateFlag.State.DENY;
            boolean allowedTarget = query.queryState(weTargetLoc, null, Flags.PISTONS) != com.sk89q.worldguard.protection.flags.StateFlag.State.DENY;

            if (!allowedPush || !allowedTarget) {
                cir.setReturnValue(false);
                return;
            }
        }

        // 2. Verificar los bloques a destruir por el pistón (toDestroy)
        List<BlockPos> toDestroy = resolver.getToDestroy();
        for (BlockPos destroyPos : toDestroy) {
            com.sk89q.worldedit.util.Location weDestroyLoc = new com.sk89q.worldedit.util.Location(
                world, 
                com.sk89q.worldedit.math.BlockVector3.at(destroyPos.getX(), destroyPos.getY(), destroyPos.getZ()).toVector3()
            );

            boolean allowedDestroy = query.queryState(weDestroyLoc, null, Flags.PISTONS) != com.sk89q.worldguard.protection.flags.StateFlag.State.DENY;
            if (!allowedDestroy) {
                cir.setReturnValue(false);
                return;
            }
        }
    }
}
