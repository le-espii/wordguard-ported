/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  javax.annotation.Nullable
 *  org.bukkit.Location
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.EntityType
 *  org.bukkit.event.Event
 *  org.bukkit.event.HandlerList
 */
package com.sk89q.worldguard.bukkit.event.entity;

import com.google.common.base.Preconditions;
import com.sk89q.worldguard.bukkit.cause.Cause;
import com.sk89q.worldguard.bukkit.event.entity.AbstractEntityEvent;
import javax.annotation.Nullable;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class SpawnEntityEvent
extends AbstractEntityEvent {
    private static final HandlerList handlers = new HandlerList();
    private final EntityType effectiveType;

    public SpawnEntityEvent(@Nullable Event originalEvent, Cause cause, Entity target) {
        super(originalEvent, cause, (Entity)Preconditions.checkNotNull((Object)target));
        this.effectiveType = target.getType();
    }

    public SpawnEntityEvent(@Nullable Event originalEvent, Cause cause, Location location, EntityType type) {
        super(originalEvent, cause, location);
        Preconditions.checkNotNull((Object)type);
        this.effectiveType = type;
    }

    public EntityType getEffectiveType() {
        return this.effectiveType;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}

