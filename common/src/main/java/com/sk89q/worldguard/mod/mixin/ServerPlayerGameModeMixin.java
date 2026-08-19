package com.sk89q.worldguard.mod.mixin;

import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import com.sk89q.worldguard.mod.WorldGuardModCommon;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
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

        // Chequear bypass primero (ej. si es OP o tiene bypass de región)
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
            this.player.sendSystemMessage(Component.literal("§cNo tienes permiso para romper bloques aquí."));
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "useItemOn", at = @At("HEAD"), cancellable = true)
    private void onUseItemOn(ServerPlayer player, Level level, ItemStack stack, InteractionHand hand, BlockHitResult hitResult, CallbackInfoReturnable<InteractionResult> cir) {
        if (WorldGuardModCommon.getAdapter() == null) return;

        com.sk89q.worldedit.world.World world = WorldGuardModCommon.getAdapter().adaptWorld(level);
        com.sk89q.worldguard.LocalPlayer localPlayer = WorldGuardModCommon.getAdapter().adaptPlayer(player);

        // Chequear bypass primero
        boolean hasBypass = WorldGuard.getInstance().getPlatform().getSessionManager().hasBypass(localPlayer, world);
        if (hasBypass) return;

        BlockPos pos = hitResult.getBlockPos();
        
        // También podemos necesitar chequear la posición adyacente (donde se coloca el bloque)
        BlockPos placePos = pos.relative(hitResult.getDirection());

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
        String className = block.getClass().getName();
        if (block instanceof net.minecraft.world.level.block.ChestBlock
            || block instanceof net.minecraft.world.level.block.EnderChestBlock
            || className.contains("Chest")
            || className.contains("Furnace")
            || className.contains("Dispenser")
            || className.contains("Dropper")
            || className.contains("Hopper")
            || className.contains("Barrel")
            || className.contains("ShulkerBox")
            || className.contains("BrewingStand")
            || className.contains("Crafter")) {
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
            || className.contains("Lever")
            || className.contains("Button")
            || className.contains("Repeater")
            || className.contains("Comparator")
            || className.contains("Lectern")) {
            interactFlags = new com.sk89q.worldguard.protection.flags.StateFlag[]{Flags.INTERACT, Flags.USE};
        }

        // Chequear interact (click derecho al bloque)
        boolean canInteract = query.testState(weLoc, localPlayer, interactFlags);
        if (!canInteract) {
            player.sendSystemMessage(Component.literal("§cNo tienes permiso para interactuar aquí."));
            cir.setReturnValue(InteractionResult.FAIL);
            return;
        }

        // Lighter check
        boolean isLighter = stack.getItem() == net.minecraft.world.item.Items.FLINT_AND_STEEL 
            || stack.getItem() == net.minecraft.world.item.Items.FIRE_CHARGE;
        if (isLighter) {
            boolean allowedLighter = query.testState(wePlaceLoc, localPlayer, Flags.LIGHTER);
            if (!allowedLighter) {
                player.sendSystemMessage(Component.literal("§cNo tienes permiso para usar encendedores aquí."));
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
                player.sendSystemMessage(Component.literal("§cNo tienes permiso para colocar vehículos aquí."));
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
                player.sendSystemMessage(Component.literal("§cNo tienes permiso para colocar eso aquí."));
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
                player.sendSystemMessage(Component.literal("§cNo tienes permiso para construir aquí."));
                cir.setReturnValue(InteractionResult.FAIL);
            }
        }
    }
}
