/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.server.PluginDisableEvent
 *  org.bukkit.event.server.PluginEnableEvent
 */
package com.sk89q.worldguard.bukkit.listener;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.bukkit.listener.AbstractListener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;

public class WorldGuardServerListener
extends AbstractListener {
    public WorldGuardServerListener(WorldGuardPlugin plugin) {
        super(plugin);
    }

    @EventHandler
    public void onPluginEnable(PluginEnableEvent event) {
        if (event.getPlugin().getDescription().getName().equalsIgnoreCase("CommandBook")) {
            WorldGuardServerListener.getConfig().updateCommandBookGodMode();
        }
    }

    @EventHandler
    public void onPluginDisable(PluginDisableEvent event) {
        if (event.getPlugin().getDescription().getName().equalsIgnoreCase("CommandBook")) {
            WorldGuardServerListener.getConfig().updateCommandBookGodMode();
        }
    }
}

