/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sk89q.worldedit.util.Location
 *  javax.annotation.Nullable
 */
package com.sk89q.worldguard.session.handler;

import com.sk89q.worldedit.util.Location;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.session.MoveType;
import com.sk89q.worldguard.session.Session;
import com.sk89q.worldguard.session.handler.FlagValueChangeHandler;
import com.sk89q.worldguard.session.handler.Handler;
import java.util.regex.Pattern;
import javax.annotation.Nullable;

public class TimeLockFlag
extends FlagValueChangeHandler<String> {
    public static final Factory FACTORY = new Factory();
    private long initialTime;
    private boolean initialRelative;
    private static Pattern timePattern = Pattern.compile("([+\\-])?\\d+");

    public TimeLockFlag(Session session) {
        super(session, Flags.TIME_LOCK);
    }

    private void updatePlayerTime(LocalPlayer player, @Nullable String value) {
        if (value == null || !timePattern.matcher(value).matches()) {
            return;
        }
        boolean relative = value.startsWith("+") || value.startsWith("-");
        long time = Long.parseLong(value);
        player.setPlayerTime(time, relative);
    }

    @Override
    protected void onInitialValue(LocalPlayer player, ApplicableRegionSet set, String value) {
        this.initialRelative = player.isPlayerTimeRelative();
        this.initialTime = player.getPlayerTimeOffset();
        this.updatePlayerTime(player, value);
    }

    @Override
    protected boolean onSetValue(LocalPlayer player, Location from, Location to, ApplicableRegionSet toSet, String currentValue, String lastValue, MoveType moveType) {
        if (lastValue == null) {
            this.initialRelative = player.isPlayerTimeRelative();
            this.initialTime = player.getPlayerTimeOffset();
        }
        this.updatePlayerTime(player, currentValue);
        return true;
    }

    @Override
    protected boolean onAbsentValue(LocalPlayer player, Location from, Location to, ApplicableRegionSet toSet, String lastValue, MoveType moveType) {
        player.setPlayerTime(this.initialTime, this.initialRelative);
        this.initialTime = 0L;
        this.initialRelative = true;
        return true;
    }

    public static class Factory
    extends Handler.Factory<TimeLockFlag> {
        @Override
        public TimeLockFlag create(Session session) {
            return new TimeLockFlag(session);
        }
    }
}

