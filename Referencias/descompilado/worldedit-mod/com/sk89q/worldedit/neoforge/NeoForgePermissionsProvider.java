/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.level.GameType
 *  net.neoforged.neoforge.server.ServerLifecycleHooks
 */
package com.sk89q.worldedit.neoforge;

import com.sk89q.worldedit.neoforge.NeoForgeConfiguration;
import com.sk89q.worldedit.neoforge.NeoForgePlatform;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameType;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

public interface NeoForgePermissionsProvider {
    public boolean hasPermission(ServerPlayer var1, String var2);

    public void registerPermission(String var1);

    public static class VanillaPermissionsProvider
    implements NeoForgePermissionsProvider {
        private final NeoForgePlatform platform;

        public VanillaPermissionsProvider(NeoForgePlatform platform) {
            this.platform = platform;
        }

        @Override
        public boolean hasPermission(ServerPlayer player, String permission) {
            NeoForgeConfiguration configuration = this.platform.getConfiguration();
            return configuration.cheatMode || ServerLifecycleHooks.getCurrentServer().getPlayerList().isOp(player.getGameProfile()) || configuration.creativeEnable && player.gameMode.getGameModeForPlayer() == GameType.CREATIVE;
        }

        @Override
        public void registerPermission(String permission) {
        }
    }
}

