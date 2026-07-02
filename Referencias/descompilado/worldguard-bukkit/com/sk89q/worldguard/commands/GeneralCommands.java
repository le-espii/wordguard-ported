/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.sk89q.minecraft.util.commands.Command
 *  com.sk89q.minecraft.util.commands.CommandContext
 *  com.sk89q.minecraft.util.commands.CommandException
 *  com.sk89q.minecraft.util.commands.CommandPermissions
 *  com.sk89q.worldedit.extension.platform.Actor
 *  com.sk89q.worldedit.extent.Extent
 *  com.sk89q.worldedit.util.Location
 *  com.sk89q.worldedit.util.auth.AuthorizationException
 */
package com.sk89q.worldguard.commands;

import com.google.common.collect.Lists;
import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandException;
import com.sk89q.minecraft.util.commands.CommandPermissions;
import com.sk89q.worldedit.extension.platform.Actor;
import com.sk89q.worldedit.extent.Extent;
import com.sk89q.worldedit.util.Location;
import com.sk89q.worldedit.util.auth.AuthorizationException;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.session.Session;
import com.sk89q.worldguard.session.handler.GodMode;

public class GeneralCommands {
    private final WorldGuard worldGuard;

    public GeneralCommands(WorldGuard worldGuard) {
        this.worldGuard = worldGuard;
    }

    @Command(aliases={"god"}, usage="[player]", desc="Enable godmode on a player", flags="s", max=1)
    public void god(CommandContext args, Actor sender) throws CommandException, AuthorizationException {
        Iterable<LocalPlayer> targets = null;
        boolean included = false;
        if (args.argsLength() == 0) {
            targets = this.worldGuard.getPlatform().getMatcher().matchPlayers(this.worldGuard.checkPlayer(sender));
            sender.checkPermission("worldguard.god");
        } else {
            targets = this.worldGuard.getPlatform().getMatcher().matchPlayers(sender, args.getString(0));
            sender.checkPermission("worldguard.god.other");
        }
        for (LocalPlayer player : targets) {
            Session session;
            if (!GodMode.set(player, session = WorldGuard.getInstance().getPlatform().getSessionManager().get(player), true)) continue;
            player.setFireTicks(0);
            if (player.equals(sender)) {
                player.print("God mode enabled! Use /ungod to disable.");
                included = true;
                continue;
            }
            if (args.hasFlag('s')) continue;
            player.print("God enabled by " + sender.getDisplayName() + ".");
        }
        if (!included) {
            sender.print("Players now have god mode.");
        }
    }

    @Command(aliases={"ungod"}, usage="[player]", desc="Disable godmode on a player", flags="s", max=1)
    public void ungod(CommandContext args, Actor sender) throws CommandException, AuthorizationException {
        Iterable<LocalPlayer> targets;
        boolean included = false;
        if (args.argsLength() == 0) {
            targets = this.worldGuard.getPlatform().getMatcher().matchPlayers(this.worldGuard.checkPlayer(sender));
            sender.checkPermission("worldguard.god");
        } else {
            targets = this.worldGuard.getPlatform().getMatcher().matchPlayers(sender, args.getString(0));
            sender.checkPermission("worldguard.god.other");
        }
        for (LocalPlayer player : targets) {
            Session session;
            if (!GodMode.set(player, session = WorldGuard.getInstance().getPlatform().getSessionManager().get(player), false)) continue;
            if (player.equals(sender)) {
                player.print("God mode disabled!");
                included = true;
                continue;
            }
            if (args.hasFlag('s')) continue;
            player.print("God disabled by " + sender.getDisplayName() + ".");
        }
        if (!included) {
            sender.print("Players no longer have god mode.");
        }
    }

    @Command(aliases={"heal"}, usage="[player]", desc="Heal a player", flags="s", max=1)
    public void heal(CommandContext args, Actor sender) throws CommandException, AuthorizationException {
        Iterable<LocalPlayer> targets = null;
        boolean included = false;
        if (args.argsLength() == 0) {
            targets = this.worldGuard.getPlatform().getMatcher().matchPlayers(this.worldGuard.checkPlayer(sender));
            sender.checkPermission("worldguard.heal");
        } else if (args.argsLength() == 1) {
            targets = this.worldGuard.getPlatform().getMatcher().matchPlayers(sender, args.getString(0));
            sender.checkPermission("worldguard.heal.other");
        }
        for (LocalPlayer player : targets) {
            player.setHealth(player.getMaxHealth());
            player.setFoodLevel(20.0);
            player.setSaturation(20.0);
            player.setExhaustion(0.0f);
            if (player.equals(sender)) {
                player.print("Healed!");
                included = true;
                continue;
            }
            if (args.hasFlag('s')) continue;
            player.print("Healed by " + sender.getDisplayName() + ".");
        }
        if (!included) {
            sender.print("Players healed.");
        }
    }

    @Command(aliases={"slay"}, usage="[player]", desc="Slay a player", flags="s", max=1)
    public void slay(CommandContext args, Actor sender) throws CommandException, AuthorizationException {
        Iterable<Object> targets = Lists.newArrayList();
        boolean included = false;
        if (args.argsLength() == 0) {
            targets = this.worldGuard.getPlatform().getMatcher().matchPlayers(this.worldGuard.checkPlayer(sender));
            sender.checkPermission("worldguard.slay");
        } else if (args.argsLength() == 1) {
            targets = this.worldGuard.getPlatform().getMatcher().matchPlayers(sender, args.getString(0));
            sender.checkPermission("worldguard.slay.other");
        }
        for (LocalPlayer player : targets) {
            player.setHealth(0.0);
            if (player.equals(sender)) {
                player.print("Slain!");
                included = true;
                continue;
            }
            if (args.hasFlag('s')) continue;
            player.print("Slain by " + sender.getDisplayName() + ".");
        }
        if (!included) {
            sender.print("Players slain.");
        }
    }

    @Command(aliases={"locate"}, usage="[player]", desc="Locate a player", max=1)
    @CommandPermissions(value={"worldguard.locate"})
    public void locate(CommandContext args, Actor sender) throws CommandException {
        LocalPlayer player = this.worldGuard.checkPlayer(sender);
        if (args.argsLength() == 0) {
            player.setCompassTarget(new Location((Extent)player.getWorld(), player.getWorld().getSpawnPosition().toVector3()));
            sender.print("Compass reset to spawn.");
        } else {
            LocalPlayer target = this.worldGuard.getPlatform().getMatcher().matchSinglePlayer(sender, args.getString(0));
            player.setCompassTarget(target.getLocation());
            sender.print("Compass repointed.");
        }
    }

    @Command(aliases={"stack", ";"}, usage="", desc="Stack items", max=0)
    @CommandPermissions(value={"worldguard.stack"})
    public void stack(CommandContext args, Actor sender) throws CommandException {
        LocalPlayer player = this.worldGuard.checkPlayer(sender);
        WorldGuard.getInstance().getPlatform().stackPlayerInventory(player);
        player.print("Items compacted into stacks!");
    }
}

