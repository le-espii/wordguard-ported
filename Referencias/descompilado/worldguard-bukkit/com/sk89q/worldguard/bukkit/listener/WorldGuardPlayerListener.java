/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sk89q.worldedit.bukkit.BukkitAdapter
 *  com.sk89q.worldedit.util.Location
 *  com.sk89q.worldedit.world.gamemode.GameMode
 *  org.bukkit.ChatColor
 *  org.bukkit.GameMode
 *  org.bukkit.Location
 *  org.bukkit.Material
 *  org.bukkit.World
 *  org.bukkit.block.Block
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.Player
 *  org.bukkit.event.Event$Result
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.EventPriority
 *  org.bukkit.event.block.Action
 *  org.bukkit.event.player.AsyncPlayerChatEvent
 *  org.bukkit.event.player.PlayerCommandPreprocessEvent
 *  org.bukkit.event.player.PlayerGameModeChangeEvent
 *  org.bukkit.event.player.PlayerInteractEvent
 *  org.bukkit.event.player.PlayerItemHeldEvent
 *  org.bukkit.event.player.PlayerJoinEvent
 *  org.bukkit.event.player.PlayerLoginEvent
 *  org.bukkit.event.player.PlayerLoginEvent$Result
 *  org.bukkit.event.player.PlayerRespawnEvent
 *  org.bukkit.event.player.PlayerTeleportEvent
 *  org.bukkit.event.player.PlayerTeleportEvent$TeleportCause
 *  org.bukkit.inventory.ItemStack
 */
package com.sk89q.worldguard.bukkit.listener;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.world.gamemode.GameMode;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.BukkitConfigurationManager;
import com.sk89q.worldguard.bukkit.BukkitWorldConfiguration;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.bukkit.event.player.ProcessPlayerEvent;
import com.sk89q.worldguard.bukkit.listener.AbstractListener;
import com.sk89q.worldguard.bukkit.listener.RegionProtectionListener;
import com.sk89q.worldguard.bukkit.util.Entities;
import com.sk89q.worldguard.bukkit.util.Events;
import com.sk89q.worldguard.bukkit.util.Materials;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import com.sk89q.worldguard.session.MoveType;
import com.sk89q.worldguard.session.Session;
import com.sk89q.worldguard.session.handler.GameModeFlag;
import com.sk89q.worldguard.util.command.CommandFilter;
import com.sk89q.worldguard.util.profile.Profile;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;

