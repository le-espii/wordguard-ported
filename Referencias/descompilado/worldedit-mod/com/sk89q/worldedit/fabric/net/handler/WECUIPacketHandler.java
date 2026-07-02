/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry
 *  net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
 *  net.minecraft.class_2960
 *  net.minecraft.class_8710
 *  net.minecraft.class_8710$class_9154
 *  net.minecraft.class_9139
 */
package com.sk89q.worldedit.fabric.net.handler;

import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.fabric.FabricAdapter;
import com.sk89q.worldedit.fabric.FabricPlayer;
import com.sk89q.worldedit.fabric.FabricWorldEdit;
import java.nio.charset.StandardCharsets;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.class_2960;
import net.minecraft.class_8710;
import net.minecraft.class_9139;

public final class WECUIPacketHandler {
    public static final class_2960 CUI_IDENTIFIER = class_2960.method_60655((String)"worldedit", (String)"cui");

    private WECUIPacketHandler() {
    }

    public static void init() {
        class_9139 codec = class_8710.method_56484((packet, buffer) -> buffer.writeCharSequence((CharSequence)packet.text(), StandardCharsets.UTF_8), buffer -> new CuiPacket(buffer.readCharSequence(buffer.readableBytes(), StandardCharsets.UTF_8).toString()));
        PayloadTypeRegistry.playC2S().register(CuiPacket.TYPE, codec);
        PayloadTypeRegistry.playS2C().register(CuiPacket.TYPE, codec);
        ServerPlayNetworking.registerGlobalReceiver(CuiPacket.TYPE, (payload, context) -> {
            LocalSession session = FabricWorldEdit.inst.getSession(context.player());
            FabricPlayer actor = FabricAdapter.adaptPlayer(context.player());
            session.handleCUIInitializationMessage(payload.text(), actor);
        });
    }

    public record CuiPacket(String text) implements class_8710
    {
        public static final class_8710.class_9154<CuiPacket> TYPE = new class_8710.class_9154(CUI_IDENTIFIER);

        public class_8710.class_9154<? extends class_8710> method_56479() {
            return TYPE;
        }
    }
}

