/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.World
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.HumanEntity
 *  org.bukkit.entity.LivingEntity
 *  org.bukkit.entity.Player
 *  org.bukkit.entity.Projectile
 *  org.bukkit.entity.Tameable
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.entity.EntityCombustEvent
 *  org.bukkit.event.entity.EntityDamageByEntityEvent
 *  org.bukkit.event.entity.EntityDamageEvent
 *  org.bukkit.event.entity.FoodLevelChangeEvent
 */
package com.sk89q.worldguard.bukkit.listener;

import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.bukkit.listener.AbstractListener;
import com.sk89q.worldguard.bukkit.util.Entities;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Tameable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;

public class InvincibilityListener
extends AbstractListener {
    public InvincibilityListener(WorldGuardPlugin plugin) {
        super(plugin);
    }

    private boolean isInvincible(LocalPlayer player) {
        return WorldGuard.getInstance().getPlatform().getSessionManager().get(player).isInvincible(player);
    }

    @EventHandler(ignoreCancelled=true)
    public void onEntityDamage(EntityDamageEvent event) {
        Entity victim = event.getEntity();
        if (Entities.isNPC(victim)) {
            return;
        }
        if (victim instanceof Player) {
            Player player = (Player)victim;
            LocalPlayer localPlayer = WorldGuardPlugin.inst().wrapPlayer(player);
            if (this.isInvincible(localPlayer)) {
                player.setFireTicks(0);
                event.setCancelled(true);
                if (event instanceof EntityDamageByEntityEvent) {
                    EntityDamageByEntityEvent byEntityEvent = (EntityDamageByEntityEvent)event;
                    Entity attacker = byEntityEvent.getDamager();
                    if (attacker instanceof Projectile && ((Projectile)attacker).getShooter() instanceof Entity) {
                        attacker = (Entity)((Projectile)attacker).getShooter();
                    }
                    if (!(!InvincibilityListener.getWorldConfig((World)player.getWorld()).regionInvinciblityRemovesMobs || !(attacker instanceof LivingEntity) || attacker instanceof Player || attacker instanceof Tameable && ((Tameable)attacker).isTamed())) {
                        attacker.remove();
                    }
                }
            }
        }
    }

    @EventHandler(ignoreCancelled=true)
    public void onEntityCombust(EntityCombustEvent event) {
        Entity entity = event.getEntity();
        if (Entities.isNPC(entity)) {
            return;
        }
        if (entity instanceof Player) {
            Player player = (Player)entity;
            LocalPlayer localPlayer = WorldGuardPlugin.inst().wrapPlayer(player);
            if (this.isInvincible(localPlayer)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(ignoreCancelled=true)
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        if (Entities.isNPC((Entity)event.getEntity())) {
            return;
        }
        HumanEntity humanEntity = event.getEntity();
        if (humanEntity instanceof Player) {
            Player player = (Player)humanEntity;
            LocalPlayer localPlayer = WorldGuardPlugin.inst().wrapPlayer(player);
            if (event.getFoodLevel() < player.getFoodLevel() && this.isInvincible(localPlayer)) {
                event.setCancelled(true);
            }
        }
    }
}

