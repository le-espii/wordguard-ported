/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sk89q.worldedit.util.Location
 */
package com.sk89q.worldguard.session.handler;

import com.sk89q.worldedit.util.Location;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.session.MoveType;
import com.sk89q.worldguard.session.Session;
import com.sk89q.worldguard.session.handler.FlagValueChangeHandler;
import com.sk89q.worldguard.session.handler.Handler;
import com.sk89q.worldguard.util.formatting.component.Notify;

public class NotifyEntryFlag
extends FlagValueChangeHandler<Boolean> {
    public static final Factory FACTORY = new Factory();

    public NotifyEntryFlag(Session session) {
        super(session, Flags.NOTIFY_ENTER);
    }

    @Override
    protected void onInitialValue(LocalPlayer player, ApplicableRegionSet set, Boolean value) {
    }

    @Override
    protected boolean onSetValue(LocalPlayer player, Location from, Location to, ApplicableRegionSet toSet, Boolean currentValue, Boolean lastValue, MoveType moveType) {
        StringBuilder regionList = new StringBuilder();
        for (ProtectedRegion region : toSet) {
            if (regionList.length() != 0) {
                regionList.append(", ");
            }
            regionList.append(region.getId());
        }
        WorldGuard.getInstance().getPlatform().broadcastNotification(new Notify(player.getName(), " entered NOTIFY region: " + String.valueOf(regionList)).create());
        return true;
    }

    @Override
    protected boolean onAbsentValue(LocalPlayer player, Location from, Location to, ApplicableRegionSet toSet, Boolean lastValue, MoveType moveType) {
        return true;
    }

    public static class Factory
    extends Handler.Factory<NotifyEntryFlag> {
        @Override
        public NotifyEntryFlag create(Session session) {
            return new NotifyEntryFlag(session);
        }
    }
}

