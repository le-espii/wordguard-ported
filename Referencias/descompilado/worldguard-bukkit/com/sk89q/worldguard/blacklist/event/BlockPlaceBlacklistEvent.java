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
import com.sk89q.worldguard.blacklist.event.BlockBlacklistEvent;
import com.sk89q.worldguard.blacklist.event.EventType;
import com.sk89q.worldguard.blacklist.target.Target;
import javax.annotation.Nullable;

public final class BlockPlaceBlacklistEvent
extends BlockBlacklistEvent {
    public BlockPlaceBlacklistEvent(@Nullable LocalPlayer player, BlockVector3 position, Target target) {
        super(player, position, target);
    }

    @Override
    public String getDescription() {
        return "place";
    }

    @Override
    public EventType getEventType() {
        return EventType.PLACE;
    }
}

