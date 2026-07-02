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

public class HealFlag
extends Handler {
    public static final Factory FACTORY = new Factory();
    private long lastHeal = 0L;

    public HealFlag(Session session) {
        super(session);
    }

    @Override
    public void tick(LocalPlayer player, ApplicableRegionSet set) {
        if (player.getHealth() <= 0.0) {
            return;
        }
        long now = System.currentTimeMillis();
        Integer healAmount = set.queryValue(player, Flags.HEAL_AMOUNT);
        Integer healDelay = set.queryValue(player, Flags.HEAL_DELAY);
        Double minHealth = set.queryValue(player, Flags.MIN_HEAL);
        Double maxHealth = set.queryValue(player, Flags.MAX_HEAL);
        if (healAmount == null || healDelay == null || healAmount == 0 || healDelay < 0) {
            return;
        }
        if (healAmount < 0 && (this.getSession().isInvincible(player) || player.getGameMode() != GameModes.SURVIVAL && player.getGameMode() != GameModes.ADVENTURE)) {
            return;
        }
        if (minHealth == null) {
            minHealth = 0.0;
        }
        if (maxHealth == null) {
            maxHealth = player.getMaxHealth();
        }
        minHealth = Math.min(player.getMaxHealth(), minHealth);
        maxHealth = Math.min(player.getMaxHealth(), maxHealth);
        if (player.getHealth() >= maxHealth && healAmount > 0) {
            return;
        }
        if (healDelay <= 0) {
            player.setHealth(healAmount > 0 ? maxHealth : minHealth);
            this.lastHeal = now;
        } else if (now - this.lastHeal > (long)(healDelay * 1000)) {
            player.setHealth(Math.min(maxHealth, Math.max(minHealth, player.getHealth() + (double)healAmount.intValue())));
            this.lastHeal = now;
        }
    }

    public static class Factory
    extends Handler.Factory<HealFlag> {
        @Override
        public HealFlag create(Session session) {
            return new HealFlag(session);
        }
    }
}

