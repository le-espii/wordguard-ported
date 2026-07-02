/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.server.MinecraftServer
 *  net.minecraft.server.level.ServerPlayer
 *  net.neoforged.bus.api.SubscribeEvent
 *  net.neoforged.neoforge.event.tick.ServerTickEvent$Post
 *  net.neoforged.neoforge.server.ServerLifecycleHooks
 */
package com.sk89q.worldedit.neoforge;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArraySet;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

public class ThreadSafeCache {
    private static final long REFRESH_DELAY = 30000L;
    private static final ThreadSafeCache INSTANCE = new ThreadSafeCache();
    private Set<UUID> onlineIds = Collections.emptySet();
    private long lastRefresh = 0L;

    public Set<UUID> getOnlineIds() {
        return this.onlineIds;
    }

    @SubscribeEvent
    public void onEndTick(ServerTickEvent.Post event) {
        long now = System.currentTimeMillis();
        if (now - this.lastRefresh > 30000L) {
            HashSet<UUID> onlineIds = new HashSet<UUID>();
            MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
            if (server == null) {
                return;
            }
            for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                if (player == null) continue;
                onlineIds.add(player.getUUID());
            }
            this.onlineIds = new CopyOnWriteArraySet<UUID>(onlineIds);
            this.lastRefresh = now;
        }
    }

    public static ThreadSafeCache getInstance() {
        return INSTANCE;
    }
}

