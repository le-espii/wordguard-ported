/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 *  com.google.common.collect.UnmodifiableIterator
 */
package org.enginehub.piston.util;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.UnmodifiableIterator;
import com.sk89q.worldedit.util.formatting.text.Component;
import com.sk89q.worldedit.util.formatting.text.TextComponent;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import org.enginehub.piston.ArgBinding;
import org.enginehub.piston.Command;
import org.enginehub.piston.CommandMetadata;
import org.enginehub.piston.CommandParameters;
import org.enginehub.piston.CommandParseResult;
import org.enginehub.piston.NoInputCommandParameters;
import org.enginehub.piston.config.ColorConfig;
import org.enginehub.piston.config.TextConfig;
import org.enginehub.piston.inject.InjectedValueAccess;
import org.enginehub.piston.part.ArgAcceptingCommandFlag;
import org.enginehub.piston.part.ArgAcceptingCommandPart;
import org.enginehub.piston.part.CommandArgument;
import org.enginehub.piston.part.CommandFlag;
import org.enginehub.piston.part.CommandPart;
import org.enginehub.piston.part.SubCommandPart;
import org.enginehub.piston.util.PartHelper;

public class HelpGenerator {
    private final CommandParseResult parseResult;

    public static HelpGenerator create(Iterable<Command> commands) {
        final ImmutableList executionPath = ImmutableList.copyOf(commands);
        return HelpGenerator.create(new CommandParseResult(){

            @Override
            public ImmutableList<Command> getExecutionPath() {
                return executionPath;
            }

            @Override
            public ImmutableList<ArgBinding> getBoundArguments() {
                return ImmutableList.of();
            }

            @Override
            public CommandParameters getParameters() {
                return NoInputCommandParameters.builder().injectedValues(InjectedValueAccess.EMPTY).build();
            }
        });
    }

    public static HelpGenerator create(CommandParseResult parseResult) {
        return new HelpGenerator(parseResult);
    }

    private HelpGenerator(CommandParseResult parseResult) {
        this.parseResult = parseResult;
    }

    public Component getFullName() {
        TextComponent.Builder usage = TextComponent.builder();
        String name = ((Command)this.parseResult.getExecutionPath().get(0)).getName();
        CommandMetadata metadata = this.parseResult.getParameters().getMetadata();
        if (metadata != null) {
            name = metadata.getCalledName();
        }
        usage.append(ColorConfig.mainText().wrap(TextConfig.commandPrefixValue(), TextComponent.of(name)));
        for (String input : this.parseResult.getOriginalArguments()) {
            usage.append((Component)TextComponent.space());
            usage.append(ColorConfig.mainText().wrap(input));
        }
        return usage.build();
    }

    public Component getUsage() {
        TextComponent.Builder usage = (TextComponent.Builder)TextComponent.builder().append(ColorConfig.mainText().wrap(TextConfig.commandPrefixValue()));
        UnmodifiableIterator iterator = this.parseResult.getExecutionPath().iterator();
        while (iterator.hasNext()) {
            ImmutableList reducedParts;
            CommandMetadata metadata;
            Command command = (Command)iterator.next();
            String name = command.getName();
            if (command == this.parseResult.getExecutionPath().get(0) && (metadata = this.parseResult.getParameters().getMetadata()) != null) {
                name = metadata.getCalledName();
            }
            usage.append(ColorConfig.mainText().wrap(name));
            if (iterator.hasNext()) {
                CommandPart part;
                ImmutableList.Builder parts = ImmutableList.builder();
                UnmodifiableIterator unmodifiableIterator = command.getParts().iterator();
                while (unmodifiableIterator.hasNext() && !((part = (CommandPart)unmodifiableIterator.next()) instanceof SubCommandPart)) {
                    parts.add((Object)part);
                }
                reducedParts = parts.build();
            } else {
                reducedParts = command.getParts();
            }
            if (!reducedParts.isEmpty()) {
                usage.append((Component)TextComponent.space());
            }
            PartHelper.appendUsage(reducedParts.stream(), usage);
            if (!iterator.hasNext()) continue;
            usage.append((Component)TextComponent.space());
        }
        return usage.build();
    }

    public Component getFullHelp() {
        Command primary = this.parseResult.getPrimaryCommand();
        ImmutableList.Builder builder = ImmutableList.builder();
        builder.add((Object)primary.getDescription());
        builder.add((Object)TextComponent.of("\nUsage: "));
        builder.add((Object)this.getUsage());
        this.appendArguments((ImmutableList.Builder<Component>)builder);
        this.appendFlags((ImmutableList.Builder<Component>)builder);
        primary.getFooter().ifPresent(footer -> builder.add((Object)TextComponent.newline()).add(footer));
        return ColorConfig.helpText().wrap((List<Component>)builder.build());
    }

    private void appendArguments(ImmutableList.Builder<Component> builder) {
        Command primary = this.parseResult.getPrimaryCommand();
        List args = primary.getParts().stream().filter(x -> x instanceof CommandArgument).map(x -> (CommandArgument)x).collect(Collectors.toList());
        if (args.size() > 0) {
            builder.add((Object)TextComponent.newline());
            builder.add((Object)TextComponent.of("Arguments:\n"));
            Iterator iterator = args.iterator();
            while (iterator.hasNext()) {
                CommandArgument arg = (CommandArgument)iterator.next();
                builder.add((Object)TextComponent.of("  ")).add((Object)arg.getTextRepresentation());
                this.addDefaultInfo(builder, arg);
                builder.add((Object)TextComponent.of(": ")).add((Object)arg.getDescription());
                if (!iterator.hasNext()) continue;
                builder.add((Object)TextComponent.newline());
            }
        }
    }

    private void appendFlags(ImmutableList.Builder<Component> builder) {
        Command primary = this.parseResult.getPrimaryCommand();
        List flags = primary.getParts().stream().filter(x -> x instanceof CommandFlag).map(x -> (CommandFlag)x).collect(Collectors.toList());
        if (flags.size() > 0) {
            builder.add((Object)TextComponent.newline());
            builder.add((Object)TextComponent.of("Flags:\n"));
            Iterator iterator = flags.iterator();
            while (iterator.hasNext()) {
                CommandFlag flag = (CommandFlag)iterator.next();
                builder.add((Object)ColorConfig.mainText().wrap("  -" + flag.getName()));
                if (flag instanceof ArgAcceptingCommandFlag) {
                    this.addDefaultInfo(builder, (ArgAcceptingCommandFlag)flag);
                }
                builder.add((Object)TextComponent.of(": ")).add((Object)flag.getDescription());
                if (!iterator.hasNext()) continue;
                builder.add((Object)TextComponent.newline());
            }
        }
    }

    private void addDefaultInfo(ImmutableList.Builder<Component> builder, ArgAcceptingCommandPart arg) {
        String value;
        if (arg.getDefaults().isEmpty()) {
            return;
        }
        builder.add((Object)TextComponent.of(" (defaults to "));
        if (arg.getDefaults().size() == 1) {
            value = (String)arg.getDefaults().get(0);
            if (value.trim().isEmpty()) {
                value = "none";
            }
        } else {
            value = arg.getDefaults().stream().filter(s -> s.trim().length() > 0).collect(Collectors.joining(", ", "[", "]"));
        }
        builder.add((Object)TextComponent.of(value));
        builder.add((Object)TextComponent.of(")"));
    }
}

