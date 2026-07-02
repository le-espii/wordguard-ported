/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sk89q.minecraft.util.commands.Command
 *  com.sk89q.minecraft.util.commands.CommandContext
 *  com.sk89q.minecraft.util.commands.CommandException
 *  com.sk89q.minecraft.util.commands.CommandPermissions
 *  com.sk89q.worldedit.WorldEdit
 *  com.sk89q.worldedit.entity.Entity
 *  com.sk89q.worldedit.extension.platform.Actor
 *  com.sk89q.worldedit.extension.platform.Capability
 *  com.sk89q.worldedit.util.formatting.component.CodeFormat
 *  com.sk89q.worldedit.util.formatting.component.ErrorFormat
 *  com.sk89q.worldedit.util.formatting.component.LabelFormat
 *  com.sk89q.worldedit.util.formatting.text.Component
 *  com.sk89q.worldedit.util.formatting.text.TextComponent
 *  com.sk89q.worldedit.util.formatting.text.TextComponent$Builder
 *  com.sk89q.worldedit.util.formatting.text.event.ClickEvent
 *  com.sk89q.worldedit.util.formatting.text.event.ClickEvent$Action
 *  com.sk89q.worldedit.util.formatting.text.event.HoverEvent
 *  com.sk89q.worldedit.util.formatting.text.event.HoverEvent$Action
 *  com.sk89q.worldedit.util.formatting.text.format.TextColor
 *  com.sk89q.worldedit.util.formatting.text.format.TextDecoration
 *  com.sk89q.worldedit.util.formatting.text.format.TextDecoration$State
 *  com.sk89q.worldedit.world.World
 */
package com.sk89q.worldguard.commands;

import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandException;
import com.sk89q.minecraft.util.commands.CommandPermissions;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.entity.Entity;
import com.sk89q.worldedit.extension.platform.Actor;
import com.sk89q.worldedit.extension.platform.Capability;
import com.sk89q.worldedit.util.formatting.component.CodeFormat;
import com.sk89q.worldedit.util.formatting.component.ErrorFormat;
import com.sk89q.worldedit.util.formatting.component.LabelFormat;
import com.sk89q.worldedit.util.formatting.text.Component;
import com.sk89q.worldedit.util.formatting.text.TextComponent;
import com.sk89q.worldedit.util.formatting.text.event.ClickEvent;
import com.sk89q.worldedit.util.formatting.text.event.HoverEvent;
import com.sk89q.worldedit.util.formatting.text.format.TextColor;
import com.sk89q.worldedit.util.formatting.text.format.TextDecoration;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.config.ConfigurationManager;
import com.sk89q.worldguard.config.WorldConfiguration;
import com.sk89q.worldguard.util.Entities;

public class ToggleCommands {
    private final WorldGuard worldGuard;

    public ToggleCommands(WorldGuard worldGuard) {
        this.worldGuard = worldGuard;
    }

    @Command(aliases={"stopfire"}, usage="[<world>]", desc="Disables all fire spread temporarily", max=1)
    @CommandPermissions(value={"worldguard.fire-toggle.stop"})
    public void stopFire(CommandContext args, Actor sender) throws CommandException {
        World world = args.argsLength() == 0 ? this.worldGuard.checkPlayer(sender).getWorld() : this.worldGuard.getPlatform().getMatcher().matchWorld(sender, args.getString(0));
        WorldConfiguration wcfg = WorldGuard.getInstance().getPlatform().getGlobalStateManager().get(world);
        if (!wcfg.fireSpreadDisableToggle) {
            this.worldGuard.getPlatform().broadcastNotification(LabelFormat.wrap((String[])new String[]{"Fire spread has been globally disabled for '" + world.getName() + "' by " + sender.getDisplayName() + "."}));
        } else {
            sender.print("Fire spread was already globally disabled.");
        }
        wcfg.fireSpreadDisableToggle = true;
    }

    @Command(aliases={"allowfire"}, usage="[<world>]", desc="Allows all fire spread temporarily", max=1)
    @CommandPermissions(value={"worldguard.fire-toggle.stop"})
    public void allowFire(CommandContext args, Actor sender) throws CommandException {
        World world = args.argsLength() == 0 ? this.worldGuard.checkPlayer(sender).getWorld() : this.worldGuard.getPlatform().getMatcher().matchWorld(sender, args.getString(0));
        WorldConfiguration wcfg = WorldGuard.getInstance().getPlatform().getGlobalStateManager().get(world);
        if (wcfg.fireSpreadDisableToggle) {
            this.worldGuard.getPlatform().broadcastNotification(LabelFormat.wrap((String[])new String[]{"Fire spread has been globally for '" + world.getName() + "' re-enabled by " + sender.getDisplayName() + "."}));
        } else {
            sender.print("Fire spread was already globally enabled.");
        }
        wcfg.fireSpreadDisableToggle = false;
    }

