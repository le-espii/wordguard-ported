/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.event.Event
 *  org.bukkit.event.HandlerList
 */
package com.sk89q.worldguard.bukkit.protection.events.flags;

import com.sk89q.worldguard.protection.flags.FlagContext;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class FlagContextCreateEvent
extends Event {
    private FlagContext.FlagContextBuilder builder;
    private static final HandlerList handlers = new HandlerList();

    public FlagContextCreateEvent(FlagContext.FlagContextBuilder builder) {
        this.builder = builder;
    }

    public boolean addObject(String key, Object value) {
        return this.builder.tryAddToMap(key, value);
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}

