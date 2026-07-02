/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sk89q.minecraft.util.commands.CommandException
 *  com.sk89q.minecraft.util.commands.CommandPermissionsException
 *  com.sk89q.worldedit.bukkit.BukkitAdapter
 *  com.sk89q.worldedit.entity.Player
 *  com.sk89q.worldedit.extension.platform.Actor
 *  com.sk89q.worldedit.util.formatting.text.Component
 *  com.sk89q.worldedit.util.formatting.text.TextComponent
 *  com.sk89q.worldedit.util.formatting.text.format.TextColor
 *  com.sk89q.worldedit.util.paste.ActorCallbackPaste
 *  com.sk89q.worldedit.util.task.Supervisor
 *  org.bukkit.Bukkit
 *  org.bukkit.ChatColor
 *  org.bukkit.Material
 *  org.bukkit.block.Block
 *  org.bukkit.block.BlockFace
 *  org.bukkit.command.CommandSender
 *  org.bukkit.command.ConsoleCommandSender
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.LivingEntity
 *  org.bukkit.entity.Player
 *  org.bukkit.event.Event
 *  org.bukkit.event.block.Action
 *  org.bukkit.event.entity.EntityDamageEvent$DamageCause
 *  org.bukkit.util.BlockIterator
 */
package com.sk89q.worldguard.bukkit;

import com.sk89q.minecraft.util.commands.CommandException;
import com.sk89q.minecraft.util.commands.CommandPermissionsException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.entity.Player;
import com.sk89q.worldedit.extension.platform.Actor;
import com.sk89q.worldedit.util.formatting.text.Component;
import com.sk89q.worldedit.util.formatting.text.TextComponent;
import com.sk89q.worldedit.util.formatting.text.format.TextColor;
import com.sk89q.worldedit.util.paste.ActorCallbackPaste;
import com.sk89q.worldedit.util.task.Supervisor;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.bukkit.event.debug.CancelLogging;
import com.sk89q.worldguard.bukkit.event.debug.LoggingBlockBreakEvent;
import com.sk89q.worldguard.bukkit.event.debug.LoggingBlockPlaceEvent;
import com.sk89q.worldguard.bukkit.event.debug.LoggingEntityDamageByEntityEvent;
import com.sk89q.worldguard.bukkit.event.debug.LoggingPlayerInteractEvent;
import com.sk89q.worldguard.bukkit.util.report.CancelReport;
import com.sk89q.worldguard.internal.platform.DebugHandler;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Event;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.util.BlockIterator;

