/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.event.Cancellable
 */
package com.sk89q.worldguard.bukkit.event.debug;

import com.sk89q.worldguard.bukkit.event.debug.CancelAttempt;
import java.util.List;
import org.bukkit.event.Cancellable;

public interface CancelLogging
extends Cancellable {
    public List<CancelAttempt> getCancels();
}

