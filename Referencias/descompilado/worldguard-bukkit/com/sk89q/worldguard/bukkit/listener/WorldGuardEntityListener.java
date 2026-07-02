/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sk89q.worldedit.bukkit.BukkitAdapter
 *  com.sk89q.worldedit.math.BlockVector3
 *  com.sk89q.worldedit.world.World
 *  com.sk89q.worldedit.world.entity.EntityType
 *  org.bukkit.Location
 *  org.bukkit.Material
 *  org.bukkit.World
 *  org.bukkit.block.Block
 *  org.bukkit.block.BlockState
 *  org.bukkit.entity.AbstractWindCharge
 *  org.bukkit.entity.ArmorStand
 *  org.bukkit.entity.Creeper
 *  org.bukkit.entity.EnderCrystal
 *  org.bukkit.entity.EnderDragon
 *  org.bukkit.entity.Enderman
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.EntityType
 *  org.bukkit.entity.FallingBlock
 *  org.bukkit.entity.Fireball
 *  org.bukkit.entity.HumanEntity
 *  org.bukkit.entity.ItemFrame
 *  org.bukkit.entity.LivingEntity
 *  org.bukkit.entity.Pig
 *  org.bukkit.entity.Player
 *  org.bukkit.entity.Projectile
 *  org.bukkit.entity.TNTPrimed
 *  org.bukkit.entity.Tameable
 *  org.bukkit.entity.WindCharge
 *  org.bukkit.entity.Wither
 *  org.bukkit.entity.WitherSkull
 *  org.bukkit.entity.Wolf
 *  org.bukkit.entity.minecart.ExplosiveMinecart
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.EventPriority
 *  org.bukkit.event.entity.CreatureSpawnEvent
 *  org.bukkit.event.entity.CreatureSpawnEvent$SpawnReason
 *  org.bukkit.event.entity.CreeperPowerEvent
 *  org.bukkit.event.entity.EntityBreakDoorEvent
 *  org.bukkit.event.entity.EntityChangeBlockEvent
 *  org.bukkit.event.entity.EntityDamageByBlockEvent
 *  org.bukkit.event.entity.EntityDamageByEntityEvent
 *  org.bukkit.event.entity.EntityDamageEvent
 *  org.bukkit.event.entity.EntityDamageEvent$DamageCause
 *  org.bukkit.event.entity.EntityDeathEvent
 *  org.bukkit.event.entity.EntityExplodeEvent
 *  org.bukkit.event.entity.EntityInteractEvent
 *  org.bukkit.event.entity.EntityRegainHealthEvent
 *  org.bukkit.event.entity.EntityRegainHealthEvent$RegainReason
 *  org.bukkit.event.entity.EntityTransformEvent
 *  org.bukkit.event.entity.EntityTransformEvent$TransformReason
 *  org.bukkit.event.entity.ExplosionPrimeEvent
 *  org.bukkit.event.entity.FoodLevelChangeEvent
 *  org.bukkit.event.entity.PigZapEvent
 *  org.bukkit.event.entity.PlayerDeathEvent
 *  org.bukkit.event.vehicle.VehicleEnterEvent
 *  org.bukkit.event.world.PortalCreateEvent
 *  org.bukkit.event.world.PortalCreateEvent$CreateReason
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.projectiles.ProjectileSource
 */
package com.sk89q.worldguard.bukkit.listener;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.BukkitConfigurationManager;
import com.sk89q.worldguard.bukkit.BukkitWorldConfiguration;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.bukkit.cause.Cause;
import com.sk89q.worldguard.bukkit.listener.AbstractListener;
import com.sk89q.worldguard.bukkit.listener.RegionProtectionListener;
import com.sk89q.worldguard.bukkit.util.Entities;
import com.sk89q.worldguard.bukkit.util.InteropUtils;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.FailedLoadRegionSet;
import com.sk89q.worldguard.protection.association.RegionAssociable;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import java.util.Set;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.AbstractWindCharge;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Enderman;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Pig;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.entity.Tameable;
import org.bukkit.entity.WindCharge;
import org.bukkit.entity.Wither;
import org.bukkit.entity.WitherSkull;
import org.bukkit.entity.Wolf;
import org.bukkit.entity.minecart.ExplosiveMinecart;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreeperPowerEvent;
import org.bukkit.event.entity.EntityBreakDoorEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityTransformEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PigZapEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.event.world.PortalCreateEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.projectiles.ProjectileSource;

