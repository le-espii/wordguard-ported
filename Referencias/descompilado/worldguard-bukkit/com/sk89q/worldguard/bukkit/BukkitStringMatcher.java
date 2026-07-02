/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sk89q.minecraft.util.commands.CommandException
 *  com.sk89q.worldedit.WorldEdit
 *  com.sk89q.worldedit.bukkit.BukkitAdapter
 *  com.sk89q.worldedit.extension.platform.Actor
 *  com.sk89q.worldedit.extension.platform.Capability
 *  com.sk89q.worldedit.math.Vector3
 *  com.sk89q.worldedit.world.World
 *  org.bukkit.Bukkit
 *  org.bukkit.World
 *  org.bukkit.World$Environment
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 */
package com.sk89q.worldguard.bukkit;

import com.sk89q.minecraft.util.commands.CommandException;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extension.platform.Actor;
import com.sk89q.worldedit.extension.platform.Capability;
import com.sk89q.worldedit.math.Vector3;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.internal.platform.StringMatcher;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BukkitStringMatcher
implements StringMatcher {
    @Override
    public World matchWorld(Actor sender, String filter) throws CommandException {
        List worlds = WorldEdit.getInstance().getPlatformManager().queryCapability(Capability.GAME_HOOKS).getWorlds();
        if (filter.charAt(0) == '#') {
            if (filter.equalsIgnoreCase("#main")) {
                return (World)worlds.get(0);
            }
            if (filter.equalsIgnoreCase("#normal")) {
                for (World world : worlds) {
                    if (BukkitAdapter.adapt((World)world).getEnvironment() != World.Environment.NORMAL) continue;
                    return world;
                }
                throw new CommandException("No normal world found.");
            }
            if (filter.equalsIgnoreCase("#nether")) {
                for (World world : worlds) {
                    if (BukkitAdapter.adapt((World)world).getEnvironment() != World.Environment.NETHER) continue;
                    return world;
                }
                throw new CommandException("No nether world found.");
            }
            if (filter.equalsIgnoreCase("#end")) {
                for (World world : worlds) {
                    if (BukkitAdapter.adapt((World)world).getEnvironment() != World.Environment.THE_END) continue;
                    return world;
                }
                throw new CommandException("No end world found.");
            }
            if (filter.matches("^#player$")) {
                String[] parts = filter.split(":", 2);
                if (parts.length == 1) {
                    throw new CommandException("Argument expected for #player.");
                }
                return this.matchPlayers(sender, parts[1]).iterator().next().getWorld();
            }
            throw new CommandException("Invalid identifier '" + filter + "'.");
        }
        for (World world : worlds) {
            if (!world.getName().equals(filter)) continue;
            return world;
        }
        throw new CommandException("No world by that exact name found.");
    }

    @Override
    public List<LocalPlayer> matchPlayerNames(String filter) {
        List wgPlayers = Bukkit.getServer().getOnlinePlayers().stream().map(player -> WorldGuardPlugin.inst().wrapPlayer((Player)player)).collect(Collectors.toList());
        if ((filter = filter.toLowerCase()).charAt(0) == '@' && filter.length() >= 2) {
            filter = filter.substring(1);
            for (LocalPlayer player2 : wgPlayers) {
                if (!player2.getName().equalsIgnoreCase(filter)) continue;
                ArrayList<LocalPlayer> list = new ArrayList<LocalPlayer>();
                list.add(player2);
                return list;
            }
            return new ArrayList<LocalPlayer>();
        }
        if (filter.charAt(0) == '*' && filter.length() >= 2) {
            filter = filter.substring(1);
            ArrayList<LocalPlayer> list = new ArrayList<LocalPlayer>();
            for (LocalPlayer player3 : wgPlayers) {
                if (!player3.getName().toLowerCase().contains(filter)) continue;
                list.add(player3);
            }
            return list;
        }
        ArrayList<LocalPlayer> list = new ArrayList<LocalPlayer>();
        for (LocalPlayer player4 : wgPlayers) {
            if (!player4.getName().toLowerCase().startsWith(filter)) continue;
            list.add(player4);
        }
        return list;
    }

    @Override
    public Iterable<? extends LocalPlayer> matchPlayers(Actor source, String filter) throws CommandException {
        if (Bukkit.getServer().getOnlinePlayers().isEmpty()) {
            throw new CommandException("No players matched query.");
        }
        List wgPlayers = Bukkit.getServer().getOnlinePlayers().stream().map(player -> WorldGuardPlugin.inst().wrapPlayer((Player)player)).collect(Collectors.toList());
        if (filter.equals("*")) {
            return this.checkPlayerMatch(wgPlayers);
        }
        if (filter.charAt(0) == '#') {
            if (filter.equalsIgnoreCase("#world")) {
                ArrayList<LocalPlayer> players = new ArrayList<LocalPlayer>();
                LocalPlayer sourcePlayer = WorldGuard.getInstance().checkPlayer(source);
                World sourceWorld = sourcePlayer.getWorld();
                for (LocalPlayer player2 : wgPlayers) {
                    if (!player2.getWorld().equals((Object)sourceWorld)) continue;
                    players.add(player2);
                }
                return this.checkPlayerMatch(players);
            }
            if (filter.equalsIgnoreCase("#near")) {
                ArrayList<LocalPlayer> players = new ArrayList<LocalPlayer>();
                LocalPlayer sourcePlayer = WorldGuard.getInstance().checkPlayer(source);
                World sourceWorld = sourcePlayer.getWorld();
                Vector3 sourceVector = sourcePlayer.getLocation().toVector();
                for (LocalPlayer player3 : wgPlayers) {
                    if (!player3.getWorld().equals((Object)sourceWorld) || !(player3.getLocation().toVector().distanceSq(sourceVector) < 900.0)) continue;
                    players.add(player3);
                }
                return this.checkPlayerMatch(players);
            }
            throw new CommandException("Invalid group '" + filter + "'.");
        }
        List<LocalPlayer> players = this.matchPlayerNames(filter);
        return this.checkPlayerMatch(players);
    }

    @Override
    public Actor matchPlayerOrConsole(Actor sender, String filter) throws CommandException {
        if (filter.equalsIgnoreCase("#console") || filter.equalsIgnoreCase("*console*") || filter.equalsIgnoreCase("!")) {
            return WorldGuardPlugin.inst().wrapCommandSender((CommandSender)Bukkit.getServer().getConsoleSender());
        }
        return this.matchSinglePlayer(sender, filter);
    }

    @Override
    public World getWorldByName(String worldName) {
        org.bukkit.World bukkitW = Bukkit.getServer().getWorld(worldName);
        if (bukkitW == null) {
            return null;
        }
        return BukkitAdapter.adapt((org.bukkit.World)bukkitW);
    }

    @Override
    public String replaceMacros(Actor sender, String message) {
        Collection online = Bukkit.getServer().getOnlinePlayers();
        message = message.replace("%name%", sender.getName());
        message = message.replace("%id%", sender.getUniqueId().toString());
        message = message.replace("%online%", String.valueOf(online.size()));
        if (sender instanceof LocalPlayer) {
            LocalPlayer player = (LocalPlayer)sender;
            World world = (World)player.getExtent();
            message = message.replace("%world%", world.getName());
            message = message.replace("%health%", String.valueOf(player.getHealth()));
        }
        return message;
    }
}

