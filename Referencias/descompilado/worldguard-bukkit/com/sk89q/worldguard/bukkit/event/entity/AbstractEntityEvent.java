/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.google.common.base.Predicate
 *  javax.annotation.Nullable
 *  org.bukkit.Location
 *  org.bukkit.World
 *  org.bukkit.entity.Entity
 *  org.bukkit.event.Event
 */
package com.sk89q.worldguard.bukkit.event.entity;

import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.sk89q.worldguard.bukkit.cause.Cause;
import com.sk89q.worldguard.bukkit.event.DelegateEvent;
import javax.annotation.Nullable;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;

abstract class AbstractEntityEvent
extends DelegateEvent {
    private final Location target;
    @Nullable
    private final Entity entity;

    protected AbstractEntityEvent(@Nullable Event originalEvent, Cause cause, Entity entity) {
        super(originalEvent, cause);
        Preconditions.checkNotNull((Object)entity);
        this.target = entity.getLocation();
        this.entity = entity;
    }

    protected AbstractEntityEvent(@Nullable Event originalEvent, Cause cause, Location target) {
        super(originalEvent, cause);
        Preconditions.checkNotNull((Object)target);
        this.target = target;
        this.entity = null;
    }

    public World getWorld() {
        return this.target.getWorld();
    }

    public Location getTarget() {
        return this.target;
    }

    @Nullable
    public Entity getEntity() {
        return this.entity;
    }

    public boolean filter(Predicate<Location> predicate, boolean cancelEventOnFalse) {
        if (!this.isCancelled() && !predicate.apply((Object)this.getTarget())) {
            this.setCancelled(true);
        }
        return this.isCancelled();
    }
}

