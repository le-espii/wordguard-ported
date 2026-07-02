/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.sk89q.worldedit.math.BlockVector3
 *  javax.annotation.Nullable
 */
package com.sk89q.worldguard.blacklist.event;

import com.google.common.base.Preconditions;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.blacklist.event.BlacklistEvent;
import com.sk89q.worldguard.blacklist.target.Target;
import javax.annotation.Nullable;

abstract class AbstractBlacklistEvent
implements BlacklistEvent {
    @Nullable
    private final LocalPlayer player;
    private final BlockVector3 position;
    private final Target target;

    AbstractBlacklistEvent(@Nullable LocalPlayer player, BlockVector3 position, Target target) {
        Preconditions.checkNotNull((Object)position);
        Preconditions.checkNotNull((Object)target);
        this.player = player;
        this.position = position;
        this.target = target;
    }

    @Override
    @Nullable
    public LocalPlayer getPlayer() {
        return this.player;
    }

    @Override
    public String getCauseName() {
        return this.player != null ? this.player.getName() : this.position.toString();
    }

    @Override
    public BlockVector3 getPosition() {
        return this.position;
    }

    @Override
    public Target getTarget() {
        return this.target;
    }

    protected String getPlayerName() {
        return this.player == null ? "(unknown)" : this.player.getName();
    }
}

