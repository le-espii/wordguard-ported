/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.ChatColor
 *  org.bukkit.Material
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Arrow
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.Player
 *  org.bukkit.entity.SpectralArrow
 *  org.bukkit.event.Event
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.entity.EntityDamageByEntityEvent
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.inventory.meta.ItemMeta
 *  org.bukkit.inventory.meta.PotionMeta
 *  org.bukkit.potion.PotionEffect
 *  org.bukkit.potion.PotionEffectType
 *  org.bukkit.potion.PotionType
 */
package com.sk89q.worldguard.bukkit.listener;

import com.sk89q.worldguard.bukkit.BukkitWorldConfiguration;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.bukkit.event.entity.DamageEntityEvent;
import com.sk89q.worldguard.bukkit.event.inventory.UseItemEvent;
import com.sk89q.worldguard.bukkit.listener.AbstractListener;
import com.sk89q.worldguard.bukkit.util.Entities;
import java.util.ArrayList;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.SpectralArrow;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

public class BlockedPotionsListener
extends AbstractListener {
    public BlockedPotionsListener(WorldGuardPlugin plugin) {
        super(plugin);
    }

    private PotionEffectType getBlockedEffectByArrow(Arrow arrow, BukkitWorldConfiguration wcfg) {
        ArrayList effects = new ArrayList();
        PotionType potionType = arrow.getBasePotionType();
        if (potionType != null) {
            effects.addAll(potionType.getPotionEffects());
        }
        effects.addAll(arrow.getCustomEffects());
        for (PotionEffect potionEffect : effects) {
            if (!wcfg.blockPotions.contains(potionEffect.getType())) continue;
            return potionEffect.getType();
        }
        return null;
    }

    @EventHandler
    public void onProjectile(DamageEntityEvent event) {
        Event event2 = event.getOriginalEvent();
        if (!(event2 instanceof EntityDamageByEntityEvent)) {
            return;
        }
        EntityDamageByEntityEvent originalEvent = (EntityDamageByEntityEvent)event2;
        if (!Entities.isPotionArrow(originalEvent.getDamager())) {
            return;
        }
        BukkitWorldConfiguration wcfg = BlockedPotionsListener.getWorldConfig(event.getWorld());
        PotionEffectType blockedEffect = null;
        if (originalEvent.getDamager() instanceof SpectralArrow) {
            if (wcfg.blockPotions.contains(PotionEffectType.GLOWING)) {
                blockedEffect = PotionEffectType.GLOWING;
            }
        } else {
            Entity entity = originalEvent.getDamager();
            if (entity instanceof Arrow) {
                Arrow arrow = (Arrow)entity;
                blockedEffect = this.getBlockedEffectByArrow(arrow, wcfg);
            }
        }
        if (blockedEffect != null) {
            Player player = event.getCause().getFirstPlayer();
            if (player != null) {
                if (BlockedPotionsListener.getPlugin().hasPermission((CommandSender)player, "worldguard.override.potions")) {
                    return;
                }
                player.sendMessage(String.valueOf(ChatColor.RED) + "Sorry, arrows with " + blockedEffect.getName() + " are presently disabled.");
            }
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onItemInteract(UseItemEvent event) {
        BukkitWorldConfiguration wcfg = BlockedPotionsListener.getWorldConfig(event.getWorld());
        ItemStack item = event.getItemStack();
        if (item.getType() != Material.POTION && item.getType() != Material.SPLASH_POTION && item.getType() != Material.LINGERING_POTION) {
            return;
        }
        if (!wcfg.blockPotions.isEmpty()) {
            PotionEffectType blockedEffect = null;
            ItemMeta itemMeta = item.getItemMeta();
            if (!(itemMeta instanceof PotionMeta)) {
                return;
            }
            PotionMeta meta = (PotionMeta)itemMeta;
            ArrayList effects = new ArrayList();
            if (meta.getBasePotionType() != null) {
                effects.addAll(meta.getBasePotionType().getPotionEffects());
            }
            effects.addAll(meta.getCustomEffects());
            for (PotionEffect potionEffect : effects) {
                if (!wcfg.blockPotions.contains(potionEffect.getType())) continue;
                blockedEffect = potionEffect.getType();
                break;
            }
            if (blockedEffect != null) {
                Player player = event.getCause().getFirstPlayer();
                if (player != null) {
                    if (BlockedPotionsListener.getPlugin().hasPermission((CommandSender)player, "worldguard.override.potions")) {
                        if (wcfg.blockPotionsAlways && (item.getType() == Material.SPLASH_POTION || item.getType() == Material.LINGERING_POTION)) {
                            player.sendMessage(String.valueOf(ChatColor.RED) + "Sorry, potions with " + blockedEffect.getName() + " can't be thrown, even if you have a permission to bypass it, due to limitations (and because overly-reliable potion blocking is on).");
                            event.setCancelled(true);
                        }
                    } else {
                        player.sendMessage(String.valueOf(ChatColor.RED) + "Sorry, potions with " + blockedEffect.getName() + " are presently disabled.");
                        event.setCancelled(true);
                    }
                } else {
                    event.setCancelled(true);
                }
            }
        }
    }
}