public class WorldGuardEntityListener
extends AbstractListener {
    public WorldGuardEntityListener(WorldGuardPlugin plugin) {
        super(plugin);
    }

    @EventHandler(priority=EventPriority.HIGH, ignoreCancelled=true)
    public void onEntityInteract(EntityInteractEvent event) {
        Block block = event.getBlock();
        BukkitWorldConfiguration wcfg = WorldGuardEntityListener.getWorldConfig(block.getWorld());
        if (block.getType() == Material.FARMLAND && wcfg.disableCreatureCropTrampling) {
            event.setCancelled(true);
            return;
        }
        if (block.getType() == Material.TURTLE_EGG && wcfg.disableCreatureTurtleEggTrampling) {
            event.setCancelled(true);
            return;
        }
        if (block.getType() == Material.SNIFFER_EGG && wcfg.disableCreatureSnifferEggTrampling) {
            event.setCancelled(true);
            return;
        }
    }

    @EventHandler(priority=EventPriority.HIGH)
    public void onEntityDeath(EntityDeathEvent event) {
        BukkitWorldConfiguration wcfg = WorldGuardEntityListener.getWorldConfig(event.getEntity().getWorld());
        if (event instanceof PlayerDeathEvent && wcfg.disableDeathMessages) {
            ((PlayerDeathEvent)event).setDeathMessage("");
        }
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    private void onEntityDamageByBlock(EntityDamageByBlockEvent event) {
        Entity defender = event.getEntity();
        EntityDamageEvent.DamageCause type = event.getCause();
        BukkitWorldConfiguration wcfg = WorldGuardEntityListener.getWorldConfig(defender.getWorld());
        if (defender instanceof Wolf && ((Wolf)defender).isTamed()) {
            if (!wcfg.antiWolfDumbness || type == EntityDamageEvent.DamageCause.VOID) return;
            event.setCancelled(true);
            return;
        }
        if (defender instanceof Player) {
            Player player = (Player)defender;
            if (!Entities.isNPC(defender)) {
                LocalPlayer localPlayer = WorldGuardPlugin.inst().wrapPlayer(player);
                if (wcfg.disableLavaDamage && type == EntityDamageEvent.DamageCause.LAVA) {
                    event.setCancelled(true);
                    player.setFireTicks(0);
                    return;
                }
                if (wcfg.disableContactDamage && type == EntityDamageEvent.DamageCause.CONTACT) {
                    event.setCancelled(true);
                    return;
                }
                if (wcfg.teleportOnVoid && type == EntityDamageEvent.DamageCause.VOID) {
                    localPlayer.findFreePosition();
                    if (wcfg.safeFallOnVoid) {
                        localPlayer.resetFallDistance();
                    }
                    event.setCancelled(true);
                    return;
                }
                if (wcfg.disableVoidDamage && type == EntityDamageEvent.DamageCause.VOID) {
                    event.setCancelled(true);
                    return;
                }
                if (type != EntityDamageEvent.DamageCause.BLOCK_EXPLOSION || !wcfg.disableExplosionDamage && !wcfg.blockOtherExplosions && (!wcfg.explosionFlagCancellation || StateFlag.test(WorldGuard.getInstance().getPlatform().getRegionContainer().createQuery().queryState(localPlayer.getLocation(), (RegionAssociable)null, Flags.OTHER_EXPLOSION)))) return;
                event.setCancelled(true);
                return;
            }
        }
        if (type != EntityDamageEvent.DamageCause.BLOCK_EXPLOSION || !wcfg.blockOtherExplosions && (!wcfg.explosionFlagCancellation && !Entities.isConsideredBuildingIfUsed(defender) || StateFlag.test(WorldGuard.getInstance().getPlatform().getRegionContainer().createQuery().queryState(BukkitAdapter.adapt((Location)defender.getLocation()), (RegionAssociable)null, Flags.OTHER_EXPLOSION)))) return;
        event.setCancelled(true);
    }

    private void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Projectile) {
            this.onEntityDamageByProjectile(event);
            return;
        }
        Entity attacker = event.getDamager();
        Entity defender = event.getEntity();
        BukkitWorldConfiguration wcfg = WorldGuardEntityListener.getWorldConfig(defender.getWorld());
        if (defender instanceof ItemFrame) {
            if (this.checkItemFrameProtection(attacker, (ItemFrame)defender)) {
                event.setCancelled(true);
                return;
            }
        } else if (defender instanceof ArmorStand && !(attacker instanceof Player) && wcfg.blockEntityArmorStandDestroy) {
            event.setCancelled(true);
            return;
        }
        if (attacker instanceof EnderCrystal && wcfg.useRegions && wcfg.explosionFlagCancellation && !WorldGuard.getInstance().getPlatform().getRegionContainer().createQuery().getApplicableRegions(BukkitAdapter.adapt((Location)defender.getLocation())).testState(null, Flags.OTHER_EXPLOSION)) {
            event.setCancelled(true);
            return;
        }
        if (defender instanceof Player) {
            Player player = (Player)defender;
            if (!Entities.isNPC(defender)) {
                LocalPlayer localPlayer = WorldGuardEntityListener.getPlugin().wrapPlayer(player);
                if (wcfg.disableLightningDamage && event.getCause() == EntityDamageEvent.DamageCause.LIGHTNING) {
                    event.setCancelled(true);
                    return;
                }
                if (wcfg.disableExplosionDamage) {
                    switch (event.getCause()) {
                        case BLOCK_EXPLOSION: 
                        case ENTITY_EXPLOSION: {
                            event.setCancelled(true);
                            return;
                        }
                    }
                }
                if (attacker != null) {
                    if ((attacker instanceof TNTPrimed || attacker instanceof ExplosiveMinecart) && wcfg.blockTNTExplosions) {
                        event.setCancelled(true);
                        return;
                    }
                    if (attacker instanceof LivingEntity && !(attacker instanceof Player)) {
                        ApplicableRegionSet set;
                        if (attacker instanceof Creeper && wcfg.blockCreeperExplosions) {
                            event.setCancelled(true);
                            return;
                        }
                        if (wcfg.disableMobDamage) {
                            event.setCancelled(true);
                            return;
                        }
                        if (wcfg.useRegions && !(set = WorldGuard.getInstance().getPlatform().getRegionContainer().createQuery().getApplicableRegions(localPlayer.getLocation())).testState(localPlayer, Flags.MOB_DAMAGE) && !(attacker instanceof Tameable)) {
                            event.setCancelled(true);
                            return;
                        }
                    }
                }
            }
        }
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    private void onEntityDamageByProjectile(EntityDamageByEntityEvent event) {
        Entity defender = event.getEntity();
        ProjectileSource source = ((Projectile)event.getDamager()).getShooter();
        if (!(source instanceof LivingEntity)) {
            return;
        }
        LivingEntity attacker = (LivingEntity)source;
        BukkitWorldConfiguration wcfg = WorldGuardEntityListener.getWorldConfig(defender.getWorld());
        if (defender instanceof Player) {
            Player player = (Player)defender;
            if (!Entities.isNPC(defender)) {
                RegionQuery query;
                LocalPlayer localPlayer = WorldGuardEntityListener.getPlugin().wrapPlayer(player);
                if (attacker instanceof Player) return;
                if (wcfg.disableMobDamage) {
                    event.setCancelled(true);
                    return;
                }
                if (wcfg.useRegions && !WorldGuard.getInstance().getPlatform().getRegionContainer().createQuery().getApplicableRegions(localPlayer.getLocation()).testState(localPlayer, Flags.MOB_DAMAGE)) {
                    event.setCancelled(true);
                    return;
                }
                Entity entity = event.getDamager();
                if (!(entity instanceof Fireball)) return;
                Fireball fireball = (Fireball)entity;
                if (fireball instanceof WitherSkull) {
                    if (wcfg.blockWitherSkullExplosions) {
                        event.setCancelled(true);
                        return;
                    }
                } else if (fireball instanceof AbstractWindCharge) {
                    if (wcfg.blockWindChargeExplosions) {
                        event.setCancelled(true);
                        return;
                    }
                } else if (wcfg.blockFireballExplosions) {
                    event.setCancelled(true);
                    return;
                }
                if (!wcfg.useRegions || (query = WorldGuard.getInstance().getPlatform().getRegionContainer().createQuery()).testState(localPlayer.getLocation(), localPlayer, Entities.getExplosionFlag(event.getDamager())) || !wcfg.explosionFlagCancellation) return;
                event.setCancelled(true);
                return;
            }
        }
        if (defender instanceof ItemFrame) {
            if (!this.checkItemFrameProtection((Entity)attacker, (ItemFrame)defender)) return;
            event.setCancelled(true);
            return;
        }
        if (!(defender instanceof ArmorStand) || !Entities.isNonPlayerCreature((Entity)attacker) || !wcfg.blockEntityArmorStandDestroy) return;
        event.setCancelled(true);
    }

    @EventHandler(priority=EventPriority.HIGH, ignoreCancelled=true)
    public void onEntityDamage(EntityDamageEvent event) {
        if (event instanceof EntityDamageByEntityEvent) {
            this.onEntityDamageByEntity((EntityDamageByEntityEvent)event);
            return;
        }
        if (event instanceof EntityDamageByBlockEvent) {
            this.onEntityDamageByBlock((EntityDamageByBlockEvent)event);
            return;
        }
        Entity defender = event.getEntity();
        EntityDamageEvent.DamageCause type = event.getCause();
        BukkitWorldConfiguration wcfg = WorldGuardEntityListener.getWorldConfig(defender.getWorld());
        if (defender instanceof Wolf && ((Wolf)defender).isTamed()) {
            if (wcfg.antiWolfDumbness) {
                event.setCancelled(true);
                return;
            }
        } else if (defender instanceof Player) {
            Player player = (Player)defender;
            if (!Entities.isNPC(defender)) {
                LocalPlayer localPlayer = WorldGuardPlugin.inst().wrapPlayer(player);
                if (type == EntityDamageEvent.DamageCause.WITHER) {
                    ApplicableRegionSet set;
                    if (wcfg.disableMobDamage) {
                        event.setCancelled(true);
                        return;
                    }
                    if (wcfg.useRegions && !(set = WorldGuard.getInstance().getPlatform().getRegionContainer().createQuery().getApplicableRegions(localPlayer.getLocation())).testState(WorldGuardEntityListener.getPlugin().wrapPlayer(player), Flags.MOB_DAMAGE)) {
                        event.setCancelled(true);
                        return;
                    }
                }
                if (type == EntityDamageEvent.DamageCause.DROWNING && WorldGuardEntityListener.getConfig().hasAmphibiousMode(localPlayer)) {
                    player.setRemainingAir(player.getMaximumAir());
                    event.setCancelled(true);
                    return;
                }
                ItemStack helmet = player.getInventory().getHelmet();
                if (type == EntityDamageEvent.DamageCause.DROWNING && wcfg.pumpkinScuba && helmet != null && (helmet.getType() == Material.CARVED_PUMPKIN || helmet.getType() == Material.JACK_O_LANTERN)) {
                    player.setRemainingAir(player.getMaximumAir());
                    event.setCancelled(true);
                    return;
                }
                if (wcfg.disableFallDamage && type == EntityDamageEvent.DamageCause.FALL) {
                    event.setCancelled(true);
                    return;
                }
                if (wcfg.disableFireDamage && (type == EntityDamageEvent.DamageCause.FIRE || type == EntityDamageEvent.DamageCause.FIRE_TICK)) {
                    event.setCancelled(true);
                    return;
                }
                if (wcfg.disableDrowningDamage && type == EntityDamageEvent.DamageCause.DROWNING) {
                    player.setRemainingAir(player.getMaximumAir());
                    event.setCancelled(true);
                    return;
                }
                if (wcfg.teleportOnSuffocation && type == EntityDamageEvent.DamageCause.SUFFOCATION) {
                    localPlayer.findFreePosition();
                    event.setCancelled(true);
                    return;
                }
                if (wcfg.disableSuffocationDamage && type == EntityDamageEvent.DamageCause.SUFFOCATION) {
                    event.setCancelled(true);
                    return;
                }
            }
        }
    }

    @EventHandler(priority=EventPriority.HIGH, ignoreCancelled=true)
    public void onEntityExplode(EntityExplodeEvent event) {
        BukkitConfigurationManager cfg = WorldGuardEntityListener.getConfig();
        Entity ent = event.getEntity();
        if (cfg.activityHaltToggle) {
            ent.remove();
            event.setCancelled(true);
            return;
        }
        BukkitWorldConfiguration wcfg = WorldGuardEntityListener.getWorldConfig(event.getLocation().getWorld());
        if (ent instanceof Creeper) {
            if (wcfg.blockCreeperExplosions) {
                event.setCancelled(true);
                return;
            }
            if (wcfg.blockCreeperBlockDamage) {
                event.blockList().clear();
                return;
            }
        } else if (ent instanceof EnderDragon) {
            if (wcfg.blockEnderDragonBlockDamage) {
                event.blockList().clear();
                return;
            }
        } else if (ent instanceof TNTPrimed || ent instanceof ExplosiveMinecart) {
            if (wcfg.blockTNTExplosions) {
                event.setCancelled(true);
                return;
            }
            if (wcfg.blockTNTBlockDamage) {
                event.blockList().clear();
                return;
            }
        } else if (ent instanceof Fireball) {
            if (ent instanceof WitherSkull) {
                if (wcfg.blockWitherSkullExplosions) {
                    event.setCancelled(true);
                    return;
                }
                if (wcfg.blockWitherSkullBlockDamage) {
                    event.blockList().clear();
                    return;
                }
            } else if (ent instanceof AbstractWindCharge) {
                if (wcfg.blockWindChargeExplosions) {
                    event.setCancelled(true);
                    return;
                }
            } else {
                if (wcfg.blockFireballExplosions) {
                    event.setCancelled(true);
                    return;
                }
                if (wcfg.blockFireballBlockDamage) {
                    event.blockList().clear();
                    return;
                }
            }
            if (wcfg.useRegions && !(ent instanceof WindCharge)) {
                for (Block block : event.blockList()) {
                    if (WorldGuard.getInstance().getPlatform().getRegionContainer().createQuery().testState(BukkitAdapter.adapt((Location)block.getLocation()), null, Entities.getExplosionFlag(ent))) continue;
                    event.blockList().clear();
                    if (wcfg.explosionFlagCancellation) {
                        event.setCancelled(true);
                    }
                    return;
                }
            }
        } else if (ent instanceof Wither) {
            if (wcfg.blockWitherExplosions) {
                event.setCancelled(true);
                return;
            }
            if (wcfg.blockWitherBlockDamage) {
                event.blockList().clear();
                return;
            }
            if (wcfg.useRegions) {
                for (Block block : event.blockList()) {
                    if (WorldGuard.getInstance().getPlatform().getRegionContainer().createQuery().testState(BukkitAdapter.adapt((Location)block.getLocation()), null, Flags.WITHER_DAMAGE)) continue;
                    event.blockList().clear();
                    event.setCancelled(true);
                    return;
                }
            }
        } else {
            if (wcfg.blockOtherExplosions) {
                event.setCancelled(true);
                return;
            }
            if (wcfg.useRegions) {
                for (Block block : event.blockList()) {
                    if (WorldGuard.getInstance().getPlatform().getRegionContainer().createQuery().testState(BukkitAdapter.adapt((Location)block.getLocation()), null, Flags.OTHER_EXPLOSION)) continue;
                    event.blockList().clear();
                    if (wcfg.explosionFlagCancellation) {
                        event.setCancelled(true);
                    }
                    return;
                }
            }
        }
        if (wcfg.signChestProtection) {
            for (Block block : event.blockList()) {
                if (!wcfg.isChestProtected(BukkitAdapter.adapt((Location)block.getLocation()))) continue;
                event.blockList().clear();
                return;
            }
        }
    }

    @EventHandler(priority=EventPriority.HIGH, ignoreCancelled=true)
    public void onExplosionPrime(ExplosionPrimeEvent event) {
        BukkitConfigurationManager cfg = WorldGuardEntityListener.getConfig();
        Entity ent = event.getEntity();
        if (cfg.activityHaltToggle) {
            ent.remove();
            event.setCancelled(true);
            return;
        }
        BukkitWorldConfiguration wcfg = WorldGuardEntityListener.getWorldConfig(ent.getWorld());
        if (event.getEntityType() == EntityType.WITHER) {
            if (wcfg.blockWitherExplosions) {
                event.setCancelled(true);
                return;
            }
        } else if (event.getEntityType() == EntityType.WITHER_SKULL) {
            if (wcfg.blockWitherSkullExplosions) {
                event.setCancelled(true);
                return;
            }
        } else if (event.getEntityType() == EntityType.FIREBALL) {
            if (wcfg.blockFireballExplosions) {
                event.setCancelled(true);
                return;
            }
        } else if (event.getEntityType() == EntityType.CREEPER) {
            if (wcfg.blockCreeperExplosions) {
                event.setCancelled(true);
                return;
            }
        } else if (event.getEntityType() == EntityType.TNT || event.getEntityType() == EntityType.TNT_MINECART) {
            if (wcfg.blockTNTExplosions) {
                event.setCancelled(true);
                return;
            }
        } else if (event instanceof AbstractWindCharge && wcfg.blockWindChargeExplosions) {
            event.setCancelled(true);
            return;
        }
    }

    @EventHandler(priority=EventPriority.HIGH, ignoreCancelled=true)
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        BukkitConfigurationManager cfg = WorldGuardEntityListener.getConfig();
        if (cfg.activityHaltToggle) {
            event.setCancelled(true);
            return;
        }
        BukkitWorldConfiguration wcfg = WorldGuardEntityListener.getWorldConfig(event.getEntity().getWorld());
        if (!wcfg.blockPluginSpawning && Entities.isPluginSpawning(event.getSpawnReason())) {
            return;
        }
        if (Entities.isConsideredBuildingIfUsed((Entity)event.getEntity())) {
            return;
        }
        if (wcfg.allowTamedSpawns && event.getEntity() instanceof Tameable && ((Tameable)event.getEntity()).isTamed()) {
            return;
        }
        EntityType entityType = event.getEntityType();
        com.sk89q.worldedit.world.entity.EntityType weEntityType = BukkitAdapter.adapt((EntityType)entityType);
        if (weEntityType != null && wcfg.blockCreatureSpawn.contains(weEntityType)) {
            event.setCancelled(true);
            return;
        }
        Location eventLoc = event.getLocation();
        if (wcfg.useRegions && cfg.useRegionsCreatureSpawnEvent) {
            ApplicableRegionSet set = WorldGuard.getInstance().getPlatform().getRegionContainer().createQuery().getApplicableRegions(BukkitAdapter.adapt((Location)eventLoc));
            if (!set.testState(null, Flags.MOB_SPAWNING)) {
                event.setCancelled(true);
                return;
            }
            Set entityTypes = (Set)set.queryValue(null, Flags.DENY_SPAWN);
            if (entityTypes != null && weEntityType != null && entityTypes.contains(weEntityType)) {
                event.setCancelled(true);
                return;
            }
        }
        if (wcfg.blockGroundSlimes && entityType == EntityType.SLIME && eventLoc.getY() >= 60.0 && event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.NATURAL) {
            event.setCancelled(true);
            return;
        }
    }

    @EventHandler(priority=EventPriority.HIGH, ignoreCancelled=true)
    public void onCreatePortal(PortalCreateEvent event) {
        BukkitWorldConfiguration wcfg = WorldGuardEntityListener.getWorldConfig(event.getWorld());
        if (wcfg.useRegions && wcfg.regionNetherPortalProtection && event.getReason() == PortalCreateEvent.CreateReason.NETHER_PAIR && !event.getBlocks().isEmpty()) {
            ApplicableRegionSet regions;
            RegionManager regionManager;
            World world = BukkitAdapter.adapt((org.bukkit.World)event.getWorld());
            Cause cause = Cause.create(event.getEntity());
            LocalPlayer localPlayer = null;
            Object object = cause.getRootCause();
            if (object instanceof Player) {
                Player player = (Player)object;
                if (wcfg.fakePlayerBuildOverride && InteropUtils.isFakePlayer(player)) {
                    return;
                }
                localPlayer = WorldGuardEntityListener.getPlugin().wrapPlayer(player);
                if (WorldGuard.getInstance().getPlatform().getSessionManager().hasBypass(localPlayer, world)) {
                    return;
                }
            }
            if ((regionManager = WorldGuard.getInstance().getPlatform().getRegionContainer().get(world)) == null) {
                regions = FailedLoadRegionSet.getInstance();
            } else {
                BlockVector3 min = null;
                BlockVector3 max = null;
                for (BlockState block : event.getBlocks()) {
                    BlockVector3 loc = BlockVector3.at((int)block.getX(), (int)block.getY(), (int)block.getZ());
                    min = min == null ? loc : loc.getMinimum(min);
                    max = max == null ? loc : loc.getMaximum(max);
                }
                ProtectedCuboidRegion target = new ProtectedCuboidRegion("__portal_check", true, min, max);
                regions = regionManager.getApplicableRegions(target);
            }
            RegionAssociable associable = this.createRegionAssociable(cause);
            StateFlag.State buildState = StateFlag.denyToNone(regions.queryState(associable, Flags.BUILD));
            if (!StateFlag.test(buildState, regions.queryState(associable, Flags.BLOCK_BREAK)) || !StateFlag.test(buildState, regions.queryState(associable, Flags.BLOCK_PLACE))) {
                if (localPlayer != null && !cause.isIndirect()) {
                    String message = regions.queryValue(localPlayer, Flags.DENY_MESSAGE);
                    RegionProtectionListener.formatAndSendDenyMessage("create portals", localPlayer, message);
                }
                event.setCancelled(true);
            }
        }
        if (event.getEntity() instanceof EnderDragon && wcfg.blockEnderDragonPortalCreation) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority=EventPriority.HIGH, ignoreCancelled=true)
    public void onEntityTransform(EntityTransformEvent event) {
        Entity entity = event.getEntity();
        BukkitWorldConfiguration wcfg = WorldGuardEntityListener.getWorldConfig(entity.getWorld());
        EntityType type = entity.getType();
        if (wcfg.disableVillagerZap && type == EntityType.VILLAGER && event.getTransformReason() == EntityTransformEvent.TransformReason.LIGHTNING) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority=EventPriority.HIGH, ignoreCancelled=true)
    public void onPigZap(PigZapEvent event) {
        Pig entity = event.getEntity();
        BukkitWorldConfiguration wcfg = WorldGuardEntityListener.getWorldConfig(entity.getWorld());
        if (wcfg.disablePigZap) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority=EventPriority.HIGH, ignoreCancelled=true)
    public void onCreeperPower(CreeperPowerEvent event) {
        Creeper entity = event.getEntity();
        BukkitWorldConfiguration wcfg = WorldGuardEntityListener.getWorldConfig(entity.getWorld());
        if (wcfg.disableCreeperPower) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority=EventPriority.HIGH, ignoreCancelled=true)
    public void onEntityRegainHealth(EntityRegainHealthEvent event) {
        EntityRegainHealthEvent.RegainReason regainReason = event.getRegainReason();
        if (regainReason != EntityRegainHealthEvent.RegainReason.REGEN && regainReason != EntityRegainHealthEvent.RegainReason.SATIATED) {
            return;
        }
        Entity ent = event.getEntity();
        BukkitWorldConfiguration wcfg = WorldGuardEntityListener.getWorldConfig(ent.getWorld());
        if (wcfg.disableHealthRegain) {
            event.setCancelled(true);
            return;
        }
        if (wcfg.useRegions && ent instanceof Player) {
            Player player = (Player)ent;
            if (!Entities.isNPC(ent) && !WorldGuard.getInstance().getPlatform().getRegionContainer().createQuery().testState(BukkitAdapter.adapt((Location)ent.getLocation()), WorldGuardPlugin.inst().wrapPlayer(player), Flags.HEALTH_REGEN)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(ignoreCancelled=true)
    public void onFoodChange(FoodLevelChangeEvent event) {
        if (event.getItem() != null) {
            return;
        }
        HumanEntity ent = event.getEntity();
        if (Entities.isNPC((Entity)ent)) {
            return;
        }
        if (!(ent instanceof Player)) {
            return;
        }
        Player bukkitPlayer = (Player)ent;
        if (event.getFoodLevel() > ent.getFoodLevel()) {
            return;
        }
        LocalPlayer player = WorldGuardPlugin.inst().wrapPlayer(bukkitPlayer);
        BukkitWorldConfiguration wcfg = WorldGuardEntityListener.getWorldConfig(ent.getWorld());
        if (wcfg.useRegions && !WorldGuard.getInstance().getPlatform().getRegionContainer().createQuery().testState(player.getLocation(), player, Flags.HUNGER_DRAIN)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority=EventPriority.HIGH, ignoreCancelled=true)
    public void onEntityChangeBlock(EntityChangeBlockEvent event) {
        Entity ent = event.getEntity();
        BukkitWorldConfiguration wcfg = WorldGuardEntityListener.getWorldConfig(ent.getWorld());
        if (ent instanceof FallingBlock) {
            Material id = event.getBlock().getType();
            if (id == Material.GRAVEL && wcfg.noPhysicsGravel) {
                event.setCancelled(true);
                return;
            }
            if ((id == Material.SAND || id == Material.RED_SAND) && wcfg.noPhysicsSand) {
                event.setCancelled(true);
                return;
            }
        } else if (ent instanceof Enderman) {
            if (wcfg.disableEndermanGriefing) {
                event.setCancelled(true);
                return;
            }
        } else if (ent.getType() == EntityType.WITHER) {
            if (wcfg.blockWitherBlockDamage || wcfg.blockWitherExplosions) {
                event.setCancelled(true);
                return;
            }
            if (wcfg.useRegions) {
                Location location = event.getBlock().getLocation();
                if (!StateFlag.test(WorldGuard.getInstance().getPlatform().getRegionContainer().createQuery().queryState(BukkitAdapter.adapt((Location)location), (RegionAssociable)null, Flags.WITHER_DAMAGE))) {
                    event.setCancelled(true);
                    return;
                }
            }
        } else if (event instanceof EntityBreakDoorEvent && wcfg.blockZombieDoorDestruction) {
            event.setCancelled(true);
            return;
        }
    }

    @EventHandler(priority=EventPriority.HIGH, ignoreCancelled=true)
    public void onVehicleEnter(VehicleEnterEvent event) {
        BukkitWorldConfiguration wcfg = WorldGuardEntityListener.getWorldConfig(event.getEntered().getWorld());
        if (wcfg.blockEntityVehicleEntry && !(event.getEntered() instanceof Player)) {
            event.setCancelled(true);
        }
    }

    private boolean checkItemFrameProtection(Entity attacker, ItemFrame defender) {
        org.bukkit.World world = defender.getWorld();
        BukkitWorldConfiguration wcfg = WorldGuardEntityListener.getWorldConfig(world);
        if (wcfg.useRegions && !(attacker instanceof Player) && !StateFlag.test(WorldGuard.getInstance().getPlatform().getRegionContainer().createQuery().queryState(BukkitAdapter.adapt((Location)defender.getLocation()), (RegionAssociable)null, Flags.ENTITY_ITEM_FRAME_DESTROY))) {
            return true;
        }
        return wcfg.blockEntityItemFrameDestroy && !(attacker instanceof Player);
    }
}

