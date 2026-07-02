/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.event.platform;

import com.sk89q.worldedit.event.Event;
import com.sk89q.worldedit.session.SessionKey;

public final class SessionIdleEvent
extends Event {
    private final SessionKey key;

    public SessionIdleEvent(SessionKey key) {
        this.key = key;
    }

    public SessionKey getKey() {
        return this.key;
    }
}

