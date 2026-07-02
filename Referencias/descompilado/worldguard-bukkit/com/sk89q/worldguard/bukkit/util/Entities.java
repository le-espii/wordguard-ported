/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  java.lang.runtime.SwitchBootstraps
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  org.bukkit.entity.Allay
 *  org.bukkit.entity.Ambient
 *  org.bukkit.entity.ArmorStand
 *  org.bukkit.entity.Arrow
 *  org.bukkit.entity.BreezeWindCharge
 *  org.bukkit.entity.Creature
 *  org.bukkit.entity.Creeper
 *  org.bukkit.entity.EnderCrystal
 *  org.bukkit.entity.EnderDragon
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.EntityType
 *  org.bukkit.entity.Fireball
 *  org.bukkit.entity.Firework
 *  org.bukkit.entity.Flying
 *  org.bukkit.entity.Hanging
 *  org.bukkit.entity.LivingEntity
 *  org.bukkit.entity.Minecart
 *  org.bukkit.entity.Monster
 *  org.bukkit.entity.NPC
 *  org.bukkit.entity.Player
 *  org.bukkit.entity.Projectile
 *  org.bukkit.entity.Shulker
 *  org.bukkit.entity.Slime
 *  org.bukkit.entity.SpectralArrow
 *  org.bukkit.entity.Steerable
 *  org.bukkit.entity.TNTPrimed
 *  org.bukkit.entity.Tameable
 *  org.bukkit.entity.Vehicle
 *  org.bukkit.entity.WindCharge
 *  org.bukkit.entity.Wither
 *  org.bukkit.entity.minecart.ExplosiveMinecart
 *  org.bukkit.event.entity.CreatureSpawnEvent$SpawnReason
 *  org.bukkit.inventory.InventoryHolder
 *  org.bukkit.projectiles.ProjectileSource
 */
package com.sk89q.worldguard.bukkit.util;

import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import java.lang.runtime.SwitchBootstraps;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.bukkit.entity.Allay;
import org.bukkit.entity.Ambient;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.BreezeWindCharge;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Flying;
import org.bukkit.entity.Hanging;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Monster;
import org.bukkit.entity.NPC;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Shulker;
import org.bukkit.entity.Slime;
import org.bukkit.entity.SpectralArrow;
import org.bukkit.entity.Steerable;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.entity.Tameable;
import org.bukkit.entity.Vehicle;
import org.bukkit.entity.WindCharge;
import org.bukkit.entity.Wither;
import org.bukkit.entity.minecart.ExplosiveMinecart;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.projectiles.ProjectileSource;

public final class Entities {
    private Entities() {
    }

    public static boolean isTamed(@Nullable Entity entity) {
        return entity instanceof Tameable && ((Tameable)entity).isTamed();
    }

    public static boolean isTNTBased(Entity entity) {
        return entity instanceof TNTPrimed || entity instanceof ExplosiveMinecart;
    }

    public static boolean isFireball(EntityType type) {
        return type == EntityType.FIREBALL || type == EntityType.SMALL_FIREBALL;
    }

    public static boolean isRiddenOnUse(Entity entity) {
        return entity instanceof Steerable ? ((Steerable)entity).hasSaddle() : entity instanceof Vehicle;
    }

    public static boolean isVehicle(EntityType type) {
        return Entities.isBoat(type) || Entities.isMinecart(type);
    }

    public static boolean isBoat(EntityType type) {
        return switch (type) {
            case EntityType.BOAT, EntityType.CHEST_BOAT -> true;
            default -> false;
        };
    }

    public static boolean isMinecart(EntityType type) {
        return switch (type) {
            case EntityType.MINECART, EntityType.CHEST_MINECART, EntityType.COMMAND_BLOCK_MINECART, EntityType.FURNACE_MINECART, EntityType.HOPPER_MINECART, EntityType.SPAWNER_MINECART, EntityType.TNT_MINECART -> true;
            default -> false;
        };
    }

    public static Entity getShooter(Entity entity) {
        while (entity instanceof Projectile) {
            Projectile projectile = (Projectile)entity;
            ProjectileSource remover = projectile.getShooter();
            if (remover instanceof Entity && remover != entity) {
                entity = (Entity)remover;
                continue;
            }
            return entity;
        }
        return entity;
    }

    public static boolean isHostile(Entity entity) {
        return entity instanceof Monster || entity instanceof Slime || entity instanceof Flying || entity instanceof EnderDragon || entity instanceof Shulker;
    }

    public static boolean isNonHostile(Entity entity) {
        return !Entities.isHostile(entity) && entity instanceof Creature;
    }

    public static boolean isAmbient(Entity entity) {
        return entity instanceof Ambient;
    }

    public static boolean isNPC(Entity entity) {
        return entity instanceof NPC || entity.hasMetadata("NPC");
    }

    public static boolean isNonPlayerCreature(Entity entity) {
        return entity instanceof LivingEntity && !(entity instanceof Player);
    }

    public static boolean isConsideredBuildingIfUsed(Entity entity) {
        return entity instanceof Hanging || entity instanceof ArmorStand || entity instanceof EnderCrystal || entity instanceof Allay || entity instanceof Minecart && entity instanceof InventoryHolder;
    }

    public static boolean isPotionArrow(Entity entity) {
        return entity instanceof Arrow || entity instanceof SpectralArrow;
    }

    public static boolean isAoECloud(EntityType type) {
        return type == EntityType.AREA_EFFECT_CLOUD;
    }

    public static boolean isPluginSpawning(CreatureSpawnEvent.SpawnReason spawnReason) {
        return switch (spawnReason) {
            case CreatureSpawnEvent.SpawnReason.CUSTOM, CreatureSpawnEvent.SpawnReason.COMMAND -> true;
            default -> false;
        };
    }

    @Nonnull
    public static StateFlag getExplosionFlag(Entity entity) {
        Entity entity2 = entity;
        int n = 0;
        return switch (SwitchBootstraps.typeSwitch("typeSwitch", new Object[]{BreezeWindCharge.class, WindCharge.class, Firework.class, Fireball.class, Wither.class, Creeper.class, TNTPrimed.class, ExplosiveMinecart.class, EnderDragon.class}, (Object)entity2, (int)n)) {
            case 0 -> {
                BreezeWindCharge breezeWindCharge = (BreezeWindCharge)entity2;
                yield Flags.BREEZE_WIND_CHARGE;
            }
            case 1 -> {
                WindCharge windCharge = (WindCharge)entity2;
                yield Flags.WIND_CHARGE_BURST;
            }
            case 2 -> {
                Firework firework = (Firework)entity2;
                yield Flags.FIREWORK_DAMAGE;
            }
            case 3 -> {
                Fireball fireball = (Fireball)entity2;
                yield Flags.GHAST_FIREBALL;
            }
            case 4 -> {
                Wither wither = (Wither)entity2;
                yield Flags.WITHER_DAMAGE;
            }
            case 5 -> {
                Creeper creeper = (Creeper)entity2;
                yield Flags.CREEPER_EXPLOSION;
            }
            case 6 -> {
                TNTPrimed tnt = (TNTPrimed)entity2;
                yield Flags.TNT;
            }
            case 7 -> {
                ExplosiveMinecart minecart = (ExplosiveMinecart)entity2;
                yield Flags.TNT;
            }
            case 8 -> {
                EnderDragon dragon = (EnderDragon)entity2;
                yield Flags.ENDERDRAGON_BLOCK_DAMAGE;
            }
            default -> Flags.OTHER_EXPLOSION;
        };
    }
}

