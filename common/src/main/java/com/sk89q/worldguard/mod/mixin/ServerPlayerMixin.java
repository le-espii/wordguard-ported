package com.sk89q.worldguard.mod.mixin;

import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.mod.WorldGuardModCommon;
import com.sk89q.worldguard.session.MoveType;
import com.sk89q.worldguard.session.Session;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMixin {

    @Unique private double worldguard$lastX = Double.NaN;
    @Unique private double worldguard$lastY = Double.NaN;
    @Unique private double worldguard$lastZ = Double.NaN;

    @Inject(method = "tick", at = @At("HEAD"))
    private void onTick(CallbackInfo ci) {
        if (WorldGuardModCommon.getAdapter() == null) return;

        ServerPlayer player = (ServerPlayer) (Object) this;
        if (player.level().isClientSide()) return;
        if (player.connection == null) return; // Evitar NullPointerException durante el login muy temprano

        LocalPlayer localPlayer = WorldGuardModCommon.getAdapter().adaptPlayer(player);
        Session session = WorldGuard.getInstance().getPlatform().getSessionManager().get(localPlayer);
        if (session == null) return;

        // Tick session
        session.tick(localPlayer);

        // Check if position changed
        if (Double.isNaN(worldguard$lastX)) {
            worldguard$lastX = player.getX();
            worldguard$lastY = player.getY();
            worldguard$lastZ = player.getZ();
            return;
        }

        int lastBlockX = (int) Math.floor(worldguard$lastX);
        int lastBlockY = (int) Math.floor(worldguard$lastY);
        int lastBlockZ = (int) Math.floor(worldguard$lastZ);
        int currBlockX = (int) Math.floor(player.getX());
        int currBlockY = (int) Math.floor(player.getY());
        int currBlockZ = (int) Math.floor(player.getZ());

        if (lastBlockX != currBlockX || lastBlockY != currBlockY || lastBlockZ != currBlockZ) {
            com.sk89q.worldedit.util.Location toLoc = new com.sk89q.worldedit.util.Location(
                WorldGuardModCommon.getAdapter().adaptWorld(player.level()),
                new com.sk89q.worldedit.math.Vector3(player.getX(), player.getY(), player.getZ())
            );

            MoveType moveType = MoveType.MOVE;
            if (player.isFallFlying()) {
                moveType = MoveType.GLIDE;
            } else if (player.isSwimming()) {
                moveType = MoveType.SWIM;
            } else if (player.getVehicle() != null) {
                moveType = MoveType.RIDE;
            }

            com.sk89q.worldedit.util.Location disallowedLoc = session.testMoveTo(localPlayer, toLoc, moveType);
            if (disallowedLoc != null) {
                // Teleport back to last valid position
                player.teleportTo((net.minecraft.server.level.ServerLevel) player.level(), worldguard$lastX, worldguard$lastY, worldguard$lastZ, java.util.EnumSet.noneOf(net.minecraft.world.entity.RelativeMovement.class), player.getYRot(), player.getXRot());
            } else {
                worldguard$lastX = player.getX();
                worldguard$lastY = player.getY();
                worldguard$lastZ = player.getZ();
            }
        }
    }
}
