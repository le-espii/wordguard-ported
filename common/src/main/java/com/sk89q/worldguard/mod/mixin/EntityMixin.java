package com.sk89q.worldguard.mod.mixin;

import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.mod.CommonWorldGuardEvents;
import com.sk89q.worldguard.mod.WorldGuardModCommon;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityMixin {

    @Inject(method = "hurt", at = @At("HEAD"), cancellable = true)
    private void onHurt(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        Entity entity = (Entity) (Object) this;
        if (!CommonWorldGuardEvents.canDamage(entity, source, amount)) {
            cir.setReturnValue(false);
        }
    }

    // Impide que un jugador se baje de un vehículo dentro de una región donde no tendría
    // permiso de RIDE/INTERACT, igual que RegionProtectionListener#onVehicleExit en el
    // original ("Don't disembark here!"). Sin esto, el port dejaba desmontar en cualquier
    // lugar sin importar la región.
    @Inject(method = "stopRiding()V", at = @At("HEAD"), cancellable = true)
    private void worldguard$onStopRiding(CallbackInfo ci) {
        if (WorldGuardModCommon.getAdapter() == null) return;
        Entity self = (Entity) (Object) this;
        if (!(self instanceof ServerPlayer player)) return;
        if (self.level().isClientSide()) return;

        Entity vehicle = self.getVehicle();
        if (vehicle == null) return;

        LocalPlayer localPlayer = WorldGuardModCommon.getAdapter().adaptPlayer(player);
        if (WorldGuard.getInstance().getPlatform().getSessionManager().hasBypass(localPlayer, localPlayer.getWorld())) {
            return;
        }

        BlockPos pos = vehicle.blockPosition();
        com.sk89q.worldedit.world.World world = WorldGuardModCommon.getAdapter().adaptWorld(vehicle.level());
        com.sk89q.worldedit.util.Location weLoc = new com.sk89q.worldedit.util.Location(
            world,
            com.sk89q.worldedit.math.BlockVector3.at(pos.getX(), pos.getY(), pos.getZ()).toVector3()
        );

        RegionQuery query = WorldGuard.getInstance().getPlatform().getRegionContainer().createQuery();
        boolean canDismount = query.queryState(weLoc, localPlayer, Flags.RIDE) != StateFlag.State.DENY
            && query.queryState(weLoc, localPlayer, Flags.INTERACT) != StateFlag.State.DENY;
        if (!canDismount) {
            player.sendSystemMessage(Component.literal("§6No puedes bajarte aquí. §7No podrás volver a subir."));
            ci.cancel();
        }
    }
}
