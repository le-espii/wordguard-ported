/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sk89q.worldedit.bukkit.BukkitAdapter
 *  org.bukkit.Location
 *  org.bukkit.Material
 *  org.bukkit.World
 *  org.bukkit.block.Block
 *  org.bukkit.block.data.Waterlogged
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 *  org.bukkit.entity.Snowman
 *  org.bukkit.event.Cancellable
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.EventPriority
 *  org.bukkit.event.block.BlockBreakEvent
 *  org.bukkit.event.block.BlockBurnEvent
 *  org.bukkit.event.block.BlockExplodeEvent
 *  org.bukkit.event.block.BlockFadeEvent
 *  org.bukkit.event.block.BlockFormEvent
 *  org.bukkit.event.block.BlockFromToEvent
 *  org.bukkit.event.block.BlockGrowEvent
 *  org.bukkit.event.block.BlockIgniteEvent
 *  org.bukkit.event.block.BlockIgniteEvent$IgniteCause
 *  org.bukkit.event.block.BlockPhysicsEvent
 *  org.bukkit.event.block.BlockPlaceEvent
 *  org.bukkit.event.block.BlockRedstoneEvent
 *  org.bukkit.event.block.BlockSpreadEvent
 *  org.bukkit.event.block.EntityBlockFormEvent
 *  org.bukkit.event.block.LeavesDecayEvent
 *  org.bukkit.event.block.MoistureChangeEvent
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.inventory.meta.Damageable
 *  org.bukkit.inventory.meta.ItemMeta
 */
package com.sk89q.worldguard.bukkit.listener;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.BukkitConfigurationManager;
import com.sk89q.worldguard.bukkit.BukkitWorldConfiguration;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.bukkit.listener.AbstractListener;
import com.sk89q.worldguard.bukkit.util.Materials;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.association.RegionAssociable;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.util.SpongeUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.Waterlogged;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowman;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockFormEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockGrowEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.block.EntityBlockFormEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.block.MoistureChangeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

