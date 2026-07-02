/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.command.Command
 *  org.bukkit.command.CommandExecutor
 *  org.bukkit.command.CommandMap
 *  org.bukkit.command.PluginIdentifiableCommand
 *  org.bukkit.command.SimpleCommandMap
 *  org.bukkit.event.Listener
 *  org.bukkit.help.HelpTopicFactory
 *  org.bukkit.plugin.Plugin
 */
package com.sk89q.bukkit.util;

import com.sk89q.bukkit.util.CommandInfo;
import com.sk89q.bukkit.util.DynamicPluginCommand;
import com.sk89q.bukkit.util.DynamicPluginCommandHelpTopic;
import com.sk89q.bukkit.util.FallbackRegistrationListener;
import com.sk89q.util.ReflectionUtil;
import com.sk89q.worldedit.bukkit.paperlib.PaperLib;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandMap;
import org.bukkit.command.PluginIdentifiableCommand;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.event.Listener;
import org.bukkit.help.HelpTopicFactory;
import org.bukkit.plugin.Plugin;

public class CommandRegistration {
    protected final Plugin plugin;
    protected final CommandExecutor executor;
    private CommandMap serverCommandMap;
    private CommandMap fallbackCommands;

    public CommandRegistration(Plugin plugin) {
        this(plugin, (CommandExecutor)plugin);
    }

    public CommandRegistration(Plugin plugin, CommandExecutor executor) {
        this.plugin = plugin;
        this.executor = executor;
    }

    public Plugin getCommandOwner(String label) {
        if (this.serverCommandMap == null) {
            return null;
        }
        Command command = this.serverCommandMap.getCommand(label);
        if (command instanceof PluginIdentifiableCommand) {
            return ((PluginIdentifiableCommand)command).getPlugin();
        }
        return null;
    }

    public boolean register(List<CommandInfo> registered) {
        CommandMap commandMap = this.getCommandMap();
        if (registered == null || commandMap == null) {
            return false;
        }
        for (CommandInfo command : registered) {
            DynamicPluginCommand cmd = new DynamicPluginCommand(command.getAliases(), command.getDesc(), command.getUsage(), this.executor, command.getRegisteredWith(), this.plugin);
            cmd.setPermissions(command.getPermissions());
            commandMap.register(this.plugin.getDescription().getName(), (Command)cmd);
        }
        return true;
    }

    public CommandMap getCommandMap() {
        CommandMap commandMap;
        if (this.serverCommandMap != null) {
            return this.serverCommandMap;
        }
        if (this.fallbackCommands != null) {
            return this.fallbackCommands;
        }
        CommandMap commandMap2 = commandMap = PaperLib.isPaper() ? Bukkit.getCommandMap() : (CommandMap)ReflectionUtil.getField(this.plugin.getServer().getPluginManager(), "commandMap");
        if (commandMap == null) {
            Bukkit.getServer().getLogger().severe(this.plugin.getDescription().getName() + ": Could not retrieve server CommandMap, using fallback instead!");
            this.fallbackCommands = commandMap = new SimpleCommandMap(Bukkit.getServer());
            Bukkit.getServer().getPluginManager().registerEvents((Listener)new FallbackRegistrationListener(this.fallbackCommands), this.plugin);
        } else {
            this.serverCommandMap = commandMap;
        }
        return commandMap;
    }

    public boolean unregisterCommands() {
        CommandMap commandMap = this.getCommandMap();
        ArrayList<String> toRemove = new ArrayList<String>();
        Map knownCommands = (Map)ReflectionUtil.getField(commandMap, "knownCommands");
        Set aliases = (Set)ReflectionUtil.getField(commandMap, "aliases");
        if (knownCommands == null || aliases == null) {
            return false;
        }
        Iterator i = knownCommands.values().iterator();
        while (i.hasNext()) {
            Command cmd = (Command)i.next();
            if (!(cmd instanceof DynamicPluginCommand) || !((DynamicPluginCommand)cmd).getOwner().equals(this.executor)) continue;
            i.remove();
            for (String alias : cmd.getAliases()) {
                Command aliasCmd = (Command)knownCommands.get(alias);
                if (!cmd.equals(aliasCmd)) continue;
                aliases.remove(alias);
                toRemove.add(alias);
            }
        }
        for (String string : toRemove) {
            knownCommands.remove(string);
        }
        return true;
    }

    static {
        Bukkit.getServer().getHelpMap().registerHelpTopicFactory(DynamicPluginCommand.class, (HelpTopicFactory)new DynamicPluginCommandHelpTopic.Factory());
    }
}

