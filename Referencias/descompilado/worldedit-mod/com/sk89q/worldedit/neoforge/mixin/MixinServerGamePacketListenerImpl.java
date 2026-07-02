/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.protocol.game.ServerboundPlayerActionPacket
 *  net.minecraft.network.protocol.game.ServerboundSwingPacket
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.server.network.ServerGamePacketListenerImpl
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package com.sk89q.worldedit.neoforge.mixin;

import com.sk89q.worldedit.neoforge.NeoForgeWorldEdit;
import com.sk89q.worldedit.neoforge.mixin.AccessorServerPlayerGameMode;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.network.protocol.game.ServerboundSwingPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={ServerGamePacketListenerImpl.class})
public class MixinServerGamePacketListenerImpl {
    @Shadow
    public ServerPlayer player;
    @Unique
    private int ignoreSwingPackets;

    @Inject(method={"handleAnimate"}, at={@At(value="HEAD")})
    private void onAnimate(ServerboundSwingPacket packet, CallbackInfo ci) {
        if (!((AccessorServerPlayerGameMode)this.player.gameMode).isDestroyingBlock()) {
            if (this.ignoreSwingPackets > 0) {
                --this.ignoreSwingPackets;
            } else if (NeoForgeWorldEdit.inst != null) {
                NeoForgeWorldEdit.inst.onLeftClickAir(this.player, packet.getHand());
            }
        }
    }

    @Inject(method={"handlePlayerAction"}, at={@At(value="HEAD")})
    private void onAction(ServerboundPlayerActionPacket packet, CallbackInfo ci) {
        switch (packet.getAction()) {
            case DROP_ITEM: 
            case DROP_ALL_ITEMS: 
            case START_DESTROY_BLOCK: {
                ++this.ignoreSwingPackets;
                break;
            }
        }
    }
}

