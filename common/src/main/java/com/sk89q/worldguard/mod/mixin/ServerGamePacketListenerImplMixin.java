package com.sk89q.worldguard.mod.mixin;

import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.config.WorldConfiguration;
import com.sk89q.worldguard.mod.WorldGuardModCommon;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ServerboundSignUpdatePacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.StandingSignBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * chest-protection.enable / disable-off-check (config.yml, interruptor nicho pendiente de la
 * auditoría): valida y normaliza los carteles [Lock] al editarse, igual que
 * ChestProtectionListener#onSignChange en el original (SignChangeEvent no existe en Minecraft
 * vanilla; el equivalente más cercano es interceptar el paquete que el cliente manda al cerrar
 * el editor de carteles). El resto de la lógica de protección (adyacencia, doble cofre, lectura
 * del texto ya guardado) vive en worldguard-core (SignChestProtection) y en
 * ModSignChestProtection.
 */
@Mixin(ServerGamePacketListenerImpl.class)
public abstract class ServerGamePacketListenerImplMixin {

    @Shadow public ServerPlayer player;

    @Inject(method = "handleSignUpdate", at = @At("HEAD"), cancellable = true)
    private void worldguard$onHandleSignUpdate(ServerboundSignUpdatePacket packet, CallbackInfo ci) {
        if (WorldGuardModCommon.getAdapter() == null) return;

        String[] lines = packet.getLines();
        if (!"[Lock]".equalsIgnoreCase(lines[0])) return;

        Level level = this.player.level();
        BlockPos pos = packet.getPos();
        com.sk89q.worldedit.world.World world = WorldGuardModCommon.getAdapter().adaptWorld(level);
        WorldConfiguration baseCfg = WorldGuard.getInstance().getPlatform().getGlobalStateManager().get(world);
        if (!(baseCfg instanceof com.sk89q.worldguard.mod.ModWorldConfiguration wcfg)) return;

        if (!wcfg.signChestProtection) {
            if (!wcfg.disableSignChestProtectionCheck) {
                this.player.sendSystemMessage(Component.literal(
                    com.sk89q.worldguard.mod.WorldGuardLang.get(this.player, "chest-protection.disabled")));
                level.destroyBlock(pos, true);
                ci.cancel();
            }
            return;
        }

        LocalPlayer localPlayer = WorldGuardModCommon.getAdapter().adaptPlayer(this.player);
        com.sk89q.worldedit.util.Location weLoc = new com.sk89q.worldedit.util.Location(
            world,
            com.sk89q.worldedit.math.BlockVector3.at(pos.getX(), pos.getY(), pos.getZ()).toVector3()
        );

        if (wcfg.isChestProtectedPlacement(weLoc, localPlayer)) {
            this.player.sendSystemMessage(Component.literal(
                com.sk89q.worldguard.mod.WorldGuardLang.get(this.player, "chest-protection.not-owner")));
            level.destroyBlock(pos, true);
            ci.cancel();
            return;
        }

        BlockState signState = level.getBlockState(pos);
        if (!(signState.getBlock() instanceof StandingSignBlock)) {
            this.player.sendSystemMessage(Component.literal(
                com.sk89q.worldguard.mod.WorldGuardLang.get(this.player, "chest-protection.wall-sign")));
            level.destroyBlock(pos, true);
            ci.cancel();
            return;
        }

        if (!localPlayer.getName().equalsIgnoreCase(lines[1])) {
            this.player.sendSystemMessage(Component.literal(
                com.sk89q.worldguard.mod.WorldGuardLang.get(this.player, "chest-protection.not-your-name")));
            level.destroyBlock(pos, true);
            ci.cancel();
            return;
        }

        net.minecraft.world.level.block.Block below = level.getBlockState(pos.below()).getBlock();
        boolean unsafeBelow = below == Blocks.TNT
            || below == Blocks.SAND
            || below == Blocks.GRAVEL
            || below instanceof StandingSignBlock;
        if (unsafeBelow) {
            this.player.sendSystemMessage(Component.literal(
                com.sk89q.worldguard.mod.WorldGuardLang.get(this.player, "chest-protection.unsafe-block")));
            level.destroyBlock(pos, true);
            ci.cancel();
            return;
        }

        lines[0] = "[Lock]";
        this.player.sendSystemMessage(Component.literal(
            com.sk89q.worldguard.mod.WorldGuardLang.get(this.player, "chest-protection.locked")));
    }
}
