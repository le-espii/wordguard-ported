/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.destroystokyo.paper.event.entity.EntityKnockbackByEntityEvent
 *  com.destroystokyo.paper.event.entity.EntityZapEvent
 *  io.papermc.paper.event.player.PlayerOpenSignEvent
 *  io.papermc.paper.event.player.PlayerOpenSignEvent$Cause
 *  javax.annotation.Nullable
 *  org.bukkit.Bukkit
 *  org.bukkit.Effect
 *  org.bukkit.ExplosionResult
 *  org.bukkit.GameMode
 *  org.bukkit.Keyed
 *  org.bukkit.Location
 *  org.bukkit.Material
 *  org.bukkit.Tag
 *  org.bukkit.World
 *  org.bukkit.block.Block
 *  org.bukkit.block.BlockFace
 *  org.bukkit.block.BlockState
 *  org.bukkit.block.Chest
 *  org.bukkit.block.DoubleChest
 *  org.bukkit.block.Hopper
 *  org.bukkit.block.PistonMoveReaction
 *  org.bukkit.block.data.BlockData
 *  org.bukkit.block.data.Waterlogged
 *  org.bukkit.block.data.type.Dispenser
 *  org.bukkit.entity.AreaEffectCloud
 *  org.bukkit.entity.BreezeWindCharge
 *  org.bukkit.entity.Creeper
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.EntityType
 *  org.bukkit.entity.FallingBlock
 *  org.bukkit.entity.Firework
 *  org.bukkit.entity.Item
 *  org.bukkit.entity.ItemFrame
 *  org.bukkit.entity.LivingEntity
 *  org.bukkit.entity.Mob
 *  org.bukkit.entity.Painting
 *  org.bukkit.entity.Player
 *  org.bukkit.entity.Tameable
 *  org.bukkit.entity.ThrownPotion
 *  org.bukkit.entity.WindCharge
 *  org.bukkit.entity.minecart.HopperMinecart
 *  org.bukkit.event.Cancellable
 *  org.bukkit.event.Event
 *  org.bukkit.event.Event$Result
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.Listener
 *  org.bukkit.event.block.Action
 *  org.bukkit.event.block.BlockBreakEvent
 *  org.bukkit.event.block.BlockBurnEvent
 *  org.bukkit.event.block.BlockDamageEvent
 *  org.bukkit.event.block.BlockDispenseEvent
 *  org.bukkit.event.block.BlockExpEvent
 *  org.bukkit.event.block.BlockExplodeEvent
 *  org.bukkit.event.block.BlockFertilizeEvent
 *  org.bukkit.event.block.BlockFromToEvent
 *  org.bukkit.event.block.BlockIgniteEvent
 *  org.bukkit.event.block.BlockMultiPlaceEvent
 *  org.bukkit.event.block.BlockPistonExtendEvent
 *  org.bukkit.event.block.BlockPistonRetractEvent
 *  org.bukkit.event.block.BlockPlaceEvent
 *  org.bukkit.event.block.CauldronLevelChangeEvent
 *  org.bukkit.event.block.EntityBlockFormEvent
 *  org.bukkit.event.block.SignChangeEvent
 *  org.bukkit.event.entity.AreaEffectCloudApplyEvent
 *  org.bukkit.event.entity.CreatureSpawnEvent
 *  org.bukkit.event.entity.EntityChangeBlockEvent
 *  org.bukkit.event.entity.EntityCombustByBlockEvent
 *  org.bukkit.event.entity.EntityCombustByEntityEvent
 *  org.bukkit.event.entity.EntityCombustEvent
 *  org.bukkit.event.entity.EntityDamageByBlockEvent
 *  org.bukkit.event.entity.EntityDamageByEntityEvent
 *  org.bukkit.event.entity.EntityDamageEvent
 *  org.bukkit.event.entity.EntityDeathEvent
 *  org.bukkit.event.entity.EntityEvent
 *  org.bukkit.event.entity.EntityExplodeEvent
 *  org.bukkit.event.entity.EntityInteractEvent
 *  org.bukkit.event.entity.EntityKnockbackByEntityEvent
 *  org.bukkit.event.entity.EntityPickupItemEvent
 *  org.bukkit.event.entity.EntityTameEvent
 *  org.bukkit.event.entity.EntityUnleashEvent
 *  org.bukkit.event.entity.ExpBottleEvent
 *  org.bukkit.event.entity.LingeringPotionSplashEvent
 *  org.bukkit.event.entity.PotionSplashEvent
 *  org.bukkit.event.hanging.HangingBreakByEntityEvent
 *  org.bukkit.event.hanging.HangingBreakEvent
 *  org.bukkit.event.hanging.HangingBreakEvent$RemoveCause
 *  org.bukkit.event.hanging.HangingPlaceEvent
 *  org.bukkit.event.inventory.InventoryMoveItemEvent
 *  org.bukkit.event.inventory.InventoryOpenEvent
 *  org.bukkit.event.player.PlayerBedEnterEvent
 *  org.bukkit.event.player.PlayerBucketEmptyEvent
 *  org.bukkit.event.player.PlayerBucketFillEvent
 *  org.bukkit.event.player.PlayerDropItemEvent
 *  org.bukkit.event.player.PlayerFishEvent
 *  org.bukkit.event.player.PlayerFishEvent$State
 *  org.bukkit.event.player.PlayerInteractAtEntityEvent
 *  org.bukkit.event.player.PlayerInteractEntityEvent
 *  org.bukkit.event.player.PlayerInteractEvent
 *  org.bukkit.event.player.PlayerItemConsumeEvent
 *  org.bukkit.event.player.PlayerPickupItemEvent
 *  org.bukkit.event.player.PlayerShearEntityEvent
 *  org.bukkit.event.player.PlayerTakeLecternBookEvent
 *  org.bukkit.event.player.PlayerUnleashEntityEvent
 *  org.bukkit.event.vehicle.VehicleDamageEvent
 *  org.bukkit.event.vehicle.VehicleDestroyEvent
 *  org.bukkit.event.world.StructureGrowEvent
 *  org.bukkit.inventory.EquipmentSlot
 *  org.bukkit.inventory.InventoryHolder
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.metadata.Metadatable
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.plugin.PluginManager
 *  org.bukkit.potion.PotionEffect
 *  org.bukkit.projectiles.ProjectileSource
 *  org.bukkit.util.Vector
 */
package com.sk89q.worldguard.bukkit.listener;

