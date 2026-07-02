/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.bukkit;

import com.sk89q.worldedit.bukkit.adapter.BukkitImplAdapter;
import com.sk89q.worldedit.extension.platform.Watchdog;

class BukkitWatchdog
implements Watchdog {
    private final BukkitImplAdapter adapter;

    BukkitWatchdog(BukkitImplAdapter adapter) {
        this.adapter = adapter;
    }

    @Override
    public void tick() {
        this.adapter.tickWatchdog();
    }
}

