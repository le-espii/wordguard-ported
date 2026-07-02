/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  org.bukkit.entity.Entity
 *  org.bukkit.event.Event
 *  org.bukkit.event.HandlerList
 */
package com.sk89q.worldguard.bukkit.event.entity;

import com.google.common.base.Preconditions;
import com.sk89q.worldguard.bukkit.cause.Cause;
import com.sk89q.worldguard.bukkit.event.entity.AbstractEntityEvent;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class DestroyEntityEvent
extends AbstractEntityEvent {
    private static final HandlerList handlers = new HandlerList();

    public DestroyEntityEvent(@Nullable Event originalEvent, Cause cause, Entity target) {
        super(originalEvent, cause, (Entity)Preconditions.checkNotNull((Object)target));
    }

    @Override
    @Nonnull
    public Entity getEntity() {
        return super.getEntity();
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}

