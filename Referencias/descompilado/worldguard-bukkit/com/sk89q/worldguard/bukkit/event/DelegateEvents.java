/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.event.Event$Result
 */
package com.sk89q.worldguard.bukkit.event;

import com.sk89q.worldguard.bukkit.event.DelegateEvent;
import com.sk89q.worldguard.bukkit.event.Handleable;
import org.bukkit.event.Event;

public final class DelegateEvents {
    private DelegateEvents() {
    }

    public static <T extends DelegateEvent> T setSilent(T event) {
        event.setSilent(true);
        return event;
    }

    public static <T extends DelegateEvent> T setSilent(T event, boolean silent) {
        event.setSilent(silent);
        return event;
    }

    public static <T extends Handleable> T setAllowed(T event, boolean allowed) {
        if (allowed) {
            event.setResult(Event.Result.ALLOW);
        }
        return event;
    }
}

