/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  org.bukkit.Bukkit
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.LivingEntity
 *  org.bukkit.event.Cancellable
 *  org.bukkit.event.Event
 *  org.bukkit.event.Event$Result
 *  org.bukkit.event.entity.EntityDamageEvent$DamageCause
 *  org.bukkit.event.player.PlayerInteractEvent
 */
package com.sk89q.worldguard.bukkit.util;

import com.google.common.base.Preconditions;
import com.sk89q.worldguard.bukkit.event.BulkEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public final class Events {
    private Events() {
    }

    public static void fire(Event event) {
        Preconditions.checkNotNull((Object)event);
        Bukkit.getServer().getPluginManager().callEvent(event);
    }

    public static <T extends Event> boolean fireAndTestCancel(T eventToFire) {
        Bukkit.getServer().getPluginManager().callEvent(eventToFire);
        return ((Cancellable)eventToFire).isCancelled();
    }

    public static <T extends Event> boolean fireToCancel(Cancellable original, T eventToFire) {
        Bukkit.getServer().getPluginManager().callEvent(eventToFire);
        if (((Cancellable)eventToFire).isCancelled()) {
            original.setCancelled(true);
            return true;
        }
        return false;
    }

    public static <T extends Event> boolean fireItemEventToCancel(PlayerInteractEvent original, T eventToFire) {
        Bukkit.getServer().getPluginManager().callEvent(eventToFire);
        if (((Cancellable)eventToFire).isCancelled()) {
            original.setUseItemInHand(Event.Result.DENY);
            return true;
        }
        return false;
    }

    public static <T extends Event & BulkEvent> boolean fireBulkEventToCancel(Cancellable original, T eventToFire) {
        Bukkit.getServer().getPluginManager().callEvent(eventToFire);
        if (((BulkEvent)eventToFire).getExplicitResult() == Event.Result.DENY) {
            original.setCancelled(true);
            return true;
        }
        return false;
    }

    public static boolean isFireCause(EntityDamageEvent.DamageCause cause) {
        return cause == EntityDamageEvent.DamageCause.FIRE || cause == EntityDamageEvent.DamageCause.FIRE_TICK;
    }

    public static boolean isExplosionCause(EntityDamageEvent.DamageCause cause) {
        return cause == EntityDamageEvent.DamageCause.BLOCK_EXPLOSION || cause == EntityDamageEvent.DamageCause.ENTITY_EXPLOSION;
    }

    public static void restoreStatistic(Entity entity, EntityDamageEvent.DamageCause cause) {
        if (cause == EntityDamageEvent.DamageCause.DROWNING && entity instanceof LivingEntity) {
            LivingEntity living = (LivingEntity)entity;
            living.setRemainingAir(living.getMaximumAir());
        }
        if (Events.isFireCause(cause)) {
            entity.setFireTicks(0);
        }
        if (cause == EntityDamageEvent.DamageCause.LAVA) {
            entity.setFireTicks(0);
        }
    }
}