import com.destroystokyo.paper.event.entity.EntityKnockbackByEntityEvent;
import com.destroystokyo.paper.event.entity.EntityZapEvent;
import com.sk89q.worldguard.bukkit.BukkitWorldConfiguration;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.bukkit.cause.Cause;
import com.sk89q.worldguard.bukkit.event.DelegateEvent;
import com.sk89q.worldguard.bukkit.event.block.BreakBlockEvent;
import com.sk89q.worldguard.bukkit.event.block.PlaceBlockEvent;
import com.sk89q.worldguard.bukkit.event.block.UseBlockEvent;
import com.sk89q.worldguard.bukkit.event.entity.AbstractEntityEvent;
import com.sk89q.worldguard.bukkit.event.entity.DamageEntityEvent;
import com.sk89q.worldguard.bukkit.event.entity.DestroyEntityEvent;
import com.sk89q.worldguard.bukkit.event.entity.SpawnEntityEvent;
import com.sk89q.worldguard.bukkit.event.entity.UseEntityEvent;
import com.sk89q.worldguard.bukkit.event.inventory.UseItemEvent;
import com.sk89q.worldguard.bukkit.listener.AbstractListener;
import com.sk89q.worldguard.bukkit.listener.debounce.BlockPistonExtendKey;
import com.sk89q.worldguard.bukkit.listener.debounce.BlockPistonRetractKey;
import com.sk89q.worldguard.bukkit.listener.debounce.EventDebounce;
import com.sk89q.worldguard.bukkit.listener.debounce.legacy.AbstractEventDebounce;
import com.sk89q.worldguard.bukkit.listener.debounce.legacy.BlockEntityEventDebounce;
import com.sk89q.worldguard.bukkit.listener.debounce.legacy.EntityEntityEventDebounce;
import com.sk89q.worldguard.bukkit.listener.debounce.legacy.InventoryMoveItemEventDebounce;
import com.sk89q.worldguard.bukkit.paperlib.PaperLib;
import com.sk89q.worldguard.bukkit.util.Blocks;
import com.sk89q.worldguard.bukkit.util.Entities;
import com.sk89q.worldguard.bukkit.util.Events;
import com.sk89q.worldguard.bukkit.util.Materials;
import com.sk89q.worldguard.protection.flags.Flags;
import io.papermc.paper.event.player.PlayerOpenSignEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.ExplosionResult;
import org.bukkit.GameMode;
import org.bukkit.Keyed;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.block.Hopper;
import org.bukkit.block.PistonMoveReaction;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Waterlogged;
import org.bukkit.block.data.type.Dispenser;
import org.bukkit.entity.AreaEffectCloud;
import org.bukkit.entity.BreezeWindCharge;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Item;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Painting;
import org.bukkit.entity.Player;
import org.bukkit.entity.Tameable;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.entity.WindCharge;
import org.bukkit.entity.minecart.HopperMinecart;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.block.BlockExpEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockFertilizeEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockMultiPlaceEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.CauldronLevelChangeEvent;
import org.bukkit.event.block.EntityBlockFormEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.AreaEffectCloudApplyEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityCombustByBlockEvent;
import org.bukkit.event.entity.EntityCombustByEntityEvent;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.EntityTameEvent;
import org.bukkit.event.entity.EntityUnleashEvent;
import org.bukkit.event.entity.ExpBottleEvent;
import org.bukkit.event.entity.LingeringPotionSplashEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerShearEntityEvent;
import org.bukkit.event.player.PlayerTakeLecternBookEvent;
import org.bukkit.event.player.PlayerUnleashEntityEvent;
import org.bukkit.event.vehicle.VehicleDamageEvent;
import org.bukkit.event.vehicle.VehicleDestroyEvent;
import org.bukkit.event.world.StructureGrowEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.Metadatable;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.potion.PotionEffect;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.util.Vector;

