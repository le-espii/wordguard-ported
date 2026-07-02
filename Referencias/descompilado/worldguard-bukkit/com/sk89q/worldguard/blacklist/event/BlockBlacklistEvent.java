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
import com.sk89q.worldguard.blacklist.event.AbstractBlacklistEvent;
import com.sk89q.worldguard.blacklist.target.Target;
import javax.annotation.Nullable;

abstract class BlockBlacklistEvent
extends AbstractBlacklistEvent {
    BlockBlacklistEvent(@Nullable LocalPlayer player, BlockVector3 position, Target target) {
        super(player, position, target);
    }

    @Override
    public String getLoggerMessage() {
        return this.getPlayerName() + " tried to " + this.getDescription() + " " + this.getTarget().getFriendlyName();
    }

    @Override
    public BlockVector3 getLoggedPosition() {
        return this.getPosition();
    }
}

