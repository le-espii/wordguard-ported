/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sk89q.worldedit.world.gamemode.GameModes
 */
package com.sk89q.worldguard.session.handler;

import com.sk89q.worldedit.world.gamemode.GameModes;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.session.Session;
import com.sk89q.worldguard.session.handler.Handler;

public class FeedFlag
extends Handler {
    public static final Factory FACTORY = new Factory();
    private long lastFeed = 0L;

    public FeedFlag(Session session) {
        super(session);
    }

    @Override
    public void tick(LocalPlayer player, ApplicableRegionSet set) {
        long now = System.currentTimeMillis();
        Integer feedAmount = set.queryValue(player, Flags.FEED_AMOUNT);
        Integer feedDelay = set.queryValue(player, Flags.FEED_DELAY);
        Integer minHunger = set.queryValue(player, Flags.MIN_FOOD);
        Integer maxHunger = set.queryValue(player, Flags.MAX_FOOD);
        if (feedAmount == null || feedDelay == null || feedAmount == 0 || feedDelay < 0) {
            return;
        }
        if (feedAmount < 0 && (this.getSession().isInvincible(player) || player.getGameMode() != GameModes.SURVIVAL && player.getGameMode() != GameModes.ADVENTURE)) {
            return;
        }
        if (minHunger == null) {
            minHunger = 0;
        }
        if (maxHunger == null) {
            maxHunger = 20;
        }
        minHunger = Math.min(20, minHunger);
        maxHunger = Math.min(20, maxHunger);
        if (player.getFoodLevel() >= (double)maxHunger.intValue() && feedAmount > 0) {
            return;
        }
        if (feedDelay <= 0) {
            player.setFoodLevel((feedAmount > 0 ? maxHunger : minHunger).intValue());
            player.setSaturation(player.getFoodLevel());
            this.lastFeed = now;
        } else if (now - this.lastFeed > (long)(feedDelay * 1000)) {
            player.setFoodLevel(Math.min((double)maxHunger.intValue(), Math.max((double)minHunger.intValue(), player.getFoodLevel() + (double)feedAmount.intValue())));
            player.setSaturation(player.getFoodLevel());
            this.lastFeed = now;
        }
    }

    public static class Factory
    extends Handler.Factory<FeedFlag> {
        @Override
        public FeedFlag create(Session session) {
            return new FeedFlag(session);
        }
    }
}

