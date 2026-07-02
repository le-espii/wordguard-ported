/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  org.bukkit.entity.Player
 *  org.bukkit.event.Event
 *  org.bukkit.event.HandlerList
 */
package com.sk89q.worldguard.bukkit.event.player;

import com.google.common.base.Preconditions;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ProcessPlayerEvent
extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final Player player;

    public ProcessPlayerEvent(Player player) {
        Preconditions.checkNotNull((Object)player);
        this.player = player;
    }

    public Player getPlayer() {
        return this.player;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}

