/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.sk89q.worldedit.util.Location
 *  javax.annotation.Nullable
 */
package com.sk89q.worldguard.session.handler;

import com.google.common.base.Preconditions;
import com.sk89q.worldedit.util.Location;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.session.MoveType;
import com.sk89q.worldguard.session.Session;
import java.util.Set;
import javax.annotation.Nullable;

public abstract class Handler {
    private final Session session;

    protected Handler(Session session) {
        Preconditions.checkNotNull((Object)session, (Object)"session");
        this.session = session;
    }

    public Session getSession() {
        return this.session;
    }

    public void initialize(LocalPlayer player, Location current, ApplicableRegionSet set) {
    }

    public void uninitialize(LocalPlayer player, Location current, ApplicableRegionSet set) {
    }

    public boolean testMoveTo(LocalPlayer player, Location from, Location to, ApplicableRegionSet toSet, MoveType moveType) {
        return true;
    }

    public boolean onCrossBoundary(LocalPlayer player, Location from, Location to, ApplicableRegionSet toSet, Set<ProtectedRegion> entered, Set<ProtectedRegion> exited, MoveType moveType) {
        return true;
    }

    public void tick(LocalPlayer player, ApplicableRegionSet set) {
    }

    @Nullable
    public StateFlag.State getInvincibility(LocalPlayer player) {
        return null;
    }

    public static abstract class Factory<T extends Handler> {
        public abstract T create(Session var1);
    }
}

