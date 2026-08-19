package com.sk89q.worldguard.mod.mixin;

import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.config.WorldConfiguration;
import com.sk89q.worldguard.mod.WorldGuardModCommon;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.HopperBlock;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * event-handling.ignore-hopper-item-move-events / break-hoppers-on-denied-move (config.yml,
 * interruptor nicho pendiente de la auditoría): el original engancha InventoryMoveItemEvent y
 * resuelve el InventoryHolder exacto en origen y destino para comprobar el flag de acceso a
 * contenedores con causa "desconocida" (no hay jugador, la acción la dispara el propio hopper).
 * Aquí se simplifica a comprobar CHEST_ACCESS con jugador nulo en la posición del hopper y en
 * los bloques de los que succiona/a los que empuja, en vez de resolver el holder de inventario
 * (chest doble, minecart-hopper, dropper, etc.) como hace WorldGuardBlockListener en Bukkit.
 */
@Mixin(HopperBlockEntity.class)
public abstract class HopperBlockEntityMixin {

    @Inject(method = "pushItemsTick", at = @At("HEAD"), cancellable = true)
    private static void worldguard$onPushItemsTick(Level level, BlockPos pos, BlockState state, HopperBlockEntity hopper, CallbackInfo ci) {
        if (WorldGuardModCommon.getAdapter() == null || level.isClientSide()) return;

        com.sk89q.worldedit.world.World world = WorldGuardModCommon.getAdapter().adaptWorld(level);
        WorldConfiguration wcfg = WorldGuard.getInstance().getPlatform().getGlobalStateManager().get(world);
        if (wcfg.ignoreHopperMoveEvents) return;

        RegionQuery query = WorldGuard.getInstance().getPlatform().getRegionContainer().createQuery();
        Direction facing = state.getValue(HopperBlock.FACING);
        BlockPos sourcePos = pos.above();
        BlockPos targetPos = pos.relative(facing);

        boolean allowed = worldguard$canAccess(query, world, pos)
            && worldguard$canAccess(query, world, sourcePos)
            && worldguard$canAccess(query, world, targetPos);

        if (!allowed) {
            ci.cancel();
            if (wcfg.breakDeniedHoppers) {
                level.destroyBlock(pos, true);
            }
        }
    }

    private static boolean worldguard$canAccess(RegionQuery query, com.sk89q.worldedit.world.World world, BlockPos pos) {
        com.sk89q.worldedit.util.Location loc = new com.sk89q.worldedit.util.Location(
            world,
            com.sk89q.worldedit.math.BlockVector3.at(pos.getX(), pos.getY(), pos.getZ()).toVector3()
        );
        return query.queryState(loc, (com.sk89q.worldguard.LocalPlayer) null, Flags.CHEST_ACCESS) != StateFlag.State.DENY;
    }
}