    @Command(aliases={"halt-activity", "stoplag", "haltactivity"}, usage="[confirm]", desc="Attempts to cease as much activity in order to stop lag", flags="cis", max=1)
    @CommandPermissions(value={"worldguard.halt-activity"})
    public void stopLag(CommandContext args, Actor sender) throws CommandException {
        ConfigurationManager configManager = WorldGuard.getInstance().getPlatform().getGlobalStateManager();
        if (args.hasFlag('i')) {
            if (configManager.activityHaltToggle) {
                sender.print("ALL intensive server activity is not allowed.");
            } else {
                sender.print("ALL intensive server activity is allowed.");
            }
        } else {
            boolean activityHaltToggle;
            boolean bl = activityHaltToggle = !args.hasFlag('c');
            if (activityHaltToggle && (args.argsLength() == 0 || !args.getString(0).equalsIgnoreCase("confirm"))) {
                String confirmCommand = "/" + args.getCommand() + " confirm";
                TextComponent message = (TextComponent)((TextComponent.Builder)((TextComponent.Builder)((TextComponent.Builder)((TextComponent.Builder)((TextComponent.Builder)((TextComponent.Builder)((TextComponent.Builder)((TextComponent.Builder)TextComponent.builder((String)"").append((Component)ErrorFormat.wrap((String[])new String[]{"This command will "}))).append(ErrorFormat.wrap((String[])new String[]{"PERMANENTLY"}).decoration(TextDecoration.BOLD, TextDecoration.State.TRUE))).append((Component)ErrorFormat.wrap((String[])new String[]{" erase ALL animals in ALL loaded chunks in ALL loaded worlds. "}))).append((Component)TextComponent.newline())).append(((TextComponent)TextComponent.of((String)"[Click]", (TextColor)TextColor.GREEN).clickEvent(ClickEvent.of((ClickEvent.Action)ClickEvent.Action.RUN_COMMAND, (String)confirmCommand))).hoverEvent(HoverEvent.of((HoverEvent.Action)HoverEvent.Action.SHOW_TEXT, (Component)TextComponent.of((String)("Click to confirm /" + args.getCommand())))))).append((Component)ErrorFormat.wrap((String[])new String[]{" or type "}))).append(CodeFormat.wrap((String[])new String[]{confirmCommand}).clickEvent(ClickEvent.of((ClickEvent.Action)ClickEvent.Action.SUGGEST_COMMAND, (String)confirmCommand)))).append((Component)ErrorFormat.wrap((String[])new String[]{" to confirm."}))).build();
                sender.print((Component)message);
                return;
            }
            configManager.activityHaltToggle = activityHaltToggle;
            if (activityHaltToggle) {
                if (!(sender instanceof LocalPlayer)) {
                    sender.print("ALL intensive server activity halted.");
                }
                if (!args.hasFlag('s')) {
                    this.worldGuard.getPlatform().broadcastNotification(LabelFormat.wrap((String[])new String[]{"ALL intensive server activity halted by " + sender.getDisplayName() + "."}));
                } else {
                    sender.print("(Silent) ALL intensive server activity halted by " + sender.getDisplayName() + ".");
                }
                for (World world : WorldEdit.getInstance().getPlatformManager().queryCapability(Capability.GAME_HOOKS).getWorlds()) {
                    int removed = 0;
                    for (Entity entity : world.getEntities()) {
                        if (!Entities.isIntensiveEntity(entity)) continue;
                        entity.remove();
                        ++removed;
                    }
                    if (removed <= 10) continue;
                    sender.printRaw(removed + " entities (>10) auto-removed from " + world.getName());
                }
            } else if (!args.hasFlag('s')) {
                this.worldGuard.getPlatform().broadcastNotification(LabelFormat.wrap((String[])new String[]{"ALL intensive server activity is now allowed."}));
                if (!(sender instanceof LocalPlayer)) {
                    sender.print("ALL intensive server activity is now allowed.");
                }
            } else {
                sender.print("(Silent) ALL intensive server activity is now allowed.");
            }
        }
    }
}

