/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  com.google.common.collect.ImmutableTable
 *  com.google.common.collect.ImmutableTable$Builder
 */
package org.enginehub.piston.impl;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableTable;
import java.util.stream.IntStream;
import org.enginehub.piston.Command;
import org.enginehub.piston.part.ArgAcceptingCommandFlag;
import org.enginehub.piston.part.ArgAcceptingCommandPart;
import org.enginehub.piston.part.ArgConsumingCommandPart;
import org.enginehub.piston.part.CommandArgument;
import org.enginehub.piston.part.CommandFlag;
import org.enginehub.piston.part.CommandPart;
import org.enginehub.piston.part.NoArgCommandFlag;
import org.enginehub.piston.part.SubCommandPart;

class CommandInfo {
    final ImmutableList<ArgConsumingCommandPart> arguments;
    final ImmutableList<ArgAcceptingCommandPart> defaultProvided;
    final ImmutableMap<Character, CommandFlag> flags;
    final ImmutableTable<SubCommandPart, String, Command> subCommandTable;
    final int requiredParts;

    static CommandInfo from(Command command) {
        ImmutableList.Builder arguments = ImmutableList.builder();
        ImmutableList.Builder defaultProvided = ImmutableList.builder();
        ImmutableMap.Builder flags = ImmutableMap.builder();
        ImmutableTable.Builder subCommandTable = ImmutableTable.builder();
        boolean seenRequiredSubCommand = false;
        boolean seenOptionalArg = false;
        boolean middleOptionalArg = false;
        ImmutableList<CommandPart> parts = command.getParts();
        int requiredParts = 0;
        for (int i = 0; i < parts.size(); ++i) {
            ArgAcceptingCommandPart argPart;
            CommandPart part = (CommandPart)parts.get(i);
            if (part instanceof ArgAcceptingCommandFlag || part instanceof NoArgCommandFlag) {
                CommandFlag flag = (CommandFlag)part;
                flags.put((Object)Character.valueOf(flag.getName()), (Object)flag);
            } else if (part instanceof CommandArgument) {
                if (part.isRequired() && seenOptionalArg) {
                    middleOptionalArg = true;
                }
                if (!part.isRequired()) {
                    seenOptionalArg = true;
                }
                arguments.add((Object)((ArgConsumingCommandPart)part));
            } else if (part instanceof SubCommandPart) {
                if (part.isRequired()) {
                    Preconditions.checkState((i + 1 >= parts.size() ? 1 : 0) != 0, (Object)"Required sub-command must be last part.");
                    seenRequiredSubCommand = true;
                }
                SubCommandPart subCommandPart = (SubCommandPart)part;
                for (Command cmd : subCommandPart.getCommands()) {
                    subCommandTable.put((Object)subCommandPart, (Object)cmd.getName(), (Object)cmd);
                    for (String alias : cmd.getAliases()) {
                        subCommandTable.put((Object)subCommandPart, (Object)alias, (Object)cmd);
                    }
                }
                arguments.add((Object)subCommandPart);
            } else {
                throw new IllegalStateException("Unknown part implementation " + part);
            }
            if (part.isRequired()) {
                ++requiredParts;
            }
            if (!(part instanceof ArgAcceptingCommandPart) || (argPart = (ArgAcceptingCommandPart)part).getDefaults().size() <= 0) continue;
            defaultProvided.add((Object)argPart);
        }
        Preconditions.checkState((!seenRequiredSubCommand || !middleOptionalArg ? 1 : 0) != 0, (Object)"Cannot have middle-filled optionals and sub-commands");
        ImmutableList commandArguments = arguments.build();
        int[] indexes = IntStream.range(0, commandArguments.size()).filter(idx -> commandArguments.get(idx) instanceof CommandArgument).filter(idx -> ((CommandArgument)commandArguments.get(idx)).isVariable()).toArray();
        Preconditions.checkArgument((indexes.length <= 1 ? 1 : 0) != 0, (Object)"Too many variable arguments");
        if (indexes.length > 0) {
            int varargIndex = indexes[0];
            Preconditions.checkArgument((varargIndex == commandArguments.size() - 1 ? 1 : 0) != 0, (Object)"Variable argument must be the last argument");
        }
        return new CommandInfo((ImmutableList<ArgConsumingCommandPart>)commandArguments, (ImmutableList<ArgAcceptingCommandPart>)defaultProvided.build(), (ImmutableMap<Character, CommandFlag>)flags.build(), (ImmutableTable<SubCommandPart, String, Command>)subCommandTable.build(), requiredParts);
    }

    CommandInfo(ImmutableList<ArgConsumingCommandPart> arguments, ImmutableList<ArgAcceptingCommandPart> defaultProvided, ImmutableMap<Character, CommandFlag> flags, ImmutableTable<SubCommandPart, String, Command> subCommandTable, int requiredParts) {
        this.arguments = arguments;
        this.defaultProvided = defaultProvided;
        this.flags = flags;
        this.subCommandTable = subCommandTable;
        this.requiredParts = requiredParts;
    }
}

