/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.event.Event$Result
 */
package com.sk89q.worldguard.bukkit.event;

import org.bukkit.event.Event;

public interface Handleable {
    public Event.Result getResult();

    public void setResult(Event.Result var1);
}

