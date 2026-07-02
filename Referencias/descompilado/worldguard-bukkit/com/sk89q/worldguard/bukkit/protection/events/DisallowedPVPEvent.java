/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Player
 *  org.bukkit.event.Cancellable
 *  org.bukkit.event.Event
 *  org.bukkit.event.HandlerList
 */
package com.sk89q.worldguard.bukkit.protection.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class DisallowedPVPEvent
extends Event
implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled = false;
    private final Player attacker;
    private final Player defender;
    private final Event event;

    public DisallowedPVPEvent(Player attacker, Player defender, Event event) {
        this.attacker = attacker;
        this.defender = defender;
        this.event = event;
    }

    public boolean isCancelled() {
        return this.cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public Player getAttacker() {
        return this.attacker;
    }

    public Player getDefender() {
        return this.defender;
    }

    public Event getCause() {
        return this.event;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}

