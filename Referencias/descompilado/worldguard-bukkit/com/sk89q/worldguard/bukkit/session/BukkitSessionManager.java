/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sk89q.worldedit.world.World
 *  org.bukkit.Bukkit
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.Player
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.Listener
 */
package com.sk89q.worldguard.bukkit.session;

import com.sk89q.worldedit.world.World;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.BukkitPlayer;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.bukkit.event.player.ProcessPlayerEvent;
import com.sk89q.worldguard.bukkit.util.Entities;
import com.sk89q.worldguard.session.AbstractSessionManager;
import com.sk89q.worldguard.session.Session;
import java.util.Collection;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class BukkitSessionManager
extends AbstractSessionManager
implements Runnable,
Listener {
    @Override
    public void resetAllStates() {
        Collection players = Bukkit.getServer().getOnlinePlayers();
        for (Player player : players) {
            BukkitPlayer bukkitPlayer = new BukkitPlayer(WorldGuardPlugin.inst(), player);
            Session session = this.getIfPresent(bukkitPlayer);
            if (session == null) continue;
            session.resetState(bukkitPlayer);
        }
    }

    @EventHandler
    public void onPlayerProcess(ProcessPlayerEvent event) {
        LocalPlayer player = WorldGuardPlugin.inst().wrapPlayer(event.getPlayer());
        this.get(player).initialize(player);
    }

    @Override
    public void run() {
        for (Player player : Bukkit.getServer().getOnlinePlayers()) {
            LocalPlayer localPlayer = WorldGuardPlugin.inst().wrapPlayer(player);
            this.get(localPlayer).tick(localPlayer);
        }
    }

    @Override
    public boolean hasBypass(LocalPlayer player, World world) {
        if (player instanceof BukkitPlayer) {
            BukkitPlayer bukkitPlayer = (BukkitPlayer)player;
            if (Entities.isNPC((Entity)bukkitPlayer.getPlayer()) && WorldGuard.getInstance().getPlatform().getGlobalStateManager().get((World)world).fakePlayerBuildOverride) {
                return true;
            }
            if (!((BukkitPlayer)player).getPlayer().isOnline()) {
                return false;
            }
        }
        return super.hasBypass(player, world);
    }

    public void shutdown() {
        for (Player player : Bukkit.getServer().getOnlinePlayers()) {
            LocalPlayer localPlayer = WorldGuardPlugin.inst().wrapPlayer(player);
            this.get(localPlayer).uninitialize(localPlayer);
        }
    }
}

