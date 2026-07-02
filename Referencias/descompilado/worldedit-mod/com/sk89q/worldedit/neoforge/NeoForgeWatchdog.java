/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.Util
 *  net.minecraft.server.dedicated.DedicatedServer
 */
package com.sk89q.worldedit.neoforge;

import com.sk89q.worldedit.extension.platform.Watchdog;
import net.minecraft.Util;
import net.minecraft.server.dedicated.DedicatedServer;

class NeoForgeWatchdog
implements Watchdog {
    private final DedicatedServer server;

    NeoForgeWatchdog(DedicatedServer server) {
        this.server = server;
    }

    @Override
    public void tick() {
        this.server.nextTickTimeNanos = Util.getNanos();
    }
}

