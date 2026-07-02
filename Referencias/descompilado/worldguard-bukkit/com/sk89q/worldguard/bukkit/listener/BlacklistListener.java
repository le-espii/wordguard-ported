/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sk89q.worldedit.bukkit.BukkitAdapter
 *  org.bukkit.Location
 *  org.bukkit.Material
 *  org.bukkit.World
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.HumanEntity
 *  org.bukkit.entity.Item
 *  org.bukkit.entity.Player
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.block.BlockDispenseArmorEvent
 *  org.bukkit.event.block.BlockDispenseEvent
 *  org.bukkit.event.inventory.ClickType
 *  org.bukkit.event.inventory.InventoryAction
 *  org.bukkit.event.inventory.InventoryClickEvent
 *  org.bukkit.event.inventory.InventoryCreativeEvent
 *  org.bukkit.event.inventory.InventoryDragEvent
 *  org.bukkit.event.inventory.InventoryType
 *  org.bukkit.event.inventory.InventoryType$SlotType
 *  org.bukkit.event.player.PlayerDropItemEvent
 *  org.bukkit.event.player.PlayerItemHeldEvent
 *  org.bukkit.inventory.Inventory
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.inventory.PlayerInventory
 */
package com.sk89q.worldguard.bukkit.listener;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.blacklist.event.BlockBreakBlacklistEvent;
import com.sk89q.worldguard.blacklist.event.BlockDispenseBlacklistEvent;
import com.sk89q.worldguard.blacklist.event.BlockInteractBlacklistEvent;
import com.sk89q.worldguard.blacklist.event.BlockPlaceBlacklistEvent;
import com.sk89q.worldguard.blacklist.event.ItemAcquireBlacklistEvent;
import com.sk89q.worldguard.blacklist.event.ItemDestroyWithBlacklistEvent;
import com.sk89q.worldguard.blacklist.event.ItemDropBlacklistEvent;
import com.sk89q.worldguard.blacklist.event.ItemEquipBlacklistEvent;
import com.sk89q.worldguard.blacklist.event.ItemUseBlacklistEvent;
import com.sk89q.worldguard.bukkit.BukkitUtil;
import com.sk89q.worldguard.bukkit.BukkitWorldConfiguration;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.bukkit.event.block.BreakBlockEvent;
import com.sk89q.worldguard.bukkit.event.block.PlaceBlockEvent;
import com.sk89q.worldguard.bukkit.event.block.UseBlockEvent;
import com.sk89q.worldguard.bukkit.event.entity.DestroyEntityEvent;
import com.sk89q.worldguard.bukkit.event.entity.SpawnEntityEvent;
import com.sk89q.worldguard.bukkit.event.inventory.UseItemEvent;
import com.sk89q.worldguard.bukkit.listener.AbstractListener;
import com.sk89q.worldguard.bukkit.util.Materials;
import com.sk89q.worldguard.config.ConfigurationManager;
import com.sk89q.worldguard.config.WorldConfiguration;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockDispenseArmorEvent;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCreativeEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class BlacklistListener
extends AbstractListener {
    public BlacklistListener(WorldGuardPlugin plugin) {
        super(plugin);
    }

    @EventHandler(ignoreCancelled=true)
    public void onBreakBlock(BreakBlockEvent event) {
        BukkitWorldConfiguration wcfg = BlacklistListener.getWorldConfig(event.getWorld());
        if (wcfg.getBlacklist() == null) {
            return;
        }
        Player player = event.getCause().getFirstPlayer();
        if (player == null) {
            return;
        }
        LocalPlayer localPlayer = BlacklistListener.getPlugin().wrapPlayer(player);
        event.filter(target -> {
            if (!wcfg.getBlacklist().check(new BlockBreakBlacklistEvent(localPlayer, BukkitAdapter.asBlockVector((Location)target), BukkitUtil.createTarget(target.getBlock())), false, false)) {
                return false;
            }
            return wcfg.getBlacklist().check(new ItemDestroyWithBlacklistEvent(localPlayer, BukkitAdapter.asBlockVector((Location)target), BukkitUtil.createTarget(player.getInventory().getItemInMainHand())), false, false);
        });
    }

    @EventHandler(ignoreCancelled=true)
    public void onPlaceBlock(PlaceBlockEvent event) {
        BukkitWorldConfiguration wcfg = BlacklistListener.getWorldConfig(event.getWorld());
        if (wcfg.getBlacklist() == null) {
            return;
        }
        Player player = event.getCause().getFirstPlayer();
        if (player == null) {
            return;
        }
        LocalPlayer localPlayer = BlacklistListener.getPlugin().wrapPlayer(player);
        event.filter(target -> wcfg.getBlacklist().check(new BlockPlaceBlacklistEvent(localPlayer, BukkitAdapter.asBlockVector((Location)target), BukkitUtil.createTarget(target.getBlock())), false, false));
    }

    @EventHandler(ignoreCancelled=true)
    public void onUseBlock(UseBlockEvent event) {
        BukkitWorldConfiguration wcfg = BlacklistListener.getWorldConfig(event.getWorld());
        if (wcfg.getBlacklist() == null) {
            return;
        }
        Player player = event.getCause().getFirstPlayer();
        if (player == null) {
            return;
        }
        LocalPlayer localPlayer = BlacklistListener.getPlugin().wrapPlayer(player);
        event.filter(target -> wcfg.getBlacklist().check(new BlockInteractBlacklistEvent(localPlayer, BukkitAdapter.asBlockVector((Location)target), BukkitUtil.createTarget(target.getBlock())), false, false));
    }

    @EventHandler(ignoreCancelled=true)
    public void onSpawnEntity(SpawnEntityEvent event) {
        BukkitWorldConfiguration wcfg = BlacklistListener.getWorldConfig(event.getWorld());
        if (wcfg.getBlacklist() == null) {
            return;
        }
        Player player = event.getCause().getFirstPlayer();
        if (player == null) {
            return;
        }
        LocalPlayer localPlayer = BlacklistListener.getPlugin().wrapPlayer(player);
        Material material = Materials.getRelatedMaterial(event.getEffectiveType());
        if (material != null && !wcfg.getBlacklist().check(new ItemUseBlacklistEvent(localPlayer, BukkitAdapter.asBlockVector((Location)event.getTarget()), BukkitUtil.createTarget(material)), false, false)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled=true)
    public void onDestroyEntity(DestroyEntityEvent event) {
        Material material;
        BukkitWorldConfiguration wcfg = BlacklistListener.getWorldConfig(event.getWorld());
        if (wcfg.getBlacklist() == null) {
            return;
        }
        Player player = event.getCause().getFirstPlayer();
        if (player == null) {
            return;
        }
        LocalPlayer localPlayer = BlacklistListener.getPlugin().wrapPlayer(player);
        Entity target = event.getEntity();
        if (target instanceof Item) {
            Item item = (Item)target;
            if (!wcfg.getBlacklist().check(new ItemAcquireBlacklistEvent(localPlayer, BukkitAdapter.asBlockVector((Location)target.getLocation()), BukkitUtil.createTarget(item.getItemStack())), false, true)) {
                event.setCancelled(true);
                return;
            }
        }
        if ((material = Materials.getRelatedMaterial(target.getType())) != null && !wcfg.getBlacklist().check(new BlockBreakBlacklistEvent(localPlayer, BukkitAdapter.asBlockVector((Location)event.getTarget()), BukkitUtil.createTarget(material)), false, false)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled=true)
    public void onUseItem(UseItemEvent event) {
        BukkitWorldConfiguration wcfg = BlacklistListener.getWorldConfig(event.getWorld());
        if (wcfg.getBlacklist() == null) {
            return;
        }
        Player player = event.getCause().getFirstPlayer();
        if (player == null) {
            return;
        }
        LocalPlayer localPlayer = BlacklistListener.getPlugin().wrapPlayer(player);
        ItemStack target = event.getItemStack();
        if (!wcfg.getBlacklist().check(new ItemUseBlacklistEvent(localPlayer, BukkitAdapter.asBlockVector((Location)player.getLocation()), BukkitUtil.createTarget(target)), false, false)) {
            event.setCancelled(true);
            return;
        }
        if (Materials.isArmor(target.getType()) && !wcfg.getBlacklist().check(new ItemEquipBlacklistEvent(localPlayer, BukkitAdapter.asBlockVector((Location)player.getLocation()), BukkitUtil.createTarget(target)), false, false)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled=true)
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        BukkitWorldConfiguration wcfg = BlacklistListener.getWorldConfig(event.getPlayer().getWorld());
        if (wcfg.getBlacklist() != null) {
            Item ci = event.getItemDrop();
            if (!wcfg.getBlacklist().check(new ItemDropBlacklistEvent(BlacklistListener.getPlugin().wrapPlayer(event.getPlayer()), BukkitAdapter.asBlockVector((Location)ci.getLocation()), BukkitUtil.createTarget(ci.getItemStack())), false, false)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(ignoreCancelled=true)
    public void onBlockDispense(BlockDispenseEvent event) {
        BukkitWorldConfiguration wcfg = BlacklistListener.getWorldConfig(event.getBlock().getWorld());
        if (wcfg.getBlacklist() != null && !wcfg.getBlacklist().check(new BlockDispenseBlacklistEvent(null, BukkitAdapter.asBlockVector((Location)event.getBlock().getLocation()), BukkitUtil.createTarget(event.getItem())), false, false)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled=true)
    public void onInventoryClick(InventoryClickEvent event) {
        HumanEntity entity = event.getWhoClicked();
        if (!(entity instanceof Player)) {
            return;
        }
        Inventory inventory = event.getInventory();
        ItemStack item = event.getCurrentItem();
        if (item != null && inventory.getHolder() != null) {
            ItemStack equipped;
            Player player = (Player)entity;
            BukkitWorldConfiguration wcfg = BlacklistListener.getWorldConfig(player.getWorld());
            LocalPlayer localPlayer = BlacklistListener.getPlugin().wrapPlayer(player);
            if (wcfg.getBlacklist() != null && !wcfg.getBlacklist().check(new ItemAcquireBlacklistEvent(localPlayer, BukkitAdapter.asBlockVector((Location)entity.getLocation()), BukkitUtil.createTarget(item)), false, false)) {
                event.setCancelled(true);
                if (inventory.getHolder().equals((Object)player)) {
                    event.setCurrentItem(null);
                }
            }
            if ((equipped = this.checkEquipped(event)) != null && wcfg.getBlacklist() != null && !wcfg.getBlacklist().check(new ItemEquipBlacklistEvent(localPlayer, BukkitAdapter.asBlockVector((Location)player.getLocation()), BukkitUtil.createTarget(equipped)), false, false)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(ignoreCancelled=true)
    public void onInventoryDrag(InventoryDragEvent event) {
        HumanEntity entity = event.getWhoClicked();
        if (!(entity instanceof Player)) {
            return;
        }
        if (event.getInventory().getType() != InventoryType.PLAYER && event.getInventory().getType() != InventoryType.CRAFTING) {
            return;
        }
        if (event.getRawSlots().stream().anyMatch(i -> i >= 5 && i <= 8)) {
            Player player = (Player)entity;
            ConfigurationManager cfg = WorldGuard.getInstance().getPlatform().getGlobalStateManager();
            WorldConfiguration wcfg = cfg.get(BukkitAdapter.adapt((World)entity.getWorld()));
            LocalPlayer localPlayer = BlacklistListener.getPlugin().wrapPlayer(player);
            if (wcfg.getBlacklist() != null && !wcfg.getBlacklist().check(new ItemEquipBlacklistEvent(localPlayer, BukkitAdapter.asBlockVector((Location)player.getLocation()), BukkitUtil.createTarget(event.getOldCursor())), false, false)) {
                event.setCancelled(true);
            }
        }
    }

    private ItemStack checkEquipped(InventoryClickEvent event) {
        Inventory clickedInventory = event.getClickedInventory();
        if (event.getSlotType() == InventoryType.SlotType.ARMOR) {
            switch (event.getAction()) {
                case PLACE_ONE: 
                case PLACE_SOME: 
                case PLACE_ALL: 
                case SWAP_WITH_CURSOR: {
                    ItemStack cursor = event.getCursor();
                    if (cursor != null) {
                        return cursor;
                    }
                }
                case HOTBAR_SWAP: {
                    if (event.getClick() == ClickType.SWAP_OFFHAND) {
                        return clickedInventory == null ? null : ((PlayerInventory)clickedInventory).getItemInOffHand();
                    }
                    return clickedInventory == null ? null : clickedInventory.getItem(event.getHotbarButton());
                }
            }
        } else if (clickedInventory != null && clickedInventory.getType() == InventoryType.PLAYER && (event.getView().getTopInventory().getType() == InventoryType.PLAYER || event.getView().getTopInventory().getType() == InventoryType.CRAFTING) && event.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
            return event.getCurrentItem();
        }
        return null;
    }

    @EventHandler(ignoreCancelled=true)
    public void onInventoryCreative(InventoryCreativeEvent event) {
        HumanEntity entity = event.getWhoClicked();
        ItemStack item = event.getCursor();
        if (item.getType() != Material.AIR && entity instanceof Player) {
            Player player = (Player)entity;
            BukkitWorldConfiguration wcfg = BlacklistListener.getWorldConfig(player.getWorld());
            LocalPlayer localPlayer = BlacklistListener.getPlugin().wrapPlayer(player);
            if (wcfg.getBlacklist() != null && !wcfg.getBlacklist().check(new ItemAcquireBlacklistEvent(localPlayer, BukkitAdapter.asBlockVector((Location)entity.getLocation()), BukkitUtil.createTarget(item)), false, false)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(ignoreCancelled=true)
    public void onPlayerItemHeld(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();
        PlayerInventory inventory = player.getInventory();
        ItemStack item = inventory.getItem(event.getNewSlot());
        if (item != null) {
            BukkitWorldConfiguration wcfg = BlacklistListener.getWorldConfig(player.getWorld());
            LocalPlayer localPlayer = BlacklistListener.getPlugin().wrapPlayer(player);
            if (wcfg.getBlacklist() != null && !wcfg.getBlacklist().check(new ItemAcquireBlacklistEvent(localPlayer, BukkitAdapter.asBlockVector((Location)player.getLocation()), BukkitUtil.createTarget(item)), false, false)) {
                inventory.setItem(event.getNewSlot(), null);
            }
        }
    }

    @EventHandler(ignoreCancelled=true)
    public void onBlockDispenseArmor(BlockDispenseArmorEvent event) {
        if (!(event.getTargetEntity() instanceof Player)) {
            return;
        }
        Player player = (Player)event.getTargetEntity();
        ItemStack stack = event.getItem();
        BukkitWorldConfiguration wcfg = BlacklistListener.getWorldConfig(player.getWorld());
        LocalPlayer localPlayer = BlacklistListener.getPlugin().wrapPlayer(player);
        if (wcfg.getBlacklist() != null && !wcfg.getBlacklist().check(new ItemEquipBlacklistEvent(localPlayer, BukkitAdapter.asBlockVector((Location)player.getLocation()), BukkitUtil.createTarget(stack)), false, true)) {
            event.setCancelled(true);
        }
    }
}

