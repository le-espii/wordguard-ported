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
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.session.MoveType;
import com.sk89q.worldguard.session.Session;
import com.sk89q.worldguard.session.handler.FlagValueChangeHandler;
import com.sk89q.worldguard.session.handler.Handler;
import javax.annotation.Nullable;

public class InvincibilityFlag
extends FlagValueChangeHandler<StateFlag.State> {
    public static final Factory FACTORY = new Factory();
    @Nullable
    private StateFlag.State invincibility;

    public InvincibilityFlag(Session session) {
        super(session, Flags.INVINCIBILITY);
    }

    @Override
    protected void onInitialValue(LocalPlayer player, ApplicableRegionSet set, StateFlag.State value) {
        this.invincibility = value;
    }

    @Override
    protected boolean onSetValue(LocalPlayer player, Location from, Location to, ApplicableRegionSet toSet, StateFlag.State currentValue, StateFlag.State lastValue, MoveType moveType) {
        this.invincibility = currentValue;
        return true;
    }

    @Override
    protected boolean onAbsentValue(LocalPlayer player, Location from, Location to, ApplicableRegionSet toSet, StateFlag.State lastValue, MoveType moveType) {
        this.invincibility = null;
        return true;
    }

    @Override
    @Nullable
    public StateFlag.State getInvincibility(LocalPlayer player) {
        if (this.invincibility == StateFlag.State.DENY && player.hasPermission("worldguard.god.override-regions")) {
            return null;
        }
        return this.invincibility;
    }

    public static class Factory
    extends Handler.Factory<InvincibilityFlag> {
        @Override
        public InvincibilityFlag create(Session session) {
            return new InvincibilityFlag(session);
        }
    }
}

