package com.sk89q.worldguard.mod.mixin;

import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import com.sk89q.worldguard.mod.CommonWorldGuardEvents;
import com.sk89q.worldguard.mod.WorldGuardModCommon;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayerGameMode.class)
public abstract class ServerPlayerGameModeMixin {

    @Shadow @Final protected ServerPlayer player;
    @Shadow protected net.minecraft.server.level.ServerLevel level;

    @Inject(method = "destroyBlock", at = @At("HEAD"), cancellable = true)
    private void onDestroyBlock(BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        if (WorldGuardModCommon.getAdapter() == null) return;

        com.sk89q.worldedit.world.World world = WorldGuardModCommon.getAdapter().adaptWorld(this.level);
        com.sk89q.worldguard.LocalPlayer localPlayer = WorldGuardModCommon.getAdapter().adaptPlayer(this.player);

        // La blacklist es independiente de las regiones (el original no la exime con el
        // bypass de región), así que se comprueba antes de mirar el bypass.
        if (!CommonWorldGuardEvents.checkBreakBlacklist(this.player, this.level, pos)) {
            cir.setReturnValue(false);
            return;
        }

        // Protección de cofres por cartel [Lock] (interruptor nicho pendiente de la auditoría):
        // es un sistema independiente de las regiones en el original (ChestProtectionListener
        // no comprueba el bypass de región), así que se evalúa igual que la blacklist, antes
        // del bypass.
        com.sk89q.worldedit.util.Location weBreakLoc = new com.sk89q.worldedit.util.Location(
            world,
            com.sk89q.worldedit.math.BlockVector3.at(pos.getX(), pos.getY(), pos.getZ()).toVector3()
        );
        com.sk89q.worldguard.config.WorldConfiguration breakCfg = WorldGuard.getInstance().getPlatform()
            .getGlobalStateManager().get(world);
        if (breakCfg instanceof com.sk89q.worldguard.mod.ModWorldConfiguration modBreakCfg
            && modBreakCfg.isChestProtected(weBreakLoc, localPlayer)) {
            this.player.sendSystemMessage(net.minecraft.network.chat.Component.literal(
                com.sk89q.worldguard.mod.WorldGuardLang.get(this.player, "chest-protection.protected")));
            cir.setReturnValue(false);
            return;
        }

        // Chequear bypass (ej. si es OP o tiene bypass de región)
        boolean hasBypass = WorldGuard.getInstance().getPlatform().getSessionManager().hasBypass(localPlayer, world);
        if (hasBypass) return;

        // Comprobar la protección de la región en la posición del bloque
        com.sk89q.worldedit.util.Location weLoc = new com.sk89q.worldedit.util.Location(
            world,
            com.sk89q.worldedit.math.BlockVector3.at(pos.getX(), pos.getY(), pos.getZ()).toVector3()
        );

        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionQuery query = container.createQuery();

        if (!query.testState(weLoc, localPlayer, Flags.BUILD, Flags.BLOCK_BREAK)) {
            CommonWorldGuardEvents.tellDenyMessage(this.player, weLoc, localPlayer, "break");
            cir.setReturnValue(false);
        }
    }

    // protection.item-durability (config.yml, C2): si está en false ("durabilidad infinita"),
    // repone el daño de la herramienta a 0 en cada bloque roto exitosamente, igual que
    // WorldGuardBlockListener#onBlockBreak en el original.
    @Inject(method = "destroyBlock", at = @At("RETURN"))
    private void worldguard$onDestroyBlockReturn(BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        if (WorldGuardModCommon.getAdapter() == null) return;
        if (!Boolean.TRUE.equals(cir.getReturnValue())) return;

        com.sk89q.worldguard.config.WorldConfiguration wcfg = WorldGuard.getInstance().getPlatform()
            .getGlobalStateManager().get(WorldGuardModCommon.getAdapter().adaptWorld(this.level));
        if (!wcfg.itemDurability) {
            this.player.getMainHandItem().setDamageValue(0);
        }
    }

