/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload$Type
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.entity.player.Player
 *  net.neoforged.bus.api.SubscribeEvent
 *  net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent
 */
package com.sk89q.worldedit.neoforge.net.handler;

import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.neoforge.NeoForgeAdapter;
import com.sk89q.worldedit.neoforge.NeoForgePlayer;
import com.sk89q.worldedit.neoforge.NeoForgeWorldEdit;
import java.nio.charset.StandardCharsets;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

public final class WECUIPacketHandler {
    private static final String PROTOCOL_VERSION = "1";
    public static final ResourceLocation CUI_IDENTIFIER = ResourceLocation.fromNamespaceAndPath((String)"worldedit", (String)"cui");

    private WECUIPacketHandler() {
    }

    @SubscribeEvent
    public static void register(RegisterPayloadHandlersEvent event) {
        event.registrar(PROTOCOL_VERSION).optional().playBidirectional(CuiPacket.TYPE, CustomPacketPayload.codec((packet, buffer) -> buffer.writeCharSequence((CharSequence)packet.text(), StandardCharsets.UTF_8), buffer -> new CuiPacket(buffer.readCharSequence(buffer.readableBytes(), StandardCharsets.UTF_8).toString())), (payload, context) -> {
            Player patt0$temp = context.player();
            if (!(patt0$temp instanceof ServerPlayer)) {
                return;
            }
            ServerPlayer player = (ServerPlayer)patt0$temp;
            LocalSession session = NeoForgeWorldEdit.inst.getSession(player);
            NeoForgePlayer actor = NeoForgeAdapter.adaptPlayer(player);
            session.handleCUIInitializationMessage(payload.text(), actor);
        });
    }

    public record CuiPacket(String text) implements CustomPacketPayload
    {
        public static final CustomPacketPayload.Type<CuiPacket> TYPE = new CustomPacketPayload.Type(CUI_IDENTIFIER);

        public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }
}