public class WorldGuardBlockListener
extends AbstractListener {
    public WorldGuardBlockListener(WorldGuardPlugin plugin) {
        super(plugin);
    }

    @EventHandler(priority=EventPriority.HIGH, ignoreCancelled=true)
    public void onBlockBreak(BlockBreakEvent event) {
        ItemStack held;
        ItemMeta meta;
        Player player = event.getPlayer();
        BukkitWorldConfiguration wcfg = WorldGuardBlockListener.getWorldConfig(player.getWorld());
        if (!wcfg.itemDurability && (meta = (held = player.getInventory().getItemInMainHand()).getItemMeta()) != null) {
            ((Damageable)meta).setDamage(0);
            held.setItemMeta(meta);
            player.getInventory().setItemInMainHand(held);
        }
    }

    @EventHandler(ignoreCancelled=true)
    public void onBlockFromTo(BlockFromToEvent event) {
        Material targetId;
        World world = event.getBlock().getWorld();
        Block blockFrom = event.getBlock();
        Block blockTo = event.getToBlock();
        BukkitConfigurationManager cfg = WorldGuardBlockListener.getConfig();
        if (cfg.activityHaltToggle) {
            event.setCancelled(true);
            return;
        }
        Material fromType = blockFrom.getType();
        boolean isWater = Materials.isWater(fromType);
        boolean isLava = fromType == Material.LAVA;
        boolean isAir = fromType == Material.AIR;
        BukkitWorldConfiguration wcfg = WorldGuardBlockListener.getWorldConfig(world);
        if (wcfg.simulateSponge && isWater) {
            int ox = blockTo.getX();
            int oy = blockTo.getY();
            int oz = blockTo.getZ();
            for (int cx = -wcfg.spongeRadius; cx <= wcfg.spongeRadius; ++cx) {
                for (int cy = -wcfg.spongeRadius; cy <= wcfg.spongeRadius; ++cy) {
                    for (int cz = -wcfg.spongeRadius; cz <= wcfg.spongeRadius; ++cz) {
                        Block sponge = world.getBlockAt(ox + cx, oy + cy, oz + cz);
                        if (sponge.getType() != Material.SPONGE || wcfg.redstoneSponges && sponge.isBlockIndirectlyPowered()) continue;
                        event.setCancelled(true);
                        return;
                    }
                }
            }
        }
        if (!wcfg.preventWaterDamage.isEmpty()) {
            Material targetId2 = blockTo.getType();
            if ((isAir || isWater) && wcfg.preventWaterDamage.contains(BukkitAdapter.asBlockType((Material)targetId2).getId())) {
                event.setCancelled(true);
                return;
            }
        }
        if (!wcfg.allowedLavaSpreadOver.isEmpty() && isLava && !wcfg.allowedLavaSpreadOver.contains(BukkitAdapter.asBlockType((Material)(targetId = blockTo.getRelative(0, -1, 0).getType())).getId())) {
            event.setCancelled(true);
            return;
        }
        if (wcfg.highFreqFlags && (isWater || blockFrom.getBlockData() instanceof Waterlogged) && WorldGuard.getInstance().getPlatform().getRegionContainer().createQuery().queryState(BukkitAdapter.adapt((Location)blockFrom.getLocation()), (RegionAssociable)null, Flags.WATER_FLOW) == StateFlag.State.DENY) {
            event.setCancelled(true);
            return;
        }
        if (wcfg.highFreqFlags && isLava && !StateFlag.test(WorldGuard.getInstance().getPlatform().getRegionContainer().createQuery().queryState(BukkitAdapter.adapt((Location)blockFrom.getLocation()), (RegionAssociable)null, Flags.LAVA_FLOW))) {
            event.setCancelled(true);
            return;
        }
    }

    @EventHandler(priority=EventPriority.HIGH, ignoreCancelled=true)
    public void onBlockIgnite(BlockIgniteEvent event) {
        int z;
        int y;
        int x;
        boolean isFireSpread;
        BlockIgniteEvent.IgniteCause cause = event.getCause();
        Block block = event.getBlock();
        World world = block.getWorld();
        BukkitConfigurationManager cfg = WorldGuardBlockListener.getConfig();
        if (cfg.activityHaltToggle) {
            event.setCancelled(true);
            return;
        }
        BukkitWorldConfiguration wcfg = WorldGuardBlockListener.getWorldConfig(world);
        boolean bl = isFireSpread = cause == BlockIgniteEvent.IgniteCause.SPREAD;
        if (wcfg.preventLightningFire && cause == BlockIgniteEvent.IgniteCause.LIGHTNING) {
            event.setCancelled(true);
            return;
        }
        if (wcfg.preventLavaFire && cause == BlockIgniteEvent.IgniteCause.LAVA) {
            event.setCancelled(true);
            return;
        }
        if (wcfg.disableFireSpread && isFireSpread) {
            event.setCancelled(true);
            return;
        }
        if (wcfg.blockLighter && (cause == BlockIgniteEvent.IgniteCause.FLINT_AND_STEEL || cause == BlockIgniteEvent.IgniteCause.FIREBALL) && event.getPlayer() != null && !WorldGuardBlockListener.getPlugin().hasPermission((CommandSender)event.getPlayer(), "worldguard.override.lighter")) {
            event.setCancelled(true);
            return;
        }
        if (wcfg.fireSpreadDisableToggle && isFireSpread) {
            event.setCancelled(true);
            return;
        }
        if (!wcfg.disableFireSpreadBlocks.isEmpty() && isFireSpread && (wcfg.disableFireSpreadBlocks.contains(BukkitAdapter.asBlockType((Material)world.getBlockAt(x = block.getX(), (y = block.getY()) - 1, z = block.getZ()).getType()).getId()) || wcfg.disableFireSpreadBlocks.contains(BukkitAdapter.asBlockType((Material)world.getBlockAt(x + 1, y, z).getType()).getId()) || wcfg.disableFireSpreadBlocks.contains(BukkitAdapter.asBlockType((Material)world.getBlockAt(x - 1, y, z).getType()).getId()) || wcfg.disableFireSpreadBlocks.contains(BukkitAdapter.asBlockType((Material)world.getBlockAt(x, y, z - 1).getType()).getId()) || wcfg.disableFireSpreadBlocks.contains(BukkitAdapter.asBlockType((Material)world.getBlockAt(x, y, z + 1).getType()).getId()))) {
            event.setCancelled(true);
            return;
        }
        if (wcfg.useRegions) {
            ApplicableRegionSet set = WorldGuard.getInstance().getPlatform().getRegionContainer().createQuery().getApplicableRegions(BukkitAdapter.adapt((Location)block.getLocation()));
            if (wcfg.highFreqFlags && isFireSpread && !set.testState(null, Flags.FIRE_SPREAD)) {
                event.setCancelled(true);
                return;
            }
            if (wcfg.highFreqFlags && cause == BlockIgniteEvent.IgniteCause.LAVA && !set.testState(null, Flags.LAVA_FIRE)) {
                event.setCancelled(true);
                return;
            }
            if (cause == BlockIgniteEvent.IgniteCause.FIREBALL && event.getPlayer() == null && !set.testState(null, Flags.GHAST_FIREBALL)) {
                event.setCancelled(true);
                return;
            }
            if (cause == BlockIgniteEvent.IgniteCause.LIGHTNING && !set.testState(null, Flags.LIGHTNING)) {
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler(priority=EventPriority.HIGH, ignoreCancelled=true)
    public void onBlockBurn(BlockBurnEvent event) {
        Block block;
        BukkitConfigurationManager cfg = WorldGuardBlockListener.getConfig();
        if (cfg.activityHaltToggle) {
            event.setCancelled(true);
            return;
        }
        BukkitWorldConfiguration wcfg = WorldGuardBlockListener.getWorldConfig(event.getBlock().getWorld());
        if (wcfg.disableFireSpread) {
            event.setCancelled(true);
            return;
        }
        if (wcfg.fireSpreadDisableToggle) {
            Block block2 = event.getBlock();
            event.setCancelled(true);
            this.checkAndDestroyFireAround(block2.getWorld(), block2.getX(), block2.getY(), block2.getZ());
            return;
        }
        if (!wcfg.disableFireSpreadBlocks.isEmpty() && wcfg.disableFireSpreadBlocks.contains(BukkitAdapter.asBlockType((Material)(block = event.getBlock()).getType()).getId())) {
            event.setCancelled(true);
            this.checkAndDestroyFireAround(block.getWorld(), block.getX(), block.getY(), block.getZ());
            return;
        }
        if (wcfg.isChestProtected(BukkitAdapter.adapt((Location)event.getBlock().getLocation()))) {
            event.setCancelled(true);
            return;
        }
        if (wcfg.useRegions) {
            block = event.getBlock();
            int x = block.getX();
            int y = block.getY();
            int z = block.getZ();
            ApplicableRegionSet set = WorldGuard.getInstance().getPlatform().getRegionContainer().createQuery().getApplicableRegions(BukkitAdapter.adapt((Location)block.getLocation()));
            if (!set.testState(null, Flags.FIRE_SPREAD)) {
                this.checkAndDestroyFireAround(block.getWorld(), x, y, z);
                event.setCancelled(true);
            }
        }
    }

    private void checkAndDestroyFireAround(World world, int x, int y, int z) {
        this.checkAndDestroyFire(world, x, y, z + 1);
        this.checkAndDestroyFire(world, x, y, z - 1);
        this.checkAndDestroyFire(world, x, y + 1, z);
        this.checkAndDestroyFire(world, x, y - 1, z);
        this.checkAndDestroyFire(world, x + 1, y, z);
        this.checkAndDestroyFire(world, x - 1, y, z);
    }

    private void checkAndDestroyFire(World world, int x, int y, int z) {
        if (Materials.isFire(world.getBlockAt(x, y, z).getType())) {
            world.getBlockAt(x, y, z).setType(Material.AIR);
        }
    }

    @EventHandler(ignoreCancelled=true)
    public void onBlockPhysics(BlockPhysicsEvent event) {
        BukkitConfigurationManager cfg = WorldGuardBlockListener.getConfig();
        if (cfg.activityHaltToggle) {
            event.setCancelled(true);
            return;
        }
        BukkitWorldConfiguration wcfg = WorldGuardBlockListener.getWorldConfig(event.getBlock().getWorld());
        Material id = event.getBlock().getType();
        if (id == Material.GRAVEL && wcfg.noPhysicsGravel) {
            event.setCancelled(true);
            return;
        }
        if ((id == Material.SAND || id == Material.RED_SAND) && wcfg.noPhysicsSand) {
            event.setCancelled(true);
            return;
        }
        if (id == Material.NETHER_PORTAL && wcfg.allowPortalAnywhere) {
            event.setCancelled(true);
            return;
        }
        if (id == Material.LADDER && wcfg.ropeLadders && event.getBlock().getRelative(0, 1, 0).getType() == Material.LADDER) {
            event.setCancelled(true);
            return;
        }
    }

    @EventHandler(priority=EventPriority.HIGH, ignoreCancelled=true)
    public void onBlockPlace(BlockPlaceEvent event) {
        Block target = event.getBlock();
        World world = target.getWorld();
        BukkitWorldConfiguration wcfg = WorldGuardBlockListener.getWorldConfig(world);
        if (wcfg.simulateSponge && target.getType() == Material.SPONGE) {
            if (wcfg.redstoneSponges && target.isBlockIndirectlyPowered()) {
                return;
            }
            int ox = target.getX();
            int oy = target.getY();
            int oz = target.getZ();
            SpongeUtil.clearSpongeWater(BukkitAdapter.adapt((World)world), ox, oy, oz);
        }
    }

    @EventHandler(priority=EventPriority.HIGH)
    public void onBlockRedstoneChange(BlockRedstoneEvent event) {
        Block blockTo = event.getBlock();
        World world = blockTo.getWorld();
        BukkitWorldConfiguration wcfg = WorldGuardBlockListener.getWorldConfig(world);
        if (wcfg.simulateSponge && wcfg.redstoneSponges) {
            int ox = blockTo.getX();
            int oy = blockTo.getY();
            int oz = blockTo.getZ();
            for (int cx = -1; cx <= 1; ++cx) {
                for (int cy = -1; cy <= 1; ++cy) {
                    for (int cz = -1; cz <= 1; ++cz) {
                        Block sponge = world.getBlockAt(ox + cx, oy + cy, oz + cz);
                        if (sponge.getType() == Material.SPONGE && sponge.isBlockIndirectlyPowered()) {
                            SpongeUtil.clearSpongeWater(BukkitAdapter.adapt((World)world), ox + cx, oy + cy, oz + cz);
                            continue;
                        }
                        if (sponge.getType() != Material.SPONGE || sponge.isBlockIndirectlyPowered()) continue;
                        SpongeUtil.addSpongeWater(BukkitAdapter.adapt((World)world), ox + cx, oy + cy, oz + cz);
                    }
                }
            }
            return;
        }
    }

    @EventHandler(priority=EventPriority.HIGH, ignoreCancelled=true)
    public void onLeavesDecay(LeavesDecayEvent event) {
        BukkitConfigurationManager cfg = WorldGuardBlockListener.getConfig();
        if (cfg.activityHaltToggle) {
            event.setCancelled(true);
            return;
        }
        BukkitWorldConfiguration wcfg = WorldGuardBlockListener.getWorldConfig(event.getBlock().getWorld());
        if (wcfg.disableLeafDecay) {
            event.setCancelled(true);
            return;
        }
        if (wcfg.useRegions && !StateFlag.test(WorldGuard.getInstance().getPlatform().getRegionContainer().createQuery().queryState(BukkitAdapter.adapt((Location)event.getBlock().getLocation()), (RegionAssociable)null, Flags.LEAF_DECAY))) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority=EventPriority.HIGH, ignoreCancelled=true)
    public void onBlockForm(BlockFormEvent event) {
        BukkitConfigurationManager cfg = WorldGuardBlockListener.getConfig();
        if (cfg.activityHaltToggle) {
            event.setCancelled(true);
            return;
        }
        BukkitWorldConfiguration wcfg = WorldGuardBlockListener.getWorldConfig(event.getBlock().getWorld());
        Material type = event.getNewState().getType();
        if (event instanceof EntityBlockFormEvent) {
            if (((EntityBlockFormEvent)event).getEntity() instanceof Snowman && wcfg.disableSnowmanTrails) {
                event.setCancelled(true);
                return;
            }
            return;
        }
        if (type == Material.ICE) {
            if (wcfg.disableIceFormation) {
                event.setCancelled(true);
                return;
            }
            if (wcfg.useRegions && !StateFlag.test(WorldGuard.getInstance().getPlatform().getRegionContainer().createQuery().queryState(BukkitAdapter.adapt((Location)event.getBlock().getLocation()), (RegionAssociable)null, Flags.ICE_FORM))) {
                event.setCancelled(true);
                return;
            }
        }
        if (type == Material.SNOW) {
            Material targetId;
            if (wcfg.disableSnowFormation) {
                event.setCancelled(true);
                return;
            }
            if (!wcfg.allowedSnowFallOver.isEmpty() && !wcfg.allowedSnowFallOver.contains(BukkitAdapter.asBlockType((Material)(targetId = event.getBlock().getRelative(0, -1, 0).getType())).getId())) {
                event.setCancelled(true);
                return;
            }
            if (wcfg.useRegions && !StateFlag.test(WorldGuard.getInstance().getPlatform().getRegionContainer().createQuery().queryState(BukkitAdapter.adapt((Location)event.getBlock().getLocation()), (RegionAssociable)null, Flags.SNOW_FALL))) {
                event.setCancelled(true);
                return;
            }
        }
        if (Materials.isUnwaxedCopper(event.getBlock().getType())) {
            if (wcfg.disableCopperBlockFade) {
                event.setCancelled(true);
                return;
            }
            if (wcfg.useRegions && !StateFlag.test(WorldGuard.getInstance().getPlatform().getRegionContainer().createQuery().queryState(BukkitAdapter.adapt((Location)event.getBlock().getLocation()), (RegionAssociable)null, Flags.COPPER_FADE))) {
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler(priority=EventPriority.HIGH, ignoreCancelled=true)
    public void onBlockSpread(BlockSpreadEvent event) {
        BukkitConfigurationManager cfg = WorldGuardBlockListener.getConfig();
        if (cfg.activityHaltToggle) {
            event.setCancelled(true);
            return;
        }
        BukkitWorldConfiguration wcfg = WorldGuardBlockListener.getWorldConfig(event.getBlock().getWorld());
        Material newType = event.getNewState().getType();
        if (Materials.isMushroom(newType)) {
            if (wcfg.disableMushroomSpread) {
                event.setCancelled(true);
                return;
            }
            if (wcfg.useRegions && !StateFlag.test(WorldGuard.getInstance().getPlatform().getRegionContainer().createQuery().queryState(BukkitAdapter.adapt((Location)event.getBlock().getLocation()), (RegionAssociable)null, Flags.MUSHROOMS))) {
                event.setCancelled(true);
                return;
            }
        }
        if (newType == Material.GRASS_BLOCK) {
            if (wcfg.disableGrassGrowth) {
                event.setCancelled(true);
                return;
            }
            if (wcfg.useRegions && !StateFlag.test(WorldGuard.getInstance().getPlatform().getRegionContainer().createQuery().queryState(BukkitAdapter.adapt((Location)event.getBlock().getLocation()), (RegionAssociable)null, Flags.GRASS_SPREAD))) {
                event.setCancelled(true);
                return;
            }
        }
        if (newType == Material.MYCELIUM) {
            if (wcfg.disableMyceliumSpread) {
                event.setCancelled(true);
                return;
            }
            if (wcfg.useRegions && !StateFlag.test(WorldGuard.getInstance().getPlatform().getRegionContainer().createQuery().queryState(BukkitAdapter.adapt((Location)event.getBlock().getLocation()), (RegionAssociable)null, Flags.MYCELIUM_SPREAD))) {
                event.setCancelled(true);
                return;
            }
        }
        if (Materials.isVine(newType)) {
            if (wcfg.disableVineGrowth) {
                event.setCancelled(true);
                return;
            }
            if (wcfg.useRegions && !StateFlag.test(WorldGuard.getInstance().getPlatform().getRegionContainer().createQuery().queryState(BukkitAdapter.adapt((Location)event.getBlock().getLocation()), (RegionAssociable)null, Flags.VINE_GROWTH))) {
                event.setCancelled(true);
                return;
            }
        }
        if (Materials.isAmethystGrowth(newType) || newType == Material.POINTED_DRIPSTONE) {
            if (wcfg.disableRockGrowth) {
                event.setCancelled(true);
                return;
            }
            if (wcfg.useRegions && !StateFlag.test(WorldGuard.getInstance().getPlatform().getRegionContainer().createQuery().queryState(BukkitAdapter.adapt((Location)event.getBlock().getLocation()), (RegionAssociable)null, Flags.ROCK_GROWTH))) {
                event.setCancelled(true);
                return;
            }
        }
        if (Materials.isSculkGrowth(newType)) {
            if (wcfg.disableSculkGrowth) {
                event.setCancelled(true);
                return;
            }
            if (wcfg.useRegions && !StateFlag.test(WorldGuard.getInstance().getPlatform().getRegionContainer().createQuery().queryState(BukkitAdapter.adapt((Location)event.getBlock().getLocation()), (RegionAssociable)null, Flags.SCULK_GROWTH))) {
                event.setCancelled(true);
                return;
            }
        }
        this.handleGrow((Cancellable)event, event.getBlock().getLocation(), newType);
    }

    @EventHandler(priority=EventPriority.HIGH, ignoreCancelled=true)
    public void onBlockGrow(BlockGrowEvent event) {
        Location loc = event.getBlock().getLocation();
        Material type = event.getNewState().getType();
        this.handleGrow((Cancellable)event, loc, type);
    }

    private void handleGrow(Cancellable event, Location loc, Material type) {
        BukkitWorldConfiguration wcfg = WorldGuardBlockListener.getWorldConfig(loc.getWorld());
        if (Materials.isCrop(type)) {
            if (wcfg.disableCropGrowth) {
                event.setCancelled(true);
                return;
            }
            if (wcfg.useRegions && !StateFlag.test(WorldGuard.getInstance().getPlatform().getRegionContainer().createQuery().queryState(BukkitAdapter.adapt((Location)loc), (RegionAssociable)null, Flags.CROP_GROWTH))) {
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler(priority=EventPriority.HIGH, ignoreCancelled=true)
    public void onBlockFade(BlockFadeEvent event) {
        BukkitWorldConfiguration wcfg = WorldGuardBlockListener.getWorldConfig(event.getBlock().getWorld());
        if (event.getBlock().getType() == Material.ICE) {
            if (wcfg.disableIceMelting) {
                event.setCancelled(true);
                return;
            }
            if (wcfg.useRegions && !StateFlag.test(WorldGuard.getInstance().getPlatform().getRegionContainer().createQuery().queryState(BukkitAdapter.adapt((Location)event.getBlock().getLocation()), (RegionAssociable)null, Flags.ICE_MELT))) {
                event.setCancelled(true);
                return;
            }
        } else if (event.getBlock().getType() == Material.FROSTED_ICE) {
            if (wcfg.useRegions && !StateFlag.test(WorldGuard.getInstance().getPlatform().getRegionContainer().createQuery().queryState(BukkitAdapter.adapt((Location)event.getBlock().getLocation()), (RegionAssociable)null, Flags.FROSTED_ICE_MELT))) {
                event.setCancelled(true);
                return;
            }
        } else if (event.getBlock().getType() == Material.SNOW) {
            if (wcfg.disableSnowMelting) {
                event.setCancelled(true);
                return;
            }
            if (wcfg.useRegions && !StateFlag.test(WorldGuard.getInstance().getPlatform().getRegionContainer().createQuery().queryState(BukkitAdapter.adapt((Location)event.getBlock().getLocation()), (RegionAssociable)null, Flags.SNOW_MELT))) {
                event.setCancelled(true);
                return;
            }
        } else if (event.getBlock().getType() == Material.FARMLAND) {
            if (wcfg.disableSoilDehydration) {
                event.setCancelled(true);
                return;
            }
            if (wcfg.useRegions && !StateFlag.test(WorldGuard.getInstance().getPlatform().getRegionContainer().createQuery().queryState(BukkitAdapter.adapt((Location)event.getBlock().getLocation()), (RegionAssociable)null, Flags.SOIL_DRY))) {
                event.setCancelled(true);
                return;
            }
        } else if (Materials.isCoral(event.getBlock().getType())) {
            if (wcfg.disableCoralBlockFade) {
                event.setCancelled(true);
                return;
            }
            if (wcfg.useRegions && !StateFlag.test(WorldGuard.getInstance().getPlatform().getRegionContainer().createQuery().queryState(BukkitAdapter.adapt((Location)event.getBlock().getLocation()), (RegionAssociable)null, Flags.CORAL_FADE))) {
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler(ignoreCancelled=true)
    public void onBlockExplode(BlockExplodeEvent event) {
        BukkitConfigurationManager cfg = WorldGuardBlockListener.getConfig();
        if (cfg.activityHaltToggle) {
            event.setCancelled(true);
            return;
        }
        BukkitWorldConfiguration wcfg = WorldGuardBlockListener.getWorldConfig(event.getBlock().getWorld());
        if (wcfg.blockOtherExplosions) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled=true)
    public void onMoistureChange(MoistureChangeEvent event) {
        BukkitWorldConfiguration wcfg = WorldGuardBlockListener.getWorldConfig(event.getBlock().getWorld());
        if (wcfg.disableSoilMoistureChange) {
            event.setCancelled(true);
            return;
        }
        if (wcfg.useRegions && !StateFlag.test(WorldGuard.getInstance().getPlatform().getRegionContainer().createQuery().queryState(BukkitAdapter.adapt((Location)event.getBlock().getLocation()), (RegionAssociable)null, Flags.MOISTURE_CHANGE))) {
            event.setCancelled(true);
        }
    }
}