    @Inject(method = "useItemOn", at = @At("HEAD"), cancellable = true)
    private void onUseItemOn(ServerPlayer player, Level level, ItemStack stack, InteractionHand hand, BlockHitResult hitResult, CallbackInfoReturnable<InteractionResult> cir) {
        if (WorldGuardModCommon.getAdapter() == null) return;

        com.sk89q.worldedit.world.World world = WorldGuardModCommon.getAdapter().adaptWorld(level);
        com.sk89q.worldguard.LocalPlayer localPlayer = WorldGuardModCommon.getAdapter().adaptPlayer(player);

        BlockPos pos = hitResult.getBlockPos();

        // También podemos necesitar chequear la posición adyacente (donde se coloca el bloque)
        BlockPos placePos = pos.relative(hitResult.getDirection());

        // La blacklist es independiente de las regiones (el original no la exime con el
        // bypass de región), así que se comprueba antes de mirar el bypass.
        if (!CommonWorldGuardEvents.checkInteractBlacklist(player, level, pos)) {
            cir.setReturnValue(InteractionResult.FAIL);
            return;
        }
        if (stack.getItem() instanceof net.minecraft.world.item.BlockItem earlyBlockItem
            && !CommonWorldGuardEvents.checkPlaceBlacklist(player, level, placePos, earlyBlockItem.getBlock())) {
            cir.setReturnValue(InteractionResult.FAIL);
            return;
        }

        // Protección de cofres por cartel [Lock] (interruptor nicho pendiente de la auditoría):
        // sistema independiente de las regiones en el original (ChestProtectionListener no
        // comprueba el bypass de región), así que se evalúa antes del bypass, igual que la
        // blacklist. isChestProtected() ya filtra internamente por tipo de bloque (isChest), así
        // que se puede llamar sin comprobar antes si pos es un cofre/horno/etc.
        com.sk89q.worldedit.util.Location weInteractLoc = new com.sk89q.worldedit.util.Location(
            world,
            com.sk89q.worldedit.math.BlockVector3.at(pos.getX(), pos.getY(), pos.getZ()).toVector3()
        );
        com.sk89q.worldguard.config.WorldConfiguration interactCfg = WorldGuard.getInstance().getPlatform()
            .getGlobalStateManager().get(world);
        if (interactCfg instanceof com.sk89q.worldguard.mod.ModWorldConfiguration modInteractCfg
            && modInteractCfg.isChestProtected(weInteractLoc, localPlayer)) {
            player.sendSystemMessage(net.minecraft.network.chat.Component.literal(
                com.sk89q.worldguard.mod.WorldGuardLang.get(player, "chest-protection.protected")));
            cir.setReturnValue(InteractionResult.FAIL);
            return;
        }

        // Chequear bypass
        boolean hasBypass = WorldGuard.getInstance().getPlatform().getSessionManager().hasBypass(localPlayer, world);
        if (hasBypass) return;

        com.sk89q.worldedit.util.Location weLoc = new com.sk89q.worldedit.util.Location(
            world, 
            com.sk89q.worldedit.math.BlockVector3.at(pos.getX(), pos.getY(), pos.getZ()).toVector3()
        );
        com.sk89q.worldedit.util.Location wePlaceLoc = new com.sk89q.worldedit.util.Location(
            world, 
            com.sk89q.worldedit.math.BlockVector3.at(placePos.getX(), placePos.getY(), placePos.getZ()).toVector3()
        );

        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionQuery query = container.createQuery();

        // 1. Determinar el/los flag(s) de interacción adecuados según el tipo de bloque.
        // El original combina INTERACT con un flag específico para camas, anclas de respawn,
        // TNT y bloques de tipo "USE" (auditoría, hallazgo M3) — no basta con el flag
        // específico solo.
        net.minecraft.world.level.block.state.BlockState state = level.getBlockState(pos);
        net.minecraft.world.level.block.Block block = state.getBlock();
        com.sk89q.worldguard.protection.flags.StateFlag[] interactFlags = {Flags.INTERACT};
        if (block instanceof net.minecraft.world.level.block.ChestBlock
            || block instanceof net.minecraft.world.level.block.EnderChestBlock
            || block instanceof net.minecraft.world.level.block.AbstractFurnaceBlock
            || block instanceof net.minecraft.world.level.block.DispenserBlock
            || block instanceof net.minecraft.world.level.block.HopperBlock
            || block instanceof net.minecraft.world.level.block.BarrelBlock
            || block instanceof net.minecraft.world.level.block.ShulkerBoxBlock
            || block instanceof net.minecraft.world.level.block.BrewingStandBlock
            || block instanceof net.minecraft.world.level.block.CrafterBlock) {
            interactFlags = new com.sk89q.worldguard.protection.flags.StateFlag[]{Flags.CHEST_ACCESS};
        } else if (block instanceof net.minecraft.world.level.block.AnvilBlock) {
            interactFlags = new com.sk89q.worldguard.protection.flags.StateFlag[]{Flags.USE_ANVIL};
        } else if (block instanceof net.minecraft.world.level.block.BedBlock) {
            interactFlags = new com.sk89q.worldguard.protection.flags.StateFlag[]{Flags.INTERACT, Flags.SLEEP};
        } else if (block instanceof net.minecraft.world.level.block.RespawnAnchorBlock) {
            interactFlags = new com.sk89q.worldguard.protection.flags.StateFlag[]{Flags.INTERACT, Flags.RESPAWN_ANCHORS};
        } else if (block instanceof net.minecraft.world.level.block.TntBlock) {
            interactFlags = new com.sk89q.worldguard.protection.flags.StateFlag[]{Flags.INTERACT, Flags.TNT};
        } else if (block instanceof net.minecraft.world.level.block.DoorBlock
            || block instanceof net.minecraft.world.level.block.TrapDoorBlock
            || block instanceof net.minecraft.world.level.block.FenceGateBlock
            || block instanceof net.minecraft.world.level.block.LeverBlock
            || block instanceof net.minecraft.world.level.block.ButtonBlock
            || block instanceof net.minecraft.world.level.block.RepeaterBlock
            || block instanceof net.minecraft.world.level.block.ComparatorBlock
            || block instanceof net.minecraft.world.level.block.LecternBlock) {
            interactFlags = new com.sk89q.worldguard.protection.flags.StateFlag[]{Flags.INTERACT, Flags.USE};
        }

        // Chequear interact (click derecho al bloque)
        boolean canInteract = query.testState(weLoc, localPlayer, interactFlags);
        if (!canInteract) {
            CommonWorldGuardEvents.tellDenyMessage(player, weLoc, localPlayer, "interact");
            cir.setReturnValue(InteractionResult.FAIL);
            return;
        }

        // Lighter check: combina el interruptor duro ignition.block-lighter (con permiso de
        // override, igual que WorldGuardBlockListener#onBlockIgnite) con el flag de región.
        boolean isLighter = stack.getItem() == net.minecraft.world.item.Items.FLINT_AND_STEEL
            || stack.getItem() == net.minecraft.world.item.Items.FIRE_CHARGE;
        if (isLighter) {
            com.sk89q.worldguard.config.WorldConfiguration wcfg =
                WorldGuard.getInstance().getPlatform().getGlobalStateManager().get(world);
            boolean blockedByToggle = wcfg.blockLighter && !localPlayer.hasPermission("worldguard.override.lighter");
            boolean allowedLighter = !blockedByToggle && query.testState(wePlaceLoc, localPlayer, Flags.LIGHTER);
            if (!allowedLighter) {
                CommonWorldGuardEvents.tellDenyMessage(player, wePlaceLoc, localPlayer, "lighter");
                cir.setReturnValue(InteractionResult.FAIL);
                return;
            }
        }

        // Vehicle place check
        String itemName = stack.getItem().getClass().getName();
        boolean isVehicleItem = itemName.contains("Minecart") || itemName.contains("Boat");
        if (isVehicleItem) {
            boolean allowedVehicle = query.testState(wePlaceLoc, localPlayer, Flags.BUILD, Flags.PLACE_VEHICLE);
            if (!allowedVehicle) {
                CommonWorldGuardEvents.tellDenyMessage(player, wePlaceLoc, localPlayer, "vehicle_place");
                cir.setReturnValue(InteractionResult.FAIL);
                return;
            }
        }

        // Colocación de entidades "colgantes" y otras entidades vía useOn (marcos de ítem,
        // cuadros, soportes de armadura, cristales de End). Sin este chequeo, destruirlas
        // exigía permiso (ver canDamage) pero colocarlas no exigía ninguno.
        boolean isEntityPlacementItem = stack.getItem() instanceof net.minecraft.world.item.HangingEntityItem
            || stack.getItem() instanceof net.minecraft.world.item.ArmorStandItem
            || stack.getItem() instanceof net.minecraft.world.item.EndCrystalItem;
        if (isEntityPlacementItem) {
            boolean canPlaceEntity = query.testState(wePlaceLoc, localPlayer, Flags.BUILD);
            if (!canPlaceEntity) {
                CommonWorldGuardEvents.tellDenyMessage(player, wePlaceLoc, localPlayer, "entity_place");
                cir.setReturnValue(InteractionResult.FAIL);
                return;
            }
        }

        // Protección de cofres por cartel [Lock] (interruptor nicho pendiente de la auditoría):
        // impide colocar un cofre/horno/dispensador/dropper en el hueco que un cartel [Lock]
        // ajeno ya reclama, igual que ChestProtectionListener#onPlaceBlock en el original (usa
        // el mismo conjunto legado de tipos "cofre" que BukkitSignChestProtection, más estrecho
        // que Flags.CHEST_ACCESS de arriba: no incluye barril/hopper/shulker/etc.).
        if (stack.getItem() instanceof net.minecraft.world.item.BlockItem placeBlockItem) {
            net.minecraft.world.level.block.Block placedBlock = placeBlockItem.getBlock();
            boolean isLegacyChestType = placedBlock instanceof net.minecraft.world.level.block.ChestBlock
                || placedBlock instanceof net.minecraft.world.level.block.DispenserBlock
                || placedBlock instanceof net.minecraft.world.level.block.FurnaceBlock;
            com.sk89q.worldguard.config.WorldConfiguration placeCfg = WorldGuard.getInstance().getPlatform()
                .getGlobalStateManager().get(world);
            if (isLegacyChestType && placeCfg instanceof com.sk89q.worldguard.mod.ModWorldConfiguration modPlaceCfg
                && modPlaceCfg.isChestProtected(wePlaceLoc, localPlayer)) {
                player.sendSystemMessage(net.minecraft.network.chat.Component.literal(
                    com.sk89q.worldguard.mod.WorldGuardLang.get(player, "chest-protection.protected-placement")));
                cir.setReturnValue(InteractionResult.FAIL);
                return;
            }
        }

        // Chequear place (colocación): limitado a ítems que realmente colocan un bloque
        // (BlockItem) o un fluido (BucketItem, p.ej. agua/lava). Antes se exigía BUILD para
        // CUALQUIER ítem no vacío usado sobre un bloque (comer, beber pociones, abonar, etc.),
        // lo que bloqueaba de más interacciones legítimas que no colocan nada.
        boolean isPlacingItem = stack.getItem() instanceof net.minecraft.world.item.BlockItem
            || stack.getItem() instanceof net.minecraft.world.item.BucketItem;
        if (isPlacingItem) {
            boolean canPlace = query.testState(wePlaceLoc, localPlayer, Flags.BUILD, Flags.BLOCK_PLACE);
            if (!canPlace) {
                CommonWorldGuardEvents.tellDenyMessage(player, wePlaceLoc, localPlayer, "build");
                cir.setReturnValue(InteractionResult.FAIL);
            }
        }
    }
}
