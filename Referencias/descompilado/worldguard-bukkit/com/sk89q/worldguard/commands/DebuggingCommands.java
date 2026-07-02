/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sk89q.minecraft.util.commands.Command
 *  com.sk89q.minecraft.util.commands.CommandContext
 *  com.sk89q.minecraft.util.commands.CommandException
 *  com.sk89q.minecraft.util.commands.CommandPermissions
 *  com.sk89q.worldedit.extension.platform.Actor
 */
package com.sk89q.worldguard.commands;

import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandException;
import com.sk89q.minecraft.util.commands.CommandPermissions;
import com.sk89q.worldedit.extension.platform.Actor;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;

public class DebuggingCommands {
    private final WorldGuard worldGuard;

    public DebuggingCommands(WorldGuard worldGuard) {
        this.worldGuard = worldGuard;
    }

    @Command(aliases={"testbreak"}, usage="[player]", desc="Simulate a block break", min=1, max=1, flags="ts")
    @CommandPermissions(value={"worldguard.debug.event"})
    public void fireBreakEvent(CommandContext args, Actor sender) throws CommandException {
        LocalPlayer target = this.worldGuard.getPlatform().getMatcher().matchSinglePlayer(sender, args.getString(0));
        this.worldGuard.getPlatform().getDebugHandler().testBreak(sender, target, args.hasFlag('t'), args.hasFlag('s'));
    }

    @Command(aliases={"testplace"}, usage="[player]", desc="Simulate a block place", min=1, max=1, flags="ts")
    @CommandPermissions(value={"worldguard.debug.event"})
    public void firePlaceEvent(CommandContext args, Actor sender) throws CommandException {
        LocalPlayer target = this.worldGuard.getPlatform().getMatcher().matchSinglePlayer(sender, args.getString(0));
        this.worldGuard.getPlatform().getDebugHandler().testPlace(sender, target, args.hasFlag('t'), args.hasFlag('s'));
    }

    @Command(aliases={"testinteract"}, usage="[player]", desc="Simulate a block interact", min=1, max=1, flags="ts")
    @CommandPermissions(value={"worldguard.debug.event"})
    public void fireInteractEvent(CommandContext args, Actor sender) throws CommandException {
        LocalPlayer target = this.worldGuard.getPlatform().getMatcher().matchSinglePlayer(sender, args.getString(0));
        this.worldGuard.getPlatform().getDebugHandler().testInteract(sender, target, args.hasFlag('t'), args.hasFlag('s'));
    }

    @Command(aliases={"testdamage"}, usage="[player]", desc="Simulate an entity damage", min=1, max=1, flags="ts")
    @CommandPermissions(value={"worldguard.debug.event"})
    public void fireDamageEvent(CommandContext args, Actor sender) throws CommandException {
        LocalPlayer target = this.worldGuard.getPlatform().getMatcher().matchSinglePlayer(sender, args.getString(0));
        this.worldGuard.getPlatform().getDebugHandler().testDamage(sender, target, args.hasFlag('t'), args.hasFlag('s'));
    }
}

