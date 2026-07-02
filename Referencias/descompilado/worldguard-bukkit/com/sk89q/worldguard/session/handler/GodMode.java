/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.sk89q.worldguard.session.handler;

import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.session.Session;
import com.sk89q.worldguard.session.handler.Handler;
import javax.annotation.Nullable;

public class GodMode
extends Handler {
    public static final Factory FACTORY = new Factory();
    private boolean godMode;

    public GodMode(Session session) {
        super(session);
    }

    public boolean hasGodMode(LocalPlayer player) {
        return this.godMode;
    }

    public void setGodMode(LocalPlayer player, boolean godMode) {
        this.godMode = godMode;
    }

    @Override
    @Nullable
    public StateFlag.State getInvincibility(LocalPlayer player) {
        return this.hasGodMode(player) ? StateFlag.State.ALLOW : null;
    }

    public static boolean set(LocalPlayer player, Session session, boolean value) {
        GodMode godMode = session.getHandler(GodMode.class);
        if (godMode != null) {
            godMode.setGodMode(player, value);
            return true;
        }
        return false;
    }

    public static class Factory
    extends Handler.Factory<GodMode> {
        @Override
        public GodMode create(Session session) {
            return new GodMode(session);
        }
    }
}

