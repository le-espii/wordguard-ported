/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldguard.session.handler;

import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.session.Session;
import com.sk89q.worldguard.session.handler.Handler;

public class WaterBreathing
extends Handler {
    public static final Factory FACTORY = new Factory();
    public boolean waterBreathing;

    public WaterBreathing(Session session) {
        super(session);
    }

    public boolean hasWaterBreathing() {
        return this.waterBreathing;
    }

    public void setWaterBreathing(boolean waterBreathing) {
        this.waterBreathing = waterBreathing;
    }

    public static boolean set(LocalPlayer player, Session session, boolean value) {
        WaterBreathing waterBreathing = session.getHandler(WaterBreathing.class);
        if (waterBreathing != null) {
            waterBreathing.setWaterBreathing(value);
            return true;
        }
        return false;
    }

    public static class Factory
    extends Handler.Factory<WaterBreathing> {
        @Override
        public WaterBreathing create(Session session) {
            return new WaterBreathing(session);
        }
    }
}

