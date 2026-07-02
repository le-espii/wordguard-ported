/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 */
package com.sk89q.worldguard.bukkit.event.debug;

import com.google.common.base.Preconditions;

public class CancelAttempt {
    private final boolean before;
    private final boolean after;
    private final StackTraceElement[] stackTrace;

    public CancelAttempt(boolean before, boolean after, StackTraceElement[] stackTrace) {
        Preconditions.checkNotNull((Object)stackTrace, (Object)"stackTrace");
        this.before = before;
        this.after = after;
        this.stackTrace = stackTrace;
    }

    public boolean getBefore() {
        return this.before;
    }

    public boolean getAfter() {
        return this.after;
    }

    public StackTraceElement[] getStackTrace() {
        return this.stackTrace;
    }
}