public class WorldGuardPlayerListener
extends AbstractListener {
    private static final Logger log = Logger.getLogger(WorldGuardPlayerListener.class.getCanonicalName());
    private static final Pattern opPattern = Pattern.compile("^/(?:minecraft:)?(?:bukkit:)?(?:de)?op(?:\\s.*)?$", 2);

    public WorldGuardPlayerListener(WorldGuardPlugin plugin) {
        super(plugin);
    }

    @EventHandler
    public void onPlayerGameModeChange(PlayerGameModeChangeEvent event) {
        GameModeFlag handler;
        Player player = event.getPlayer();
        LocalPlayer localPlayer = WorldGuardPlayerListener.getPlugin().wrapPlayer(player);
        BukkitWorldConfiguration wcfg = WorldGuardPlayerListener.getWorldConfig(player.getWorld());
        Session session = WorldGuard.getInstance().getPlatform().getSessionManager().getIfPresent(localPlayer);
        if (session != null && (handler = session.getHandler(GameModeFlag.class)) != null && wcfg.useRegions && !WorldGuard.getInstance().getPlatform().getSessionManager().hasBypass(localPlayer, localPlayer.getWorld())) {
            GameMode expected = handler.getSetGameMode();
            if (handler.getOriginalGameMode() != null && expected != null && expected != BukkitAdapter.adapt((org.bukkit.GameMode)event.getNewGameMode())) {
                log.info("Game mode change on " + player.getName() + " has been blocked due to the region GAMEMODE flag");
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        World world = player.getWorld();
        BukkitConfigurationManager cfg = WorldGuardPlayerListener.getConfig();
        BukkitWorldConfiguration wcfg = WorldGuardPlayerListener.getWorldConfig(world);
        if (cfg.activityHaltToggle) {
            player.sendMessage(String.valueOf(ChatColor.YELLOW) + "Intensive server activity has been HALTED.");
            int removed = 0;
            for (Entity entity : world.getEntities()) {
                if (!com.sk89q.worldguard.util.Entities.isIntensiveEntity(BukkitAdapter.adapt((Entity)entity))) continue;
                entity.remove();
                ++removed;
            }
            if (removed > 10) {
                log.info("Halt-Act: " + removed + " entities (>10) auto-removed from " + String.valueOf(player.getWorld()));
            }
        }
        if (wcfg.fireSpreadDisableToggle) {
            player.sendMessage(String.valueOf(ChatColor.YELLOW) + "Fire spread is currently globally disabled for this world.");
        }
        Events.fire(new ProcessPlayerEvent(player));
        WorldGuard.getInstance().getExecutorService().submit(() -> WorldGuard.getInstance().getProfileCache().put(new Profile(player.getUniqueId(), player.getName())));
    }

    @EventHandler(ignoreCancelled=true)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        LocalPlayer localPlayer = WorldGuardPlayerListener.getPlugin().wrapPlayer(player);
        BukkitWorldConfiguration wcfg = WorldGuardPlayerListener.getWorldConfig(player.getWorld());
        if (wcfg.useRegions) {
            RegionQuery query = WorldGuard.getInstance().getPlatform().getRegionContainer().createQuery();
            ApplicableRegionSet chatFrom = query.getApplicableRegions(localPlayer.getLocation());
            if (!chatFrom.testState(localPlayer, Flags.SEND_CHAT)) {
                String message = chatFrom.queryValue(localPlayer, Flags.DENY_MESSAGE);
                RegionProtectionListener.formatAndSendDenyMessage("chat", localPlayer, message);
                event.setCancelled(true);
                return;
            }
            boolean anyRemoved = false;
            Iterator i = event.getRecipients().iterator();
            while (i.hasNext()) {
                Player rPlayer = (Player)i.next();
                LocalPlayer rLocal = WorldGuardPlayerListener.getPlugin().wrapPlayer(rPlayer);
                if (query.testState(rLocal.getLocation(), rLocal, Flags.RECEIVE_CHAT)) continue;
                i.remove();
                anyRemoved = true;
            }
            if (anyRemoved && event.getRecipients().isEmpty() && wcfg.regionCancelEmptyChatEvents) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(ignoreCancelled=true)
    public void onPlayerLogin(PlayerLoginEvent event) {
        Player player = event.getPlayer();
        BukkitConfigurationManager cfg = WorldGuardPlayerListener.getConfig();
        String hostKey = cfg.hostKeys.get(player.getUniqueId().toString());
        if (hostKey == null) {
            hostKey = cfg.hostKeys.get(player.getName().toLowerCase());
        }
        if (hostKey != null) {
            String hostname = event.getHostname();
            int colonIndex = hostname.indexOf(58);
            if (colonIndex != -1) {
                hostname = hostname.substring(0, colonIndex);
            }
            if (!(hostname.equals(hostKey) || cfg.hostKeysAllowFMLClients && (hostname.equals(hostKey + "\u0000FML\u0000") || hostname.equals(hostKey + "\u0000FML2\u0000")))) {
                event.disallow(PlayerLoginEvent.Result.KICK_OTHER, "You did not join with the valid host key!");
                log.warning("WorldGuard host key check: " + player.getName() + " joined with '" + hostname + "' but '" + hostKey + "' was expected. Kicked!");
                return;
            }
        }
        if (cfg.deopOnJoin) {
            player.setOp(false);
        }
    }

    @EventHandler(priority=EventPriority.HIGH)
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        World world = player.getWorld();
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            this.handleBlockRightClick(event);
        } else if (event.getAction() == Action.PHYSICAL) {
            this.handlePhysicalInteract(event);
        }
        BukkitConfigurationManager cfg = WorldGuardPlayerListener.getConfig();
        BukkitWorldConfiguration wcfg = WorldGuardPlayerListener.getWorldConfig(world);
        if (wcfg.removeInfiniteStacks && !WorldGuardPlayerListener.getPlugin().hasPermission((CommandSender)player, "worldguard.override.infinite-stack")) {
            int slot = player.getInventory().getHeldItemSlot();
            ItemStack heldItem = player.getInventory().getItem(slot);
            if (heldItem != null && heldItem.getAmount() < 0) {
                player.getInventory().setItem(slot, null);
                player.sendMessage(String.valueOf(ChatColor.RED) + "Infinite stack removed.");
            }
        }
    }

    private void handleBlockRightClick(PlayerInteractEvent event) {
        if (event.useItemInHand() == Event.Result.DENY) {
            return;
        }
        Block block = event.getClickedBlock();
        World world = block.getWorld();
        Material type = block.getType();
        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        BukkitWorldConfiguration wcfg = WorldGuardPlayerListener.getWorldConfig(world);
        if (Materials.isInventoryBlock(type) && wcfg.removeInfiniteStacks && !WorldGuardPlayerListener.getPlugin().hasPermission((CommandSender)player, "worldguard.override.infinite-stack")) {
            for (int slot = 0; slot < 40; ++slot) {
                ItemStack heldItem = player.getInventory().getItem(slot);
                if (heldItem == null || heldItem.getAmount() >= 0) continue;
                player.getInventory().setItem(slot, null);
                player.sendMessage(String.valueOf(ChatColor.RED) + "Infinite stack in slot #" + slot + " removed.");
            }
        }
        if (wcfg.useRegions) {
            LocalPlayer localPlayer = WorldGuardPlayerListener.getPlugin().wrapPlayer(player);
            if (item != null && item.getType().getKey().toString().equals(wcfg.regionWand) && WorldGuardPlayerListener.getPlugin().hasPermission((CommandSender)player, "worldguard.region.wand")) {
                ApplicableRegionSet set = WorldGuard.getInstance().getPlatform().getRegionContainer().createQuery().getApplicableRegions(BukkitAdapter.adapt((Location)block.getLocation()), RegionQuery.QueryOption.SORT);
                if (set.size() > 0) {
                    player.sendMessage(String.valueOf(ChatColor.YELLOW) + "Can you build? " + (set.testState(localPlayer, Flags.BUILD) ? "Yes" : "No"));
                    StringBuilder str = new StringBuilder();
                    Iterator it = set.iterator();
                    while (it.hasNext()) {
                        str.append(((ProtectedRegion)it.next()).getId());
                        if (!it.hasNext()) continue;
                        str.append(", ");
                    }
                    localPlayer.print("Applicable regions: " + String.valueOf(str));
                } else {
                    localPlayer.print("WorldGuard: No defined regions here!");
                }
                event.setUseItemInHand(Event.Result.DENY);
            }
        }
    }

    private void handlePhysicalInteract(PlayerInteractEvent event) {
        if (event.useInteractedBlock() == Event.Result.DENY) {
            return;
        }
        Player player = event.getPlayer();
        Block block = event.getClickedBlock();
        Material type = block.getType();
        World world = player.getWorld();
        BukkitWorldConfiguration wcfg = WorldGuardPlayerListener.getWorldConfig(world);
        if (type == Material.FARMLAND && wcfg.disablePlayerCropTrampling) {
            event.setCancelled(true);
            return;
        }
        if (type == Material.TURTLE_EGG && wcfg.disablePlayerTurtleEggTrampling) {
            event.setCancelled(true);
            return;
        }
        if (type == Material.SNIFFER_EGG && wcfg.disablePlayerSnifferEggTrampling) {
            event.setCancelled(true);
            return;
        }
    }

    @EventHandler(priority=EventPriority.HIGHEST)
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        if (Entities.isNPC((Entity)player)) {
            return;
        }
        BukkitWorldConfiguration wcfg = WorldGuardPlayerListener.getWorldConfig(player.getWorld());
        if (wcfg.useRegions) {
            LocalPlayer localPlayer = WorldGuardPlayerListener.getPlugin().wrapPlayer(player);
            ApplicableRegionSet set = WorldGuard.getInstance().getPlatform().getRegionContainer().createQuery().getApplicableRegions(localPlayer.getLocation());
            com.sk89q.worldedit.util.Location spawn = set.queryValue(localPlayer, Flags.SPAWN_LOC);
            if (spawn != null) {
                event.setRespawnLocation(BukkitAdapter.adapt((com.sk89q.worldedit.util.Location)spawn));
            }
        }
    }

    @EventHandler(priority=EventPriority.HIGH)
    public void onItemHeldChange(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();
        BukkitWorldConfiguration wcfg = WorldGuardPlayerListener.getWorldConfig(player.getWorld());
        if (wcfg.removeInfiniteStacks && !WorldGuardPlayerListener.getPlugin().hasPermission((CommandSender)player, "worldguard.override.infinite-stack")) {
            int newSlot = event.getNewSlot();
            ItemStack heldItem = player.getInventory().getItem(newSlot);
            if (heldItem != null && heldItem.getAmount() < 0) {
                player.getInventory().setItem(newSlot, null);
                player.sendMessage(String.valueOf(ChatColor.RED) + "Infinite stack removed.");
            }
        }
    }

    @EventHandler(priority=EventPriority.LOW, ignoreCancelled=true)
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        if (event.getTo() == null) {
            return;
        }
        Player player = event.getPlayer();
        if (Entities.isNPC((Entity)player)) {
            return;
        }
        LocalPlayer localPlayer = WorldGuardPlayerListener.getPlugin().wrapPlayer(player);
        BukkitConfigurationManager cfg = WorldGuardPlayerListener.getConfig();
        BukkitWorldConfiguration wcfg = WorldGuardPlayerListener.getWorldConfig(player.getWorld());
        if (wcfg.useRegions && cfg.usePlayerTeleports) {
            RegionQuery query = WorldGuard.getInstance().getPlatform().getRegionContainer().createQuery();
            ApplicableRegionSet set = query.getApplicableRegions(BukkitAdapter.adapt((Location)event.getTo()));
            ApplicableRegionSet setFrom = query.getApplicableRegions(BukkitAdapter.adapt((Location)event.getFrom()));
            if (event.getCause() == PlayerTeleportEvent.TeleportCause.ENDER_PEARL) {
                if (!WorldGuard.getInstance().getPlatform().getSessionManager().hasBypass(localPlayer, localPlayer.getWorld())) {
                    boolean cancel = false;
                    String message = null;
                    if (!setFrom.testState(localPlayer, Flags.ENDERPEARL)) {
                        cancel = true;
                        message = setFrom.queryValue(localPlayer, Flags.EXIT_DENY_MESSAGE);
                    } else if (!set.testState(localPlayer, Flags.ENDERPEARL)) {
                        cancel = true;
                        message = set.queryValue(localPlayer, Flags.ENTRY_DENY_MESSAGE);
                    }
                    if (cancel) {
                        if (message != null && !message.isEmpty()) {
                            player.sendMessage(message);
                        }
                        event.setCancelled(true);
                        return;
                    }
                }
            } else if (event.getCause() == PlayerTeleportEvent.TeleportCause.CHORUS_FRUIT && !WorldGuard.getInstance().getPlatform().getSessionManager().hasBypass(localPlayer, localPlayer.getWorld())) {
                boolean cancel = false;
                String message = null;
                if (!setFrom.testState(localPlayer, Flags.CHORUS_TELEPORT)) {
                    cancel = true;
                    message = setFrom.queryValue(localPlayer, Flags.EXIT_DENY_MESSAGE);
                } else if (!set.testState(localPlayer, Flags.CHORUS_TELEPORT)) {
                    cancel = true;
                    message = set.queryValue(localPlayer, Flags.ENTRY_DENY_MESSAGE);
                }
                if (cancel) {
                    if (message != null && !message.isEmpty()) {
                        player.sendMessage(message);
                    }
                    event.setCancelled(true);
                    return;
                }
            }
            if (null != WorldGuard.getInstance().getPlatform().getSessionManager().get(localPlayer).testMoveTo(localPlayer, BukkitAdapter.adapt((Location)event.getTo()), MoveType.TELEPORT)) {
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler(priority=EventPriority.LOWEST, ignoreCancelled=true)
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        Set blockedCommands;
        ApplicableRegionSet set;
        Set allowedCommands;
        CommandFilter test;
        Player player = event.getPlayer();
        LocalPlayer localPlayer = WorldGuardPlayerListener.getPlugin().wrapPlayer(player);
        BukkitConfigurationManager cfg = WorldGuardPlayerListener.getConfig();
        BukkitWorldConfiguration wcfg = WorldGuardPlayerListener.getWorldConfig(player.getWorld());
        if (wcfg.useRegions && !WorldGuard.getInstance().getPlatform().getSessionManager().hasBypass(localPlayer, localPlayer.getWorld()) && !(test = new CommandFilter(allowedCommands = (Set)((Object)(set = WorldGuard.getInstance().getPlatform().getRegionContainer().createQuery().getApplicableRegions(localPlayer.getLocation())).queryValue(localPlayer, Flags.ALLOWED_CMDS)), blockedCommands = (Set)((Object)set.queryValue(localPlayer, Flags.BLOCKED_CMDS)))).apply(event.getMessage())) {
            String message = set.queryValue(localPlayer, Flags.DENY_MESSAGE);
            RegionProtectionListener.formatAndSendDenyMessage("use " + event.getMessage(), localPlayer, message);
            event.setCancelled(true);
            return;
        }
        if (cfg.blockInGameOp && opPattern.matcher(event.getMessage()).matches()) {
            player.sendMessage(String.valueOf(ChatColor.RED) + "/op and /deop can only be used in console (as set by a WG setting).");
            event.setCancelled(true);
            return;
        }
    }
}