public class EventAbstractionListener
extends AbstractListener {
    private final BlockEntityEventDebounce interactDebounce = new BlockEntityEventDebounce(10000);
    private final EntityEntityEventDebounce pickupDebounce = new EntityEntityEventDebounce(10000);
    private final BlockEntityEventDebounce entityBreakBlockDebounce = new BlockEntityEventDebounce(10000);
    private final InventoryMoveItemEventDebounce moveItemDebounce = new InventoryMoveItemEventDebounce(30000);
    private final EventDebounce<BlockPistonRetractKey> pistonRetractDebounce = EventDebounce.create(5000);
    private final EventDebounce<BlockPistonExtendKey> pistonExtendDebounce = EventDebounce.create(5000);

    public EventAbstractionListener(WorldGuardPlugin plugin) {
        super(plugin);
    }

    @Override
    public void registerEvents() {
        super.registerEvents();
        PluginManager pm = EventAbstractionListener.getPlugin().getServer().getPluginManager();
        if (PaperLib.isPaper()) {
            pm.registerEvents((Listener)new PaperListener(), (Plugin)EventAbstractionListener.getPlugin());
        } else {
            pm.registerEvents((Listener)new SpigotListener(), (Plugin)EventAbstractionListener.getPlugin());
        }
    }

    @EventHandler(ignoreCancelled=true)
    public void onBlockBreak(BlockBreakEvent event) {
        Events.fireToCancel((Cancellable)event, new BreakBlockEvent((Event)event, Cause.create(event.getPlayer()), event.getBlock()));
        if (event.isCancelled()) {
            EventAbstractionListener.playDenyEffect(event.getPlayer(), event.getBlock().getLocation().add(0.5, 1.0, 0.5));
        }
    }

    @EventHandler(ignoreCancelled=true)
    public void onBlockMultiPlace(BlockMultiPlaceEvent event) {
        List<Block> placed = event.getReplacedBlockStates().stream().map(BlockState::getBlock).collect(Collectors.toList());
        int origAmt = placed.size();
        PlaceBlockEvent delegateEvent = new PlaceBlockEvent((Event)event, Cause.create(event.getPlayer()), event.getBlock().getWorld(), placed, event.getBlockPlaced().getType());
        Events.fireToCancel((Cancellable)event, delegateEvent);
        if (origAmt != placed.size()) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled=true)
    public void onBlockPlace(BlockPlaceEvent event) {
        if (event instanceof BlockMultiPlaceEvent) {
            return;
        }
        BlockState previousState = event.getBlockReplacedState();
        if (previousState.getType() != Material.AIR && previousState.getType() != event.getBlockReplacedState().getType()) {
            Events.fireToCancel((Cancellable)event, new BreakBlockEvent((Event)event, Cause.create(event.getPlayer()), previousState.getLocation(), previousState.getType()));
        }
        ItemStack itemStack = event.getItemInHand();
        if (!event.isCancelled() && itemStack.getType() != Material.AIR) {
            Events.fireToCancel((Cancellable)event, new UseItemEvent((Event)event, Cause.create(event.getPlayer()), event.getPlayer().getWorld(), itemStack));
        }
        if (!event.isCancelled()) {
            Events.fireToCancel((Cancellable)event, new PlaceBlockEvent((Event)event, Cause.create(event.getPlayer()), event.getBlock()));
        }
        if (event.isCancelled()) {
            EventAbstractionListener.playDenyEffect(event.getPlayer(), event.getBlockPlaced().getLocation().add(0.5, 0.5, 0.5));
        }
    }

    @EventHandler(ignoreCancelled=true)
    public void onBlockBurn(BlockBurnEvent event) {
        Block target = event.getBlock();
        Block[] adjacent = new Block[]{target.getRelative(BlockFace.NORTH), target.getRelative(BlockFace.SOUTH), target.getRelative(BlockFace.WEST), target.getRelative(BlockFace.EAST), target.getRelative(BlockFace.UP), target.getRelative(BlockFace.DOWN)};
        int found = 0;
        boolean allowed = false;
        for (Block source : adjacent) {
            if (!Materials.isFire(source.getType())) continue;
            ++found;
            if (Events.fireAndTestCancel(new BreakBlockEvent((Event)event, Cause.create(source), target))) {
                source.setType(Material.AIR);
                continue;
            }
            allowed = true;
        }
        if (found > 0 && !allowed) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled=true)
    public void onStructureGrowEvent(StructureGrowEvent event) {
        int originalCount = event.getBlocks().size();
        Player player = event.getPlayer();
        Events.fireBulkEventToCancel((Cancellable)event, new PlaceBlockEvent((Event)event, Cause.create(player == null ? event.getLocation().getBlock() : player), event.getLocation().getWorld(), event.getBlocks()));
        if (!event.isCancelled() && event.getBlocks().size() != originalCount) {
            event.getLocation().getBlock().setType(Material.AIR);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void handleFallingBlock(EntityChangeBlockEvent event, boolean dropItem) {
        Entity entity = event.getEntity();
        Block block = event.getBlock();
        if (entity instanceof FallingBlock) {
            try {
                if (dropItem) {
                    FallingBlock fallingBlock = (FallingBlock)entity;
                    if (!fallingBlock.getDropItem()) {
                        return;
                    }
                    Material material = fallingBlock.getBlockData().getMaterial();
                    if (!material.isItem()) {
                        return;
                    }
                    ItemStack itemStack = new ItemStack(material, 1);
                    Item item = block.getWorld().dropItem(fallingBlock.getLocation(), itemStack);
                    item.setVelocity(new Vector());
                    if (Events.fireAndTestCancel(new SpawnEntityEvent((Event)event, Cause.create(block, entity), (Entity)item))) {
                        item.remove();
                    }
                }
            }
            finally {
                Cause.untrackParentCause((Metadatable)entity);
            }
        }
    }

    private void setDelegateEventMaterialOptions(DelegateEvent event, Material fromType, Material toType) {
        if (fromType == Material.FARMLAND && toType == Material.DIRT) {
            event.setSilent(true);
            event.getRelevantFlags().add(Flags.TRAMPLE_BLOCKS);
        } else if (Tag.REDSTONE_ORES.isTagged((Keyed)fromType)) {
            event.setSilent(true);
        } else if (fromType == Material.BIG_DRIPLEAF && toType == Material.BIG_DRIPLEAF) {
            event.setSilent(true);
            event.getRelevantFlags().add(Flags.USE_DRIPLEAF);
        }
    }

    @EventHandler(ignoreCancelled=true)
    public void onEntityChangeBlock(EntityChangeBlockEvent event) {
        Block block = event.getBlock();
        Entity entity = event.getEntity();
        Material toType = event.getTo();
        Material fromType = block.getType();
        Cause cause = Cause.create(entity);
        if (toType != Material.AIR && fromType != Material.AIR) {
            BreakBlockEvent breakDelegate = new BreakBlockEvent((Event)event, cause, block);
            this.setDelegateEventMaterialOptions(breakDelegate, fromType, toType);
            boolean denied = Events.fireToCancel((Cancellable)event, breakDelegate);
            if (!denied) {
                PlaceBlockEvent placeDelegate = new PlaceBlockEvent((Event)event, cause, block.getLocation(), toType);
                this.setDelegateEventMaterialOptions(placeDelegate, fromType, toType);
                denied = Events.fireToCancel((Cancellable)event, placeDelegate);
            }
            if (denied && entity instanceof Player) {
                EventAbstractionListener.playDenyEffect((Player)entity, block.getLocation());
            }
            this.handleFallingBlock(event, denied);
        } else if (toType == Material.AIR) {
            if (entity instanceof FallingBlock) {
                Cause.trackParentCause((Metadatable)entity, block);
                Events.fireToCancel((Cancellable)event, new SpawnEntityEvent((Event)event, Cause.create(block), entity));
            } else {
                this.entityBreakBlockDebounce.debounce(block, event.getEntity(), (Cancellable)event, new BreakBlockEvent((Event)event, cause, block));
            }
        } else {
            boolean denied = Events.fireToCancel((Cancellable)event, new PlaceBlockEvent((Event)event, cause, block.getLocation(), toType));
            this.handleFallingBlock(event, denied);
        }
    }

    private static <T extends EntityEvent> void handleKnockback(T event, Entity damager) {
        DamageEntityEvent eventToFire = new DamageEntityEvent((Event)event, Cause.create(damager), event.getEntity());
        if (damager instanceof BreezeWindCharge) {
            eventToFire.getRelevantFlags().add(Flags.BREEZE_WIND_CHARGE);
        } else if (damager instanceof WindCharge) {
            eventToFire.getRelevantFlags().add(Flags.WIND_CHARGE_BURST);
        }
        Events.fireToCancel((Cancellable)event, eventToFire);
    }

    @EventHandler(ignoreCancelled=true)
    public void onEntityExplode(EntityExplodeEvent event) {
        Entity entity = event.getEntity();
        if (event.getExplosionResult() == ExplosionResult.TRIGGER_BLOCK) {
            UseBlockEvent useEvent = new UseBlockEvent((Event)event, Cause.create(entity), event.getLocation().getWorld(), event.blockList(), Material.AIR);
            useEvent.getRelevantFlags().add(Entities.getExplosionFlag(entity));
            useEvent.setSilent(true);
            Events.fireBulkEventToCancel((Cancellable)event, useEvent);
        } else if (event.getExplosionResult() == ExplosionResult.DESTROY || event.getExplosionResult() == ExplosionResult.DESTROY_WITH_DECAY) {
            Events.fireBulkEventToCancel((Cancellable)event, new BreakBlockEvent((Event)event, Cause.create(entity), event.getLocation().getWorld(), event.blockList(), Material.AIR));
        }
        if (entity instanceof Creeper) {
            Cause.untrackParentCause((Metadatable)entity);
        }
    }

    @EventHandler(ignoreCancelled=true)
    public void onBlockPistonRetract(BlockPistonRetractEvent event) {
        EventDebounce.Entry entry;
        if (event.isSticky() && (entry = this.pistonRetractDebounce.getIfNotPresent(new BlockPistonRetractKey(event), (Cancellable)event)) != null) {
            Block piston = event.getBlock();
            Cause cause = Cause.create(piston);
            BlockFace direction = event.getDirection();
            ArrayList<Block> blocks = new ArrayList<Block>(event.getBlocks());
            int originalSize = blocks.size();
            Events.fireBulkEventToCancel((Cancellable)event, new BreakBlockEvent((Event)event, cause, event.getBlock().getWorld(), blocks, Material.AIR));
            if (originalSize != blocks.size()) {
                event.setCancelled(true);
                return;
            }
            for (Block b : blocks) {
                Location loc = b.getRelative(direction).getLocation();
                Events.fireToCancel((Cancellable)event, new PlaceBlockEvent((Event)event, cause, loc, b.getType()));
            }
            entry.setCancelled(event.isCancelled());
            if (event.isCancelled()) {
                EventAbstractionListener.playDenyEffect(piston.getLocation().add(0.5, 1.0, 0.5));
            }
        }
    }

    @EventHandler(ignoreCancelled=true)
    public void onBlockPistonExtend(BlockPistonExtendEvent event) {
        EventDebounce.Entry entry = this.pistonExtendDebounce.getIfNotPresent(new BlockPistonExtendKey(event), (Cancellable)event);
        if (entry != null) {
            Cause cause = Cause.create(event.getBlock());
            ArrayList<Block> blocks = new ArrayList<Block>(event.getBlocks());
            int originalLength = blocks.size();
            Events.fireBulkEventToCancel((Cancellable)event, new BreakBlockEvent((Event)event, cause, event.getBlock().getWorld(), blocks, Material.AIR));
            if (originalLength != blocks.size()) {
                event.setCancelled(true);
                return;
            }
            BlockFace dir = event.getDirection();
            for (int i = 0; i < blocks.size(); ++i) {
                Block existing = (Block)blocks.get(i);
                if (existing.getPistonMoveReaction() != PistonMoveReaction.MOVE && existing.getPistonMoveReaction() != PistonMoveReaction.PUSH_ONLY && existing.getType() != Material.PISTON && existing.getType() != Material.STICKY_PISTON) continue;
                blocks.set(i, existing.getRelative(dir));
            }
            Events.fireBulkEventToCancel((Cancellable)event, new PlaceBlockEvent((Event)event, cause, event.getBlock().getWorld(), blocks, Material.STONE));
            if (blocks.size() != originalLength) {
                event.setCancelled(true);
            }
            entry.setCancelled(event.isCancelled());
            if (event.isCancelled()) {
                EventAbstractionListener.playDenyEffect(event.getBlock().getLocation().add(0.5, 1.0, 0.5));
            }
        }
    }

    @EventHandler(ignoreCancelled=true)
    public void onBlockDamage(BlockDamageEvent event) {
        Block target = event.getBlock();
        if (target.getType() == Material.CAKE) {
            Events.fireToCancel((Cancellable)event, new UseBlockEvent((Event)event, Cause.create(event.getPlayer()), target));
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        Block clicked = event.getClickedBlock();
        Cause cause = Cause.create(player);
        switch (event.getAction()) {
            case PHYSICAL: {
                if (event.useInteractedBlock() == Event.Result.DENY) break;
                if (clicked.getType() == Material.FARMLAND || clicked.getType() == Material.TURTLE_EGG || clicked.getType() == Material.SNIFFER_EGG) {
                    BreakBlockEvent breakDelagate = new BreakBlockEvent((Event)event, cause, clicked);
                    breakDelagate.setSilent(true);
                    breakDelagate.getRelevantFlags().add(Flags.TRAMPLE_BLOCKS);
                    boolean denied = Events.fireToCancel((Cancellable)event, breakDelagate);
                    if (!denied) {
                        PlaceBlockEvent placeDelegate = new PlaceBlockEvent((Event)event, cause, clicked.getLocation(), clicked.getType() == Material.FARMLAND ? Material.DIRT : clicked.getType());
                        placeDelegate.setSilent(true);
                        placeDelegate.getRelevantFlags().add(Flags.TRAMPLE_BLOCKS);
                        denied = Events.fireToCancel((Cancellable)event, placeDelegate);
                    }
                    if (denied) {
                        EventAbstractionListener.playDenyEffect(player, clicked.getLocation());
                    }
                    return;
                }
                DelegateEvent firedEvent = new UseBlockEvent((Event)event, cause, clicked).setAllowed(EventAbstractionListener.hasInteractBypass(clicked));
                if (Tag.REDSTONE_ORES.isTagged((Keyed)clicked.getType())) {
                    firedEvent.setSilent(true);
                }
                if (clicked.getType() == Material.BIG_DRIPLEAF) {
                    firedEvent.getRelevantFlags().add(Flags.USE_DRIPLEAF);
                    firedEvent.setSilent(true);
                }
                this.interactDebounce.debounce(clicked, (Entity)event.getPlayer(), (Cancellable)event, firedEvent);
                if (event.useInteractedBlock() != Event.Result.DENY || firedEvent.isSilent()) break;
                EventAbstractionListener.playDenyEffect(player, clicked.getLocation().add(0.0, 1.0, 0.0));
                break;
            }
            case RIGHT_CLICK_BLOCK: {
                Block placed;
                if (event.useInteractedBlock() != Event.Result.DENY) {
                    placed = clicked.getRelative(event.getBlockFace());
                    EventAbstractionListener.handleBlockRightClick(event, Cause.create(event.getPlayer()), item, clicked, placed);
                }
            }
            case LEFT_CLICK_BLOCK: {
                Block placed;
                if (event.useInteractedBlock() != Event.Result.DENY) {
                    boolean modifiesWorld;
                    boolean hasItemInteraction;
                    placed = clicked.getRelative(event.getBlockFace());
                    boolean bl = hasItemInteraction = item != null && EventAbstractionListener.isItemAppliedToBlock(item, clicked) && event.getAction() == Action.RIGHT_CLICK_BLOCK;
                    boolean bl2 = modifiesWorld = hasItemInteraction || EventAbstractionListener.isBlockModifiedOnClick(clicked, event.getAction() == Action.RIGHT_CLICK_BLOCK);
                    if (Events.fireAndTestCancel(new UseBlockEvent((Event)event, cause, clicked).setAllowed(!modifiesWorld))) {
                        event.setUseInteractedBlock(Event.Result.DENY);
                    }
                    for (Block connected : Blocks.getConnected(clicked)) {
                        if (!Events.fireAndTestCancel(new UseBlockEvent((Event)event, Cause.create(event.getPlayer()), connected).setAllowed(!modifiesWorld))) continue;
                        event.setUseInteractedBlock(Event.Result.DENY);
                        break;
                    }
                    if (hasItemInteraction && Events.fireAndTestCancel(new PlaceBlockEvent((Event)event, cause, clicked.getLocation(), clicked.getType()))) {
                        event.setUseItemInHand(Event.Result.DENY);
                        event.setUseInteractedBlock(Event.Result.DENY);
                    }
                    if (event.getAction() == Action.LEFT_CLICK_BLOCK && Materials.isFire(placed.getType()) && Events.fireAndTestCancel(new BreakBlockEvent((Event)event, Cause.create(event.getPlayer()), placed))) {
                        event.setUseInteractedBlock(Event.Result.DENY);
                        break;
                    }
                    if (event.isCancelled()) {
                        EventAbstractionListener.playDenyEffect(event.getPlayer(), clicked.getLocation().add(0.5, 1.0, 0.5));
                    }
                }
            }
            case LEFT_CLICK_AIR: 
            case RIGHT_CLICK_AIR: {
                if (event.useItemInHand() != Event.Result.DENY && item != null && !item.getType().isBlock() && Events.fireAndTestCancel(new UseItemEvent((Event)event, cause, player.getWorld(), item))) {
                    event.setUseItemInHand(Event.Result.DENY);
                }
                if (item == null || !EventAbstractionListener.getWorldConfig((World)player.getWorld()).blockUseAtFeet.test(item) || !Events.fireAndTestCancel(new UseBlockEvent((Event)event, cause, player.getLocation().getBlock()))) break;
                event.setUseInteractedBlock(Event.Result.DENY);
            }
        }
    }

    @EventHandler(ignoreCancelled=true)
    public void onEntityBlockForm(EntityBlockFormEvent event) {
        this.entityBreakBlockDebounce.debounce(event.getBlock(), event.getEntity(), (Cancellable)event, new PlaceBlockEvent((Event)event, Cause.create(event.getEntity()), event.getBlock().getLocation(), event.getNewState().getType()));
    }

    @EventHandler(ignoreCancelled=true)
    public void onEntityInteract(EntityInteractEvent event) {
        this.interactDebounce.debounce(event.getBlock(), event.getEntity(), (Cancellable)event, new UseBlockEvent((Event)event, Cause.create(event.getEntity()), event.getBlock()).setAllowed(EventAbstractionListener.hasInteractBypass(event.getBlock())));
    }

    @EventHandler(ignoreCancelled=true)
    public void onBlockFertilize(BlockFertilizeEvent event) {
        if (event.getBlocks().isEmpty()) {
            return;
        }
        Cause cause = Cause.create(event.getPlayer(), event.getBlock());
        Events.fireToCancel((Cancellable)event, new PlaceBlockEvent((Event)event, cause, event.getBlock().getWorld(), event.getBlocks()));
    }

    @EventHandler(ignoreCancelled=true)
    public void onBlockIgnite(BlockIgniteEvent event) {
        Block block = event.getBlock();
        Cause cause = event.getPlayer() != null ? Cause.create(event.getPlayer()) : (event.getIgnitingEntity() != null ? Cause.create(event.getIgnitingEntity()) : (event.getIgnitingBlock() != null ? Cause.create(event.getIgnitingBlock()) : Cause.unknown()));
        Events.fireToCancel((Cancellable)event, new PlaceBlockEvent((Event)event, cause, block.getLocation(), Material.FIRE));
    }

    @EventHandler(ignoreCancelled=true)
    public void onSignChange(SignChangeEvent event) {
        if (Events.fireToCancel((Cancellable)event, new PlaceBlockEvent((Event)event, Cause.create(event.getPlayer()), event.getBlock()))) {
            EventAbstractionListener.playDenyEffect(event.getPlayer(), event.getBlock().getLocation().add(0.5, 0.5, 0.5));
        }
    }

    @EventHandler(ignoreCancelled=true)
    public void onBedEnter(PlayerBedEnterEvent event) {
        Events.fireToCancel((Cancellable)event, new UseBlockEvent((Event)event, Cause.create(event.getPlayer()), event.getBed()));
    }

    @EventHandler(ignoreCancelled=true)
    public void onPlayerBucketEmpty(PlayerBucketEmptyEvent event) {
        Player player = event.getPlayer();
        Block blockClicked = event.getBlockClicked();
        Block blockAffected = blockClicked.getBlockData() instanceof Waterlogged ? blockClicked : blockClicked.getRelative(event.getBlockFace());
        boolean allowed = false;
        if (event.getBucket() == Material.MILK_BUCKET) {
            allowed = true;
        }
        ItemStack item = new ItemStack(event.getBucket(), 1);
        Material blockMaterial = Materials.getBucketBlockMaterial(event.getBucket());
        Events.fireToCancel((Cancellable)event, new PlaceBlockEvent((Event)event, Cause.create(player), blockAffected.getLocation(), blockMaterial).setAllowed(allowed));
        Events.fireToCancel((Cancellable)event, new UseItemEvent((Event)event, Cause.create(player), player.getWorld(), item).setAllowed(allowed));
        if (event.isCancelled()) {
            EventAbstractionListener.playDenyEffect(event.getPlayer(), blockAffected.getLocation().add(0.5, 0.5, 0.5));
        }
    }

    @EventHandler(ignoreCancelled=true)
    public void onPlayerBucketFill(PlayerBucketFillEvent event) {
        Player player = event.getPlayer();
        Block blockAffected = event.getBlockClicked().getRelative(event.getBlockFace());
        boolean allowed = false;
        if (event.getItemStack().getType() == Material.MILK_BUCKET) {
            allowed = true;
        }
        ItemStack item = new ItemStack(event.getBucket(), 1);
        Events.fireToCancel((Cancellable)event, new BreakBlockEvent((Event)event, Cause.create(player), blockAffected).setAllowed(allowed));
        Events.fireToCancel((Cancellable)event, new UseItemEvent((Event)event, Cause.create(player), player.getWorld(), item).setAllowed(allowed));
        if (event.isCancelled()) {
            EventAbstractionListener.playDenyEffect(event.getPlayer(), blockAffected.getLocation().add(0.5, 0.5, 0.5));
        }
    }

    @EventHandler(ignoreCancelled=true)
    public void onBlockFromTo(BlockFromToEvent event) {
        BukkitWorldConfiguration config = EventAbstractionListener.getWorldConfig(event.getBlock().getWorld());
        if (!config.useRegions || !config.highFreqFlags && !config.checkLiquidFlow) {
            return;
        }
        Block from = event.getBlock();
        Block to = event.getToBlock();
        Material fromType = from.getType();
        Material toType = to.getType();
        if (Materials.isLiquid(fromType) && toType.isSolid()) {
            return;
        }
        if (Materials.isWater(fromType) && Materials.isWater(toType) || Materials.isLava(fromType) && Materials.isLava(toType)) {
            return;
        }
        Cause cause = Cause.create(from);
        Events.fireToCancel((Cancellable)event, new PlaceBlockEvent((Event)event, cause, to.getLocation(), from.getType()));
    }

    @EventHandler(ignoreCancelled=true)
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        switch (event.getSpawnReason()) {
            case DISPENSE_EGG: 
            case EGG: 
            case SPAWNER_EGG: {
                if (!EventAbstractionListener.getWorldConfig((World)event.getEntity().getWorld()).strictEntitySpawn) break;
                Events.fireToCancel((Cancellable)event, new SpawnEntityEvent((Event)event, Cause.unknown(), (Entity)event.getEntity()));
                break;
            }
        }
    }

    @EventHandler(ignoreCancelled=true)
    public void onHangingPlace(HangingPlaceEvent event) {
        Events.fireToCancel((Cancellable)event, new SpawnEntityEvent((Event)event, Cause.create(event.getPlayer()), (Entity)event.getEntity()));
        if (event.isCancelled()) {
            Block effectBlock = event.getBlock().getRelative(event.getBlockFace());
            EventAbstractionListener.playDenyEffect(event.getPlayer(), effectBlock.getLocation().add(0.5, 0.5, 0.5));
        }
    }

    @EventHandler(ignoreCancelled=true)
    public void onHangingBreak(HangingBreakEvent event) {
        if (event instanceof HangingBreakByEntityEvent) {
            Entity remover = ((HangingBreakByEntityEvent)event).getRemover();
            Events.fireToCancel((Cancellable)event, new DestroyEntityEvent((Event)event, Cause.create(remover), (Entity)event.getEntity()));
            if (event.isCancelled() && remover instanceof Player) {
                EventAbstractionListener.playDenyEffect((Player)remover, event.getEntity().getLocation());
            }
        } else if (event.getCause() == HangingBreakEvent.RemoveCause.EXPLOSION) {
            DestroyEntityEvent destroyEntityEvent = new DestroyEntityEvent((Event)event, Cause.unknown(), (Entity)event.getEntity());
            destroyEntityEvent.getRelevantFlags().add(Flags.OTHER_EXPLOSION);
            if (event.getEntity() instanceof ItemFrame) {
                destroyEntityEvent.getRelevantFlags().add(Flags.ENTITY_ITEM_FRAME_DESTROY);
            } else if (event.getEntity() instanceof Painting) {
                destroyEntityEvent.getRelevantFlags().add(Flags.ENTITY_PAINTING_DESTROY);
            }
            Events.fireToCancel((Cancellable)event, destroyEntityEvent);
        }
    }

    @EventHandler(ignoreCancelled=true)
    public void onVehicleDestroy(VehicleDestroyEvent event) {
        Events.fireToCancel((Cancellable)event, new DestroyEntityEvent((Event)event, Cause.create(event.getAttacker()), (Entity)event.getVehicle()));
    }

    @EventHandler(ignoreCancelled=true)
    public void onBlockExp(BlockExpEvent event) {
        if (event.getExpToDrop() > 0 && Events.fireAndTestCancel(new SpawnEntityEvent((Event)event, Cause.create(event.getBlock()), event.getBlock().getLocation(), EntityType.EXPERIENCE_ORB))) {
            event.setExpToDrop(0);
        }
    }

    @EventHandler(ignoreCancelled=true)
    public void onPlayerFish(PlayerFishEvent event) {
        if (event.getState() == PlayerFishEvent.State.FISHING) {
            if (Events.fireAndTestCancel(new UseItemEvent((Event)event, Cause.create(event.getPlayer(), event.getHook()), event.getPlayer().getWorld(), event.getPlayer().getInventory().getItemInMainHand()))) {
                event.setCancelled(true);
            }
        } else if (event.getState() == PlayerFishEvent.State.CAUGHT_FISH) {
            if (event.getExpToDrop() > 0 && Events.fireAndTestCancel(new SpawnEntityEvent((Event)event, Cause.create(event.getPlayer(), event.getHook()), event.getHook().getLocation(), EntityType.EXPERIENCE_ORB))) {
                event.setExpToDrop(0);
            }
        } else if (event.getState() == PlayerFishEvent.State.CAUGHT_ENTITY) {
            Entity caught = event.getCaught();
            if (caught == null) {
                return;
            }
            if (caught instanceof Item) {
                Events.fireToCancel((Cancellable)event, new DestroyEntityEvent((Event)event, Cause.create(event.getPlayer(), event.getHook()), caught));
            } else if (Entities.isConsideredBuildingIfUsed(caught)) {
                Events.fireToCancel((Cancellable)event, new UseEntityEvent((Event)event, Cause.create(event.getPlayer(), event.getHook()), caught));
            } else if (Entities.isNonHostile(caught) || caught instanceof Player) {
                Events.fireToCancel((Cancellable)event, new DamageEntityEvent((Event)event, Cause.create(event.getPlayer(), event.getHook()), caught));
            }
        }
    }

    @EventHandler(ignoreCancelled=true)
    public void onExpBottle(ExpBottleEvent event) {
        if (Events.fireAndTestCancel(new SpawnEntityEvent((Event)event, Cause.create(event.getEntity()), event.getEntity().getLocation(), EntityType.EXPERIENCE_ORB))) {
            Player player;
            event.setExperience(0);
            ProjectileSource shooter = event.getEntity().getShooter();
            if (shooter instanceof Player && (player = (Player)shooter).getGameMode() != GameMode.CREATIVE) {
                player.getInventory().addItem(new ItemStack[]{new ItemStack(Material.EXPERIENCE_BOTTLE, 1)});
            }
        }
    }

    @EventHandler(ignoreCancelled=true)
    public void onEntityDeath(EntityDeathEvent event) {
        if (event.getDroppedExp() > 0 && Events.fireAndTestCancel(new SpawnEntityEvent((Event)event, Cause.create(event.getEntity()), event.getEntity().getLocation(), EntityType.EXPERIENCE_ORB))) {
            event.setDroppedExp(0);
        }
    }

    @EventHandler(ignoreCancelled=true)
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        World world = player.getWorld();
        ItemStack item = event.getHand() == EquipmentSlot.OFF_HAND ? player.getInventory().getItemInOffHand() : player.getInventory().getItemInMainHand();
        Entity entity = event.getRightClicked();
        if (Events.fireToCancel((Cancellable)event, new UseItemEvent((Event)event, Cause.create(player), world, item))) {
            return;
        }
        UseEntityEvent useEntityEvent = new UseEntityEvent((Event)event, Cause.create(player), entity);
        Material matchingItem = Materials.getRelatedMaterial(entity.getType());
        if (matchingItem != null && EventAbstractionListener.hasInteractBypass(world, matchingItem)) {
            useEntityEvent.setAllowed(true);
        }
        if (!Events.fireToCancel((Cancellable)event, useEntityEvent) && item.getType() == Material.FLINT_AND_STEEL && entity.getType() == EntityType.CREEPER) {
            Cause.trackParentCause((Metadatable)entity, player);
        }
    }

    @EventHandler(ignoreCancelled=true)
    public void onEntityDamage(EntityDamageEvent event) {
        if (event instanceof EntityDamageByBlockEvent) {
            Block attacker = ((EntityDamageByBlockEvent)event).getDamager();
            if (attacker != null) {
                Events.fireToCancel((Cancellable)event, new DamageEntityEvent((Event)event, Cause.create(attacker), event.getEntity()));
            }
        } else if (event instanceof EntityDamageByEntityEvent) {
            ItemStack item;
            EntityDamageByEntityEvent entityEvent = (EntityDamageByEntityEvent)event;
            Entity damager = entityEvent.getDamager();
            DamageEntityEvent eventToFire = new DamageEntityEvent((Event)event, Cause.create(damager), event.getEntity());
            if (damager instanceof Firework) {
                eventToFire.getRelevantFlags().add(Flags.FIREWORK_DAMAGE);
            } else if (damager instanceof Creeper) {
                eventToFire.getRelevantFlags().add(Flags.CREEPER_EXPLOSION);
            }
            if (Events.fireToCancel((Cancellable)event, eventToFire) && damager instanceof Tameable && damager instanceof Mob) {
                ((Mob)damager).setTarget(null);
            }
            if (damager instanceof Player && (item = ((Player)damager).getInventory().getItemInMainHand()).getType() != Material.AIR) {
                Events.fireToCancel((Cancellable)event, new UseItemEvent((Event)event, Cause.create(damager), event.getEntity().getWorld(), item));
            }
        }
    }

    @EventHandler(ignoreCancelled=true)
    public void onEntityCombust(EntityCombustEvent event) {
        if (event instanceof EntityCombustByBlockEvent) {
            Events.fireToCancel((Cancellable)event, new DamageEntityEvent((Event)event, Cause.create(event.getEntity().getLocation().getBlock()), event.getEntity()));
        } else if (event instanceof EntityCombustByEntityEvent) {
            Events.fireToCancel((Cancellable)event, new DamageEntityEvent((Event)event, Cause.create(((EntityCombustByEntityEvent)event).getCombuster()), event.getEntity()));
        }
    }

    @EventHandler(ignoreCancelled=true)
    public void onEntityUnleash(EntityUnleashEvent event) {
        if (event instanceof PlayerUnleashEntityEvent) {
            PlayerUnleashEntityEvent playerEvent = (PlayerUnleashEntityEvent)event;
            Events.fireToCancel((Cancellable)playerEvent, new UseEntityEvent((Event)playerEvent, Cause.create(playerEvent.getPlayer()), event.getEntity()));
        }
    }

    @EventHandler(ignoreCancelled=true)
    public void onEntityTame(EntityTameEvent event) {
        Events.fireToCancel((Cancellable)event, new UseEntityEvent((Event)event, Cause.create(event.getOwner()), (Entity)event.getEntity()));
    }

    @EventHandler(ignoreCancelled=true)
    public void onPlayerShearEntity(PlayerShearEntityEvent event) {
        Events.fireToCancel((Cancellable)event, new UseEntityEvent((Event)event, Cause.create(event.getPlayer()), event.getEntity()));
    }

    @EventHandler(ignoreCancelled=true)
    public void onPlayerPickupItem(PlayerPickupItemEvent event) {
        Item item = event.getItem();
        this.pickupDebounce.debounce((Entity)event.getPlayer(), (Entity)item, (Cancellable)event, new DestroyEntityEvent((Event)event, Cause.create(event.getPlayer()), (Entity)event.getItem()));
    }

    @EventHandler(ignoreCancelled=true)
    public void onEntityPickupItem(EntityPickupItemEvent event) {
        Item item = event.getItem();
        this.pickupDebounce.debounce((Entity)event.getEntity(), (Entity)item, (Cancellable)event, new DestroyEntityEvent((Event)event, Cause.create(event.getEntity()), (Entity)event.getItem()));
    }

    @EventHandler(ignoreCancelled=true)
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        Events.fireToCancel((Cancellable)event, new SpawnEntityEvent((Event)event, Cause.create(event.getPlayer()), (Entity)event.getItemDrop()));
    }

    @EventHandler(ignoreCancelled=true)
    public void onVehicleDamage(VehicleDamageEvent event) {
        Entity attacker = event.getAttacker();
        Events.fireToCancel((Cancellable)event, new DamageEntityEvent((Event)event, Cause.create(attacker), (Entity)event.getVehicle()));
    }

    @EventHandler(ignoreCancelled=true)
    public void onPlayerItemConsume(PlayerItemConsumeEvent event) {
        Events.fireToCancel((Cancellable)event, new UseItemEvent((Event)event, Cause.create(event.getPlayer()), event.getPlayer().getWorld(), event.getItem()));
    }

    @EventHandler(ignoreCancelled=true)
    public void onInventoryOpen(InventoryOpenEvent event) {
        InventoryHolder holder = event.getInventory().getHolder();
        if (holder instanceof Entity && holder == event.getPlayer()) {
            return;
        }
        EventAbstractionListener.handleInventoryHolderUse(event, Cause.create(event.getPlayer()), holder);
    }

    @EventHandler(ignoreCancelled=true)
    public void onInventoryMoveItem(InventoryMoveItemEvent event) {
        AbstractEventDebounce.Entry entry;
        InventoryHolder causeHolder = PaperLib.getHolder(event.getInitiator(), false).getHolder();
        BukkitWorldConfiguration wcfg = null;
        if (causeHolder instanceof Hopper) {
            wcfg = EventAbstractionListener.getWorldConfig(((Hopper)causeHolder).getWorld());
            if (wcfg.ignoreHopperMoveEvents) {
                return;
            }
        }
        if (causeHolder instanceof HopperMinecart) {
            wcfg = EventAbstractionListener.getWorldConfig(((HopperMinecart)causeHolder).getWorld());
            if (wcfg.ignoreHopperMoveEvents) {
                return;
            }
        }
        if ((entry = this.moveItemDebounce.tryDebounce(event)) != null) {
            InventoryHolder sourceHolder = PaperLib.getHolder(event.getSource(), false).getHolder();
            InventoryHolder targetHolder = PaperLib.getHolder(event.getDestination(), false).getHolder();
            Cause cause = causeHolder instanceof Entity ? Cause.create(causeHolder) : (causeHolder instanceof BlockState ? Cause.create(((BlockState)causeHolder).getBlock()) : Cause.unknown());
            if (causeHolder != null && !causeHolder.equals((Object)sourceHolder)) {
                EventAbstractionListener.handleInventoryHolderUse(event, cause, sourceHolder);
            }
            EventAbstractionListener.handleInventoryHolderUse(event, cause, targetHolder);
            if (event.isCancelled() && causeHolder instanceof Hopper && wcfg.breakDeniedHoppers) {
                Bukkit.getScheduler().scheduleSyncDelayedTask((Plugin)EventAbstractionListener.getPlugin(), () -> ((Hopper)causeHolder).getBlock().breakNaturally());
            } else {
                entry.setCancelled(event.isCancelled());
            }
        }
    }

    @EventHandler(ignoreCancelled=true)
    public void onPotionSplash(PotionSplashEvent event) {
        ThrownPotion entity = event.getEntity();
        ThrownPotion potion = event.getPotion();
        World world = entity.getWorld();
        Cause cause = Cause.create(potion);
        Events.fireToCancel((Cancellable)event, new UseItemEvent((Event)event, cause, world, potion.getItem()));
        if (!event.isCancelled()) {
            int blocked = 0;
            int affectedSize = event.getAffectedEntities().size();
            boolean hasDamageEffect = Materials.hasDamageEffect(potion.getEffects());
            for (LivingEntity affected : event.getAffectedEntities()) {
                LivingEntity shooter;
                AbstractEntityEvent delegate = hasDamageEffect ? new DamageEntityEvent((Event)event, cause, (Entity)affected) : new UseEntityEvent((Event)event, cause, (Entity)affected);
                delegate.getRelevantFlags().add(Flags.POTION_SPLASH);
                ProjectileSource projectileSource = potion.getShooter();
                if (projectileSource instanceof LivingEntity && !((shooter = (LivingEntity)projectileSource) instanceof Player) && affected instanceof Player) {
                    delegate.getRelevantFlags().add(Flags.MOB_DAMAGE);
                }
                if (!Events.fireAndTestCancel(delegate)) continue;
                event.setIntensity(affected, 0.0);
                ++blocked;
            }
            if (affectedSize > 0 && blocked == affectedSize) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(ignoreCancelled=true)
    public void onBlockDispense(BlockDispenseEvent event) {
        Block dispenserBlock = event.getBlock();
        if (dispenserBlock.getType() == Material.DISPENSER) {
            Cause cause = Cause.create(event.getBlock());
            ItemStack item = event.getItem();
            if (Events.fireToCancel((Cancellable)event, new UseItemEvent((Event)event, cause, dispenserBlock.getWorld(), item))) {
                return;
            }
            BlockData blockData = dispenserBlock.getBlockData();
            Dispenser dispenser = (Dispenser)blockData;
            Block placed = dispenserBlock.getRelative(dispenser.getFacing());
            Block clicked = placed.getRelative(dispenser.getFacing());
            EventAbstractionListener.handleBlockRightClick(event, cause, item, clicked, placed);
            if (Materials.isShulkerBox(item.getType())) {
                if (Events.fireToCancel((Cancellable)event, new PlaceBlockEvent((Event)event, cause, placed.getLocation(), item.getType()))) {
                    EventAbstractionListener.playDenyEffect(placed.getLocation());
                }
            } else if (EventAbstractionListener.isItemAppliedToBlock(item, placed) && Events.fireToCancel((Cancellable)event, new PlaceBlockEvent((Event)event, cause, placed.getLocation(), placed.getType()))) {
                EventAbstractionListener.playDenyEffect(placed.getLocation());
            }
        }
    }

    @EventHandler(ignoreCancelled=true)
    public void onLingeringSplash(LingeringPotionSplashEvent event) {
        AreaEffectCloud aec = event.getAreaEffectCloud();
        ThrownPotion potion = event.getEntity();
        World world = potion.getWorld();
        Cause cause = Cause.create(event.getEntity());
        Events.fireToCancel((Cancellable)event, new UseItemEvent((Event)event, cause, world, potion.getItem()));
        if (!event.isCancelled()) {
            Events.fireToCancel((Cancellable)event, new SpawnEntityEvent((Event)event, cause, aec.getLocation().add(0.5, 0.0, 0.5), EntityType.AREA_EFFECT_CLOUD));
        }
    }

    @EventHandler(ignoreCancelled=true)
    public void onLingeringApply(AreaEffectCloudApplyEvent event) {
        List baseEffectTypes;
        AreaEffectCloud entity = event.getEntity();
        ArrayList<PotionEffect> effects = new ArrayList<PotionEffect>();
        List list = baseEffectTypes = entity.getBasePotionType() == null ? null : entity.getBasePotionType().getPotionEffects();
        if (baseEffectTypes != null) {
            effects.addAll(baseEffectTypes);
        }
        if (entity.hasCustomEffects()) {
            effects.addAll(entity.getCustomEffects());
        }
        if (!Materials.hasDamageEffect(effects)) {
            return;
        }
        Cause cause = Cause.create(event.getEntity());
        event.getAffectedEntities().removeIf(victim -> Events.fireAndTestCancel(new DamageEntityEvent((Event)event, cause, (Entity)victim)));
    }

    @EventHandler(ignoreCancelled=true)
    public void onPlayerInteractAtEntity(PlayerInteractAtEntityEvent event) {
        this.onPlayerInteractEntity((PlayerInteractEntityEvent)event);
    }

    @EventHandler(ignoreCancelled=true)
    public void onBlockExplode(BlockExplodeEvent event) {
        BreakBlockEvent eventToFire = new BreakBlockEvent((Event)event, Cause.create(event.getBlock()), event.getBlock().getLocation().getWorld(), event.blockList(), Material.AIR);
        eventToFire.getRelevantFlags().add(Flags.OTHER_EXPLOSION);
        Events.fireBulkEventToCancel((Cancellable)event, eventToFire);
    }

    @EventHandler(ignoreCancelled=true)
    public void onTakeLecternBook(PlayerTakeLecternBookEvent event) {
        UseBlockEvent useEvent = new UseBlockEvent((Event)event, Cause.create(event.getPlayer()), event.getLectern().getBlock());
        Events.fireToCancel((Cancellable)event, useEvent);
    }

    @EventHandler(ignoreCancelled=true)
    public void onCauldronLevelChange(CauldronLevelChangeEvent event) {
        if (event.getEntity() == null) {
            return;
        }
        this.interactDebounce.debounce(event.getBlock(), event.getEntity(), (Cancellable)event, new UseBlockEvent((Event)event, Cause.create(event.getEntity()), event.getBlock()).setAllowed(EventAbstractionListener.hasInteractBypass(event.getBlock())));
    }

    private static <T extends Event> void handleBlockRightClick(T event, Cause cause, @Nullable ItemStack item, Block clicked, Block placed) {
        EntityType possibleEntityType;
        if (item != null && item.getType() == Material.TNT) {
            Events.fireToCancel((Cancellable)event, new UseBlockEvent(event, cause, clicked.getLocation(), Material.TNT));
            Events.fireToCancel((Cancellable)event, new PlaceBlockEvent(event, cause, placed.getLocation(), Material.TNT));
            return;
        }
        if (item != null && Materials.isMinecart(item.getType())) {
            EntityType entityType = Materials.getRelatedEntity(item.getType());
            if (entityType == null) {
                entityType = EntityType.MINECART;
            }
            Events.fireToCancel((Cancellable)event, new SpawnEntityEvent(event, cause, clicked.getLocation().add(0.5, 0.0, 0.5), entityType));
            return;
        }
        if (item != null && Materials.isBoat(item.getType())) {
            Events.fireToCancel((Cancellable)event, new SpawnEntityEvent(event, cause, placed.getLocation().add(0.5, 0.0, 0.5), EntityType.BOAT));
            return;
        }
        if (item != null && item.getType() == Material.ARMOR_STAND) {
            Events.fireToCancel((Cancellable)event, new SpawnEntityEvent(event, cause, placed.getLocation().add(0.5, 0.0, 0.5), EntityType.ARMOR_STAND));
            return;
        }
        if (item != null && item.getType() == Material.END_CRYSTAL) {
            Events.fireToCancel((Cancellable)event, new SpawnEntityEvent(event, cause, placed.getLocation().add(0.5, 0.0, 0.5), EntityType.END_CRYSTAL));
            return;
        }
        if (item != null && (possibleEntityType = Materials.getEntitySpawnEgg(item.getType())) != null) {
            Events.fireToCancel((Cancellable)event, new SpawnEntityEvent(event, cause, placed.getLocation().add(0.5, 0.0, 0.5), possibleEntityType));
            return;
        }
        if (item != null && (item.getType() == Material.WATER_BUCKET || item.getType() == Material.LAVA_BUCKET)) {
            Events.fireToCancel((Cancellable)event, new PlaceBlockEvent(event, cause, placed.getLocation(), item.getType() == Material.WATER_BUCKET ? Material.WATER : Material.LAVA));
            return;
        }
    }

    private static <T extends Event> void handleInventoryHolderUse(T originalEvent, Cause cause, InventoryHolder holder) {
        BlockState block;
        if (((Cancellable)originalEvent).isCancelled()) {
            return;
        }
        if (holder instanceof Entity) {
            Entity entity = (Entity)holder;
            Material mat = Materials.getRelatedMaterial(entity.getType());
            UseEntityEvent useEntityEvent = new UseEntityEvent(originalEvent, cause, entity);
            if (mat != null && EventAbstractionListener.hasInteractBypass(entity.getWorld(), mat)) {
                useEntityEvent.setAllowed(true);
            }
            Events.fireToCancel((Cancellable)originalEvent, useEntityEvent);
        } else if (holder instanceof BlockState && (block = (BlockState)holder).isPlaced()) {
            UseBlockEvent useBlockEvent = new UseBlockEvent(originalEvent, cause, block.getBlock());
            if (EventAbstractionListener.hasInteractBypass(block.getWorld(), block.getType())) {
                useBlockEvent.setAllowed(true);
            }
            Events.fireToCancel((Cancellable)originalEvent, useBlockEvent);
        } else if (holder instanceof DoubleChest) {
            InventoryHolder left = ((DoubleChest)holder).getLeftSide();
            InventoryHolder right = ((DoubleChest)holder).getRightSide();
            if (left instanceof Chest) {
                Events.fireToCancel((Cancellable)originalEvent, new UseBlockEvent(originalEvent, cause, ((Chest)left).getBlock()));
            }
            if (right instanceof Chest) {
                Events.fireToCancel((Cancellable)originalEvent, new UseBlockEvent(originalEvent, cause, ((Chest)right).getBlock()));
            }
        }
    }

    private static boolean hasInteractBypass(Block block) {
        return EventAbstractionListener.getWorldConfig((World)block.getWorld()).allowAllInteract.test(block);
    }

    private static boolean hasInteractBypass(World world, Material material) {
        return EventAbstractionListener.getWorldConfig((World)world).allowAllInteract.test(material);
    }

    private static boolean hasInteractBypass(World world, ItemStack item) {
        return EventAbstractionListener.getWorldConfig((World)world).allowAllInteract.test(item);
    }

    private static boolean isBlockModifiedOnClick(Block block, boolean rightClick) {
        return Materials.isBlockModifiedOnClick(block.getType(), rightClick) && !EventAbstractionListener.hasInteractBypass(block);
    }

    private static boolean isItemAppliedToBlock(ItemStack item, Block clicked) {
        return Materials.isItemAppliedToBlock(item.getType(), clicked.getType()) && !EventAbstractionListener.hasInteractBypass(clicked) && !EventAbstractionListener.hasInteractBypass(clicked.getWorld(), item);
    }

    private static void playDenyEffect(Player player, Location location) {
        if (EventAbstractionListener.getConfig().particleEffects) {
            player.playEffect(location, Effect.SMOKE, (Object)BlockFace.UP);
        }
    }

    private static void playDenyEffect(Location location) {
        if (EventAbstractionListener.getConfig().particleEffects) {
            location.getWorld().playEffect(location, Effect.SMOKE, (Object)BlockFace.UP);
        }
    }

    private static class PaperListener
    implements Listener {
        private PaperListener() {
        }

        @EventHandler(ignoreCancelled=true)
        public void onEntityTransform(EntityZapEvent event) {
            Events.fireToCancel((Cancellable)event, new DamageEntityEvent((Event)event, Cause.create(event.getBolt()), event.getEntity()));
        }

        @EventHandler(ignoreCancelled=true)
        public void onSignOpen(PlayerOpenSignEvent event) {
            if (event.getCause() == PlayerOpenSignEvent.Cause.INTERACT) {
                Events.fireToCancel((Cancellable)event, new UseBlockEvent((Event)event, Cause.create(event.getPlayer()), event.getSign().getBlock()));
            }
        }

        @EventHandler(ignoreCancelled=true)
        public void onEntityKnockbackByEntity(EntityKnockbackByEntityEvent event) {
            EventAbstractionListener.handleKnockback(event, event.getHitBy());
        }
    }

    private static class SpigotListener
    implements Listener {
        private SpigotListener() {
        }

        @EventHandler(ignoreCancelled=true)
        public void onEntityKnockbackByEntity(org.bukkit.event.entity.EntityKnockbackByEntityEvent event) {
            EventAbstractionListener.handleKnockback(event, event.getSourceEntity());
        }
    }
}