public class BukkitDebugHandler
implements DebugHandler {
    private static final Logger log = Logger.getLogger(BukkitDebugHandler.class.getCanonicalName());
    private static final int MAX_TRACE_DISTANCE = 20;
    private final WorldGuardPlugin plugin;

    BukkitDebugHandler(WorldGuardPlugin plugin) {
        this.plugin = plugin;
    }

    private <T extends Event> void testEvent(CommandSender receiver, org.bukkit.entity.Player target, T event, boolean stacktraceMode) throws CommandPermissionsException {
        boolean isConsole = receiver instanceof ConsoleCommandSender;
        if (!receiver.equals((Object)target)) {
            if (!isConsole) {
                log.info(receiver.getName() + " is simulating an event on " + target.getName());
            }
            target.sendMessage(String.valueOf(ChatColor.RED) + "(Please ignore any messages that may immediately follow.)");
        }
        Bukkit.getPluginManager().callEvent(event);
        int start = new Exception().getStackTrace().length;
        CancelReport report = new CancelReport(event, ((CancelLogging)event).getCancels(), start);
        report.setDetectingPlugin(!stacktraceMode);
        String result = report.toString();
        if (stacktraceMode) {
            receiver.sendMessage(String.valueOf(ChatColor.GRAY) + "The report was printed to console.");
            log.info("Event report for " + receiver.getName() + ":\n\n" + result);
            this.plugin.checkPermission(receiver, "worldguard.debug.pastebin");
            ActorCallbackPaste.pastebin((Supervisor)WorldGuard.getInstance().getSupervisor(), (Actor)this.plugin.wrapCommandSender(receiver), (String)result, (String)"Event debugging report: %s.txt");
        } else {
            receiver.sendMessage(result.replaceAll("(?m)^", ChatColor.AQUA.toString()));
            if (result.length() >= 500 && !isConsole) {
                receiver.sendMessage(String.valueOf(ChatColor.GRAY) + "The report was also printed to console.");
                log.info("Event report for " + receiver.getName() + ":\n\n" + result);
            }
        }
    }

    private org.bukkit.entity.Player getSource(CommandSender sender, org.bukkit.entity.Player target, boolean fromTarget) throws CommandException {
        if (fromTarget) {
            return target;
        }
        if (sender instanceof org.bukkit.entity.Player) {
            return (org.bukkit.entity.Player)sender;
        }
        throw new CommandException("If this command is not to be used in-game, use -t to run the test from the viewpoint of the given player rather than yourself.");
    }

    private Block traceBlock(CommandSender sender, org.bukkit.entity.Player target, boolean fromTarget) throws CommandException {
        org.bukkit.entity.Player source = this.getSource(sender, target, fromTarget);
        BlockIterator it = new BlockIterator((LivingEntity)source);
        for (int i = 0; it.hasNext() && i < 20; ++i) {
            Block block = it.next();
            if (block.getType() == Material.AIR) continue;
            return block;
        }
        throw new CommandException("Not currently looking at a block that is close enough.");
    }

    private Entity traceEntity(CommandSender sender, org.bukkit.entity.Player target, boolean fromTarget) throws CommandException {
        org.bukkit.entity.Player source = this.getSource(sender, target, fromTarget);
        BlockIterator it = new BlockIterator((LivingEntity)source);
        for (int i = 0; it.hasNext() && i < 20; ++i) {
            Entity[] entities;
            Block block = it.next();
            for (Entity entity : entities = block.getChunk().getEntities()) {
                if (entity.equals((Object)target) || !(entity.getLocation().distanceSquared(block.getLocation()) < 10.0)) continue;
                return entity;
            }
        }
        throw new CommandException("Not currently looking at an entity that is close enough.");
    }

    @Override
    public void testBreak(Actor sender, LocalPlayer target, boolean fromTarget, boolean stackTraceMode) throws CommandException {
        CommandSender bukkitSender = this.plugin.unwrapActor(sender);
        org.bukkit.entity.Player bukkitTarget = BukkitAdapter.adapt((Player)target);
        Block block = this.traceBlock(bukkitSender, bukkitTarget, fromTarget);
        sender.print(TextComponent.of((String)"Testing BLOCK BREAK at ", (TextColor)TextColor.AQUA).append((Component)TextComponent.of((String)block.toString(), (TextColor)TextColor.DARK_AQUA)));
        LoggingBlockBreakEvent event = new LoggingBlockBreakEvent(block, bukkitTarget);
        this.testEvent(bukkitSender, bukkitTarget, event, stackTraceMode);
    }

    @Override
    public void testPlace(Actor sender, LocalPlayer target, boolean fromTarget, boolean stackTraceMode) throws CommandException {
        CommandSender bukkitSender = this.plugin.unwrapActor(sender);
        org.bukkit.entity.Player bukkitTarget = BukkitAdapter.adapt((Player)target);
        Block block = this.traceBlock(bukkitSender, bukkitTarget, fromTarget);
        sender.print(TextComponent.of((String)"Testing BLOCK PLACE at ", (TextColor)TextColor.AQUA).append((Component)TextComponent.of((String)block.toString(), (TextColor)TextColor.DARK_AQUA)));
        LoggingBlockPlaceEvent event = new LoggingBlockPlaceEvent(block, block.getState(), block.getRelative(BlockFace.DOWN), bukkitTarget.getItemInHand(), bukkitTarget, true);
        this.testEvent(bukkitSender, bukkitTarget, event, stackTraceMode);
    }

    @Override
    public void testInteract(Actor sender, LocalPlayer target, boolean fromTarget, boolean stackTraceMode) throws CommandException {
        CommandSender bukkitSender = this.plugin.unwrapActor(sender);
        org.bukkit.entity.Player bukkitTarget = BukkitAdapter.adapt((Player)target);
        Block block = this.traceBlock(bukkitSender, bukkitTarget, fromTarget);
        sender.print(TextComponent.of((String)"Testing BLOCK INTERACT at ", (TextColor)TextColor.AQUA).append((Component)TextComponent.of((String)block.toString(), (TextColor)TextColor.DARK_AQUA)));
        LoggingPlayerInteractEvent event = new LoggingPlayerInteractEvent(bukkitTarget, Action.RIGHT_CLICK_BLOCK, bukkitTarget.getItemInHand(), block, BlockFace.SOUTH);
        this.testEvent(bukkitSender, bukkitTarget, event, stackTraceMode);
    }

    @Override
    public void testDamage(Actor sender, LocalPlayer target, boolean fromTarget, boolean stackTraceMode) throws CommandException {
        CommandSender bukkitSender = this.plugin.unwrapActor(sender);
        org.bukkit.entity.Player bukkitTarget = BukkitAdapter.adapt((Player)target);
        Entity entity = this.traceEntity(bukkitSender, bukkitTarget, fromTarget);
        sender.print(TextComponent.of((String)"Testing ENTITY DAMAGE at ", (TextColor)TextColor.AQUA).append((Component)TextComponent.of((String)entity.toString(), (TextColor)TextColor.DARK_AQUA)));
        LoggingEntityDamageByEntityEvent event = new LoggingEntityDamageByEntityEvent((Entity)bukkitTarget, entity, EntityDamageEvent.DamageCause.ENTITY_ATTACK, 1.0);
        this.testEvent(bukkitSender, bukkitTarget, event, stackTraceMode);
    }
}

