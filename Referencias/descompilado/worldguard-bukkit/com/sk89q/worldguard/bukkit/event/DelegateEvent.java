/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.Lists
 *  javax.annotation.Nullable
 *  org.bukkit.event.Cancellable
 *  org.bukkit.event.Event
 *  org.bukkit.event.Event$Result
 */
package com.sk89q.worldguard.bukkit.event;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.sk89q.worldguard.bukkit.cause.Cause;
import com.sk89q.worldguard.bukkit.event.Handleable;
import com.sk89q.worldguard.protection.flags.StateFlag;
import java.util.List;
import javax.annotation.Nullable;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;

public abstract class DelegateEvent
extends Event
implements Cancellable,
Handleable {
    @Nullable
    private final Event originalEvent;
    private final Cause cause;
    private final List<StateFlag> relevantFlags = Lists.newArrayList();
    private Event.Result result = Event.Result.DEFAULT;
    private boolean silent;

    protected DelegateEvent(@Nullable Event originalEvent, Cause cause) {
        Preconditions.checkNotNull((Object)cause);
        this.originalEvent = originalEvent;
        this.cause = cause;
    }

    @Nullable
    public Event getOriginalEvent() {
        return this.originalEvent;
    }

    public Cause getCause() {
        return this.cause;
    }

    public List<StateFlag> getRelevantFlags() {
        return this.relevantFlags;
    }

    public boolean isCancelled() {
        return this.getResult() == Event.Result.DENY;
    }

    public void setCancelled(boolean cancel) {
        if (cancel) {
            this.setResult(Event.Result.DENY);
        }
    }

    @Override
    public Event.Result getResult() {
        return this.result;
    }

    @Override
    public void setResult(Event.Result result) {
        this.result = result;
    }

    public boolean isSilent() {
        return this.silent;
    }

    public DelegateEvent setSilent(boolean silent) {
        this.silent = silent;
        return this;
    }

    public DelegateEvent setAllowed(boolean allowed) {
        if (allowed) {
            this.setResult(Event.Result.ALLOW);
        }
        return this;
    }
}

