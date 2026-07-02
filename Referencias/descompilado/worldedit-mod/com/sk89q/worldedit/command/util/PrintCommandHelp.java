/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Joiner
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Iterables
 */
package com.sk89q.worldedit.command.util;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.sk89q.worldedit.extension.platform.Actor;
import com.sk89q.worldedit.internal.command.CommandUtil;
import com.sk89q.worldedit.util.formatting.component.CommandListBox;
import com.sk89q.worldedit.util.formatting.component.CommandUsageBox;
import com.sk89q.worldedit.util.formatting.component.InvalidComponentException;
import com.sk89q.worldedit.util.formatting.text.TextComponent;
import com.sk89q.worldedit.util.formatting.text.TranslatableComponent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.enginehub.piston.Command;
import org.enginehub.piston.CommandManager;
import org.enginehub.piston.inject.Key;
import org.enginehub.piston.inject.MapBackedValueStore;

public class PrintCommandHelp {
    private static Command detectCommand(CommandManager manager, String command) {
        Optional<Command> mapping = manager.getCommand(command);
        if (mapping.isPresent()) {
            return mapping.get();
        }
        if (command.startsWith("/")) {
            mapping = manager.getCommand(command.substring(1));
            return mapping.orElse(null);
        }
        mapping = manager.getCommand("/" + command);
        return mapping.orElse(null);
    }

    public static void help(List<String> commandPath, int page, boolean listSubCommands, CommandManager manager, Actor actor, String helpRootCommand) throws InvalidComponentException {
        if (commandPath.isEmpty()) {
            PrintCommandHelp.printCommands(page, manager.getAllCommands(), actor, (List<Command>)ImmutableList.of(), helpRootCommand);
            return;
        }
        ArrayList<Command> visited = new ArrayList<Command>();
        Command currentCommand = PrintCommandHelp.detectCommand(manager, commandPath.get(0));
        if (currentCommand == null) {
            actor.printError(TranslatableComponent.of("worldedit.help.command-not-found", TextComponent.of(commandPath.get(0))));
            return;
        }
        visited.add(currentCommand);
        for (int i = 1; i < commandPath.size(); ++i) {
            String subCommand = commandPath.get(i);
            Map<String, Command> subCommands = CommandUtil.getSubCommands(currentCommand);
            if (subCommands.isEmpty()) {
                actor.printError(TranslatableComponent.of("worldedit.help.no-subcommands", TextComponent.of(PrintCommandHelp.toCommandString(visited)), TextComponent.of(subCommand)));
                CommandUsageBox box = new CommandUsageBox(visited, visited.stream().map(Command::getName).collect(Collectors.joining(" ")), helpRootCommand);
                actor.print(box.create());
                return;
            }
            if (!subCommands.containsKey(subCommand)) {
                actor.printError(TranslatableComponent.of("worldedit.help.subcommand-not-found", TextComponent.of(subCommand), TextComponent.of(PrintCommandHelp.toCommandString(visited))));
                PrintCommandHelp.printCommands(page, CommandUtil.getSubCommands((Command)Iterables.getLast(visited)).values().stream(), actor, visited, helpRootCommand);
                return;
            }
            currentCommand = subCommands.get(subCommand);
            visited.add(currentCommand);
        }
        Map<String, Command> subCommands = CommandUtil.getSubCommands(currentCommand);
        if (subCommands.isEmpty() || !listSubCommands) {
            CommandUsageBox box = new CommandUsageBox(visited, PrintCommandHelp.toCommandString(visited), helpRootCommand);
            actor.print(box.create());
        } else {
            PrintCommandHelp.printCommands(page, subCommands.values().stream(), actor, visited, helpRootCommand);
        }
    }

    private static String toCommandString(List<Command> visited) {
        return "/" + Joiner.on((String)" ").join(visited.stream().map(Command::getName).iterator());
    }

    private static void printCommands(int page, Stream<Command> commandStream, Actor actor, List<Command> commandList, String helpRootCommand) throws InvalidComponentException {
        MapBackedValueStore store = MapBackedValueStore.create();
        store.injectValue(Key.of(Actor.class), context -> Optional.of(actor));
        List commands = commandStream.filter(command -> command.getCondition().satisfied(store)).sorted(CommandUtil.byCleanName()).toList();
        String used = commandList.isEmpty() ? null : PrintCommandHelp.toCommandString(commandList);
        CommandListBox box = new CommandListBox((String)(used == null ? "Help" : "Subcommands: " + used), helpRootCommand + " -s -p %page%" + (String)(used == null ? "" : " " + used), helpRootCommand);
        if (!actor.isPlayer()) {
            box.formatForConsole();
        }
        for (Command mapping : commands) {
            String alias = (commandList.isEmpty() ? "/" : "") + mapping.getName();
            String command2 = Stream.concat(commandList.stream(), Stream.of(mapping)).map(Command::getName).collect(Collectors.joining(" ", "/", ""));
            box.appendCommand(alias, mapping.getDescription(), command2);
        }
        actor.print(box.create(page));
    }

    private PrintCommandHelp() {
    }
}

