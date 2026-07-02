/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.sk89q.worldguard.blacklist;

import com.sk89q.worldguard.blacklist.event.BlacklistEvent;
import javax.annotation.Nullable;

class TrackedEvent {
    @Nullable
    private BlacklistEvent lastEvent;
    private long time = 0L;

    TrackedEvent() {
    }

    public boolean matches(BlacklistEvent other) {
        if (this.lastEvent == null) {
            return false;
        }
        long now = System.currentTimeMillis();
        return other.getEventType() == this.lastEvent.getEventType() && this.time > now - 3000L;
    }

    public void resetTimer() {
        this.time = System.currentTimeMillis();
    }

    public void setLastEvent(@Nullable BlacklistEvent lastEvent) {
        this.lastEvent = lastEvent;
    }
}

