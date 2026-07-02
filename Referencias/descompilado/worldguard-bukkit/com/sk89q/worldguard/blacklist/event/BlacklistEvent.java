/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sk89q.worldedit.math.BlockVector3
 *  javax.annotation.Nullable
 */
package com.sk89q.worldguard.blacklist.event;

import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.blacklist.event.EventType;
import com.sk89q.worldguard.blacklist.target.Target;
import javax.annotation.Nullable;

public interface BlacklistEvent {
    @Nullable
    public LocalPlayer getPlayer();

    public String getCauseName();

    public BlockVector3 getPosition();

    public BlockVector3 getLoggedPosition();

    public Target getTarget();

    public String getDescription();

    public String getLoggerMessage();

    public EventType getEventType();
}

