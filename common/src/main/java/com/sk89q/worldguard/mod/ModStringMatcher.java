package com.sk89q.worldguard.mod;

import com.sk89q.minecraft.util.commands.CommandException;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.extension.platform.Actor;
import com.sk89q.worldedit.extension.platform.Capability;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.internal.platform.StringMatcher;

import java.util.ArrayList;
import java.util.List;

public class ModStringMatcher implements StringMatcher {
    @Override
    public World matchWorld(Actor sender, String filter) throws CommandException {
        List<? extends World> worlds = WorldEdit.getInstance().getPlatformManager().queryCapability(Capability.GAME_HOOKS).getWorlds();
        for (World world : worlds) {
            if (world.getName().equalsIgnoreCase(filter)) {
                return world;
            }
        }
        throw new CommandException("No world found by name: " + filter);
    }

    @Override
    public List<LocalPlayer> matchPlayerNames(String filter) {
        List<LocalPlayer> matched = new ArrayList<>();
        if (WorldGuardModCommon.getServer() != null && WorldGuardModCommon.getAdapter() != null) {
            WorldGuardModCommon.getServer().getPlayerList().getPlayers().forEach(player -> {
                if (player.getName().getString().toLowerCase().startsWith(filter.toLowerCase())) {
                    matched.add(WorldGuardModCommon.getAdapter().adaptPlayer(player));
                }
            });
        }
        return matched;
    }

    @Override
    public Iterable<? extends LocalPlayer> matchPlayers(Actor source, String filter) throws CommandException {
        return matchPlayerNames(filter);
    }

    @Override
    public Actor matchPlayerOrConsole(Actor sender, String filter) throws CommandException {
        if (filter.equalsIgnoreCase("#console") || filter.equalsIgnoreCase("!")) {
            if (WorldGuardModCommon.getServer() != null && WorldGuardModCommon.getAdapter() != null) {
                return WorldGuardModCommon.getAdapter().adaptCommandSource(WorldGuardModCommon.getServer().createCommandSourceStack());
            }
            throw new CommandException("Server console is not available.");
        }
        List<LocalPlayer> matched = matchPlayerNames(filter);
        if (!matched.isEmpty()) {
            return matched.get(0);
        }
        throw new CommandException("Player not found: " + filter);
    }

    @Override
    public World getWorldByName(String worldName) {
        List<? extends World> worlds = WorldEdit.getInstance().getPlatformManager().queryCapability(Capability.GAME_HOOKS).getWorlds();
        for (World world : worlds) {
            if (world.getName().equalsIgnoreCase(worldName)) {
                return world;
            }
        }
        return null;
    }

    @Override
    public String replaceMacros(Actor sender, String message) {
        message = message.replace("%name%", sender.getName());
        message = message.replace("%id%", sender.getUniqueId().toString());
        return message;
    }
}
