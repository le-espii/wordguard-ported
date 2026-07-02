/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.command.CommandMap
 *  org.bukkit.command.CommandSender
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.Listener
 *  org.bukkit.event.player.PlayerCommandPreprocessEvent
 */
package com.sk89q.bukkit.util;

import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class FallbackRegistrationListener
implements Listener {
    private final CommandMap commandRegistration;

    public FallbackRegistrationListener(CommandMap commandRegistration) {
        this.commandRegistration = commandRegistration;
    }

    @EventHandler(ignoreCancelled=true)
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        if (this.commandRegistration.dispatch((CommandSender)event.getPlayer(), event.getMessage().substring(1))) {
            event.setCancelled(true);
        }
    }
}

