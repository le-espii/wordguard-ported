/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 */
package com.sk89q.worldguard.bukkit.event.debug;

import com.google.common.collect.ImmutableList;
import com.sk89q.worldguard.bukkit.event.debug.CancelAttempt;
import java.util.ArrayList;
import java.util.List;

public class CancelLogger {
    private List<CancelAttempt> entries = new ArrayList<CancelAttempt>();

    public void log(boolean before, boolean after, StackTraceElement[] stackTrace) {
        this.entries.add(new CancelAttempt(before, after, stackTrace));
    }

    public List<CancelAttempt> getCancels() {
        return ImmutableList.copyOf(this.entries);
    }
}

