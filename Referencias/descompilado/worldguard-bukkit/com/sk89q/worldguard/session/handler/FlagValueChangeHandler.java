/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sk89q.worldedit.util.Location
 */
package com.sk89q.worldguard.session.handler;

import com.sk89q.worldedit.util.Location;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.session.MoveType;
import com.sk89q.worldguard.session.Session;
import com.sk89q.worldguard.session.handler.Handler;
import java.util.Set;

public abstract class FlagValueChangeHandler<T>
extends Handler {
    private final Flag<T> flag;
    private T lastValue;

    protected FlagValueChangeHandler(Session session, Flag<T> flag) {
        super(session);
        this.flag = flag;
    }

    @Override
    public final void initialize(LocalPlayer player, Location current, ApplicableRegionSet set) {
        this.lastValue = set.queryValue(player, this.flag);
        this.onInitialValue(player, set, this.lastValue);
    }

    @Override
    public final void uninitialize(LocalPlayer player, Location current, ApplicableRegionSet set) {
        this.onClearValue(player, set);
        this.lastValue = null;
    }

    @Override
    public boolean onCrossBoundary(LocalPlayer player, Location from, Location to, ApplicableRegionSet toSet, Set<ProtectedRegion> entered, Set<ProtectedRegion> exited, MoveType moveType) {
        if (entered.isEmpty() && exited.isEmpty() && from.getExtent().equals((Object)to.getExtent())) {
            return true;
        }
        T currentValue = toSet.queryValue(player, this.flag);
        boolean allowed = true;
        if (currentValue == null && this.lastValue != null) {
            allowed = this.onAbsentValue(player, from, to, toSet, this.lastValue, moveType);
        } else if (currentValue != null && currentValue != this.lastValue) {
            allowed = this.onSetValue(player, from, to, toSet, currentValue, this.lastValue, moveType);
        }
        if (allowed) {
            this.lastValue = currentValue;
        }
        return allowed;
    }

    protected abstract void onInitialValue(LocalPlayer var1, ApplicableRegionSet var2, T var3);

    protected abstract boolean onSetValue(LocalPlayer var1, Location var2, Location var3, ApplicableRegionSet var4, T var5, T var6, MoveType var7);

    protected abstract boolean onAbsentValue(LocalPlayer var1, Location var2, Location var3, ApplicableRegionSet var4, T var5, MoveType var6);

    protected void onClearValue(LocalPlayer player, ApplicableRegionSet set) {
        if (this.lastValue != null) {
            Location current = player.getLocation();
            this.onAbsentValue(player, current, current, set, this.lastValue, MoveType.OTHER_NON_CANCELLABLE);
        }
    }
}

