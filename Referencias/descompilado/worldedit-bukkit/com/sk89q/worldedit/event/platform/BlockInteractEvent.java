/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  javax.annotation.Nullable
 */
package com.sk89q.worldedit.event.platform;

import com.google.common.base.Preconditions;
import com.sk89q.worldedit.event.Cancellable;
import com.sk89q.worldedit.event.Event;
import com.sk89q.worldedit.event.platform.Interaction;
import com.sk89q.worldedit.extension.platform.Actor;
import com.sk89q.worldedit.util.Direction;
import com.sk89q.worldedit.util.Location;
import javax.annotation.Nullable;

public class BlockInteractEvent
extends Event
implements Cancellable {
    private final Actor cause;
    private final Location location;
    private final Interaction type;
    private final Direction face;
    private boolean cancelled;

    @Deprecated
    public BlockInteractEvent(Actor cause, Location location, Interaction type) {
        this(cause, location, null, type);
    }

    public BlockInteractEvent(Actor cause, Location location, @Nullable Direction face, Interaction type) {
        Preconditions.checkNotNull((Object)cause);
        Preconditions.checkNotNull((Object)location);
        Preconditions.checkNotNull((Object)((Object)type));
        this.cause = cause;
        this.location = location;
        this.face = face;
        this.type = type;
    }

    public Actor getCause() {
        return this.cause;
    }

    public Location getLocation() {
        return this.location;
    }

    @Nullable
    public Direction getFace() {
        return this.face;
    }

    public Interaction getType() {
        return this.type;
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}

