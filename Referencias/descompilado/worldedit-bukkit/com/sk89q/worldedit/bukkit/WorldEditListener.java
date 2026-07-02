/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.block.Block
 *  org.bukkit.command.CommandSender
 *  org.bukkit.event.Event$Result
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.EventPriority
 *  org.bukkit.event.Listener
 *  org.bukkit.event.block.Action
 *  org.bukkit.event.player.PlayerCommandSendEvent
 *  org.bukkit.event.player.PlayerGameModeChangeEvent
 *  org.bukkit.event.player.PlayerInteractEvent
 *  org.bukkit.event.player.PlayerQuitEvent
 *  org.bukkit.inventory.EquipmentSlot
 */
package com.sk89q.worldedit.bukkit;

import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.bukkit.BukkitPlayer;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.event.platform.SessionIdleEvent;
import com.sk89q.worldedit.extension.platform.Actor;
import com.sk89q.worldedit.extent.Extent;
import com.sk89q.worldedit.internal.event.InteractionDebouncer;
import com.sk89q.worldedit.util.Direction;
import com.sk89q.worldedit.util.Location;
import com.sk89q.worldedit.world.World;
import java.util.Optional;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerCommandSendEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.enginehub.piston.CommandManager;
import org.enginehub.piston.inject.Key;
import org.enginehub.piston.inject.MapBackedValueStore;

public class WorldEditListener
implements Listener {
    private final WorldEditPlugin plugin;
    private final InteractionDebouncer debouncer;

    public WorldEditListener(WorldEditPlugin plugin) {
        this.plugin = plugin;
        this.debouncer = new InteractionDebouncer(plugin.getInternalPlatform());
    }

    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
    public void onGamemode(PlayerGameModeChangeEvent event) {
        if (!this.plugin.getInternalPlatform().isHookingEvents()) {
            return;
        }
        WorldEdit.getInstance().getSessionManager().get(this.plugin.wrapPlayer(event.getPlayer()));
    }

    @EventHandler(priority=EventPriority.LOW, ignoreCancelled=true)
    public void onPlayerCommandSend(PlayerCommandSendEvent event) {
        MapBackedValueStore store = MapBackedValueStore.create();
        store.injectValue(Key.of(Actor.class), context -> Optional.of(this.plugin.wrapCommandSender((CommandSender)event.getPlayer())));
        CommandManager commandManager = this.plugin.getWorldEdit().getPlatformManager().getPlatformCommandManager().getCommandManager();
        event.getCommands().removeIf(name -> commandManager.getCommand((String)name).filter(command -> !command.getCondition().satisfied(store)).isPresent());
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Optional<Boolean> previousResult;
        if (!this.plugin.getInternalPlatform().isHookingEvents() || event.useItemInHand() == Event.Result.DENY || event.getHand() == EquipmentSlot.OFF_HAND || event.getAction() == Action.PHYSICAL) {
            return;
        }
        BukkitPlayer player = this.plugin.wrapPlayer(event.getPlayer());
        if (event.getAction() != Action.LEFT_CLICK_BLOCK && (previousResult = this.debouncer.getDuplicateInteractionResult(player)).isPresent()) {
            if (previousResult.get().booleanValue()) {
                event.setCancelled(true);
            }
            return;
        }
        World world = player.getWorld();
        WorldEdit we = this.plugin.getWorldEdit();
        Direction direction = BukkitAdapter.adapt(event.getBlockFace());
        Block clickedBlock = event.getClickedBlock();
        Location pos = clickedBlock == null ? null : new Location((Extent)world, clickedBlock.getX(), (double)clickedBlock.getY(), (double)clickedBlock.getZ());
        boolean result = false;
        switch (event.getAction()) {
            case LEFT_CLICK_BLOCK: {
                result = we.handleBlockLeftClick(player, pos, direction) || we.handleArmSwing(player);
                break;
            }
            case LEFT_CLICK_AIR: {
                result = we.handleArmSwing(player);
                break;
            }
            case RIGHT_CLICK_BLOCK: {
                result = we.handleBlockRightClick(player, pos, direction) || we.handleRightClick(player);
                break;
            }
            case RIGHT_CLICK_AIR: {
                result = we.handleRightClick(player);
                break;
            }
        }
        this.debouncer.setLastInteraction(player, result);
        if (result) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        this.debouncer.clearInteraction(this.plugin.wrapPlayer(event.getPlayer()));
        this.plugin.getWorldEdit().getEventBus().post(new SessionIdleEvent(new BukkitPlayer.SessionKeyImpl(event.getPlayer())));
    }
}

