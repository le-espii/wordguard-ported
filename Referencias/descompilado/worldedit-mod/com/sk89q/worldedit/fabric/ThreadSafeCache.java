/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents$EndTick
 *  net.minecraft.class_3222
 *  net.minecraft.server.MinecraftServer
 */
package com.sk89q.worldedit.fabric;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArraySet;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.class_3222;
import net.minecraft.server.MinecraftServer;

public class ThreadSafeCache
implements ServerTickEvents.EndTick {
    private static final long REFRESH_DELAY = 30000L;
    private static final ThreadSafeCache INSTANCE = new ThreadSafeCache();
    private Set<UUID> onlineIds = new CopyOnWriteArraySet<UUID>();
    private long lastRefresh = 0L;

    public Set<UUID> getOnlineIds() {
        return this.onlineIds;
    }

    public void onEndTick(MinecraftServer server) {
        long now = System.currentTimeMillis();
        if (now - this.lastRefresh > 30000L) {
            HashSet<UUID> onlineIds = new HashSet<UUID>();
            if (server == null) {
                return;
            }
            for (class_3222 player : server.method_3760().method_14571()) {
                if (player == null) continue;
                onlineIds.add(player.method_5667());
            }
            this.onlineIds = new CopyOnWriteArraySet<UUID>(onlineIds);
            this.lastRefresh = now;
        }
    }

    public static ThreadSafeCache getInstance() {
        return INSTANCE;
    }
}

