/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Iterables
 */
package com.sk89q.worldedit.internal.command;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.sk89q.worldedit.extension.platform.Actor;
import com.sk89q.worldedit.extension.platform.PlatformCommandManager;
import com.sk89q.worldedit.internal.command.CommandArgParser;
import com.sk89q.worldedit.internal.util.Substring;
import com.sk89q.worldedit.util.formatting.text.Component;
import com.sk89q.worldedit.util.formatting.text.TextComponent;
import com.sk89q.worldedit.util.formatting.text.event.ClickEvent;
import com.sk89q.worldedit.util.formatting.text.format.TextColor;
import com.sk89q.worldedit.util.formatting.text.format.TextDecoration;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.enginehub.piston.Command;
import org.enginehub.piston.CommandParameters;
import org.enginehub.piston.NoInputCommandParameters;
import org.enginehub.piston.exception.CommandException;
import org.enginehub.piston.inject.InjectedValueAccess;
import org.enginehub.piston.inject.Key;
import org.enginehub.piston.part.SubCommandPart;

public class CommandUtil {
    private static final Component DEPRECATION_MARKER = TextComponent.of("This command is deprecated.");
    private static final Comparator<Command> BY_CLEAN_NAME = Comparator.comparing(c -> CommandUtil.clean(c.getName()));

    private static Component makeDeprecatedFooter(String reason, Component replacement) {
        return ((TextComponent.Builder)((TextComponent.Builder)((TextComponent.Builder)((TextComponent.Builder)TextComponent.builder().append(DEPRECATION_MARKER)).append(" " + reason + ".")).append((Component)TextComponent.newline())).append(replacement.color(TextColor.GOLD))).build();
    }

    public static Component createNewCommandReplacementText(String suggestedCommand) {
        return ((TextComponent.Builder)((TextComponent.Builder)TextComponent.builder("Please use ", TextColor.GOLD).append(((TextComponent)TextComponent.of(suggestedCommand).decoration(TextDecoration.UNDERLINED, true)).clickEvent(ClickEvent.suggestCommand(suggestedCommand)))).append(" instead.")).build();
    }

    public static Command deprecate(Command command, String reason, ReplacementMessageGenerator replacementMessageGenerator) {
        Component deprecatedWarning = CommandUtil.makeDeprecatedFooter(reason, replacementMessageGenerator.getReplacement(command, NoInputCommandParameters.builder().build()));
        return command.toBuilder().action(parameters -> CommandUtil.deprecatedCommandWarning(parameters, command, reason, replacementMessageGenerator)).footer(command.getFooter().map(existingFooter -> existingFooter.append(TextComponent.newline()).append(deprecatedWarning)).orElse(deprecatedWarning)).build();
    }

    public static Optional<Component> footerWithoutDeprecation(Command command) {
        return command.getFooter().filter(footer -> CommandUtil.anyComponent(footer, Predicate.isEqual(DEPRECATION_MARKER))).map(CommandUtil::replaceDeprecation).or(command::getFooter);
    }

    public static Optional<Component> deprecationWarning(Command command) {
        return command.getFooter().map(CommandUtil::extractDeprecation).orElseGet(command::getFooter);
    }

    public static boolean isDeprecated(Command command) {
        return command.getFooter().filter(footer -> CommandUtil.anyComponent(footer, Predicate.isEqual(DEPRECATION_MARKER))).isPresent();
    }

    private static boolean anyComponent(Component component, Predicate<Component> test) {
        return test.test(component) || component.children().stream().anyMatch(x -> CommandUtil.anyComponent(x, test));
    }

    private static Component replaceDeprecation(Component component) {
        if (component.children().stream().anyMatch(Predicate.isEqual(DEPRECATION_MARKER))) {
            return TextComponent.empty();
        }
        return component.children(component.children().stream().map(CommandUtil::replaceDeprecation).collect(Collectors.toList()));
    }

    private static Optional<Component> extractDeprecation(Component component) {
        if (component.children().stream().anyMatch(Predicate.isEqual(DEPRECATION_MARKER))) {
            return Optional.of(component);
        }
        return component.children().stream().map(CommandUtil::extractDeprecation).filter(Optional::isPresent).map(Optional::get).findAny();
    }

    private static int deprecatedCommandWarning(CommandParameters parameters, Command command, String reason, ReplacementMessageGenerator generator) throws Exception {
        parameters.injectedValue(Key.of(Actor.class)).ifPresent(actor -> CommandUtil.sendDeprecationMessage(parameters, command, reason, generator, actor));
        return command.getAction().run(parameters);
    }

    private static void sendDeprecationMessage(CommandParameters parameters, Command command, String reason, ReplacementMessageGenerator generator, Actor actor) {
        Component replacement = generator.getReplacement(command, parameters);
        actor.print((Component)((TextComponent.Builder)TextComponent.builder(reason + ". ", TextColor.GOLD).append(replacement)).build());
    }

    public static Map<String, Command> getSubCommands(Command currentCommand) {
        return currentCommand.getParts().stream().filter(p -> p instanceof SubCommandPart).flatMap(p -> ((SubCommandPart)p).getCommands().stream()).collect(Collectors.toMap(Command::getName, Function.identity()));
    }

    private static String clean(String input) {
        return PlatformCommandManager.COMMAND_CLEAN_PATTERN.matcher(input).replaceAll("");
    }

    public static Comparator<Command> byCleanName() {
        return BY_CLEAN_NAME;
    }

    public static List<String> fixSuggestions(String arguments, List<Substring> suggestions) {
        Substring lastArg = (Substring)Iterables.getLast(CommandArgParser.spaceSplit(arguments));
        return suggestions.stream().map(suggestion -> CommandUtil.onlyOnLastQuotedWord(lastArg, suggestion)).map(suggestion -> CommandUtil.suggestLast(lastArg, suggestion)).filter(Optional::isPresent).map(Optional::get).collect(Collectors.toList());
    }

    private static Substring onlyOnLastQuotedWord(Substring lastArg, Substring suggestion) {
        if (suggestion.getSubstring().startsWith(lastArg.getSubstring())) {
            return suggestion;
        }
        String substr = suggestion.getSubstring();
        int sp = substr.trim().lastIndexOf(32);
        if (sp < 0) {
            return suggestion;
        }
        return Substring.wrap(substr.substring(sp + 1), suggestion.getStart() + sp + 1, suggestion.getEnd());
    }

    private static Optional<String> suggestLast(Substring last, Substring suggestion) {
        if (suggestion.getStart() == last.getEnd() && !last.getSubstring().equals("\"")) {
            if (last.getSubstring().isEmpty()) {
                return Optional.of(suggestion.getSubstring());
            }
            return Optional.of(last.getSubstring() + " " + suggestion.getSubstring());
        }
        StringBuilder builder = new StringBuilder(last.getSubstring());
        int start = suggestion.getStart() - last.getStart();
        int end = suggestion.getEnd() - last.getStart();
        if (start < 0) {
            return Optional.empty();
        }
        Preconditions.checkState((end <= builder.length() ? 1 : 0) != 0, (String)"Suggestion ends too late, last=%s, suggestion=", (Object)last, (Object)suggestion);
        builder.replace(start, end, suggestion.getSubstring());
        return Optional.of(builder.toString());
    }

    public static void checkCommandArgument(boolean condition, String message) {
        CommandUtil.checkCommandArgument(condition, TextComponent.of(message));
    }

    public static void checkCommandArgument(boolean condition, Component message) {
        if (!condition) {
            throw new CommandException(message, (ImmutableList<Command>)ImmutableList.of());
        }
    }

    public static <T> T requireIV(Key<T> type, String name, InjectedValueAccess injectedValueAccess) {
        return injectedValueAccess.injectedValue(type).orElseThrow(() -> new IllegalStateException("No injected value for " + name + " (type " + String.valueOf(type) + ")"));
    }

    private CommandUtil() {
    }

    public static interface ReplacementMessageGenerator {
        public static ReplacementMessageGenerator forNewCommand(NewCommandGenerator generator) {
            return (oldCommand, oldParameters) -> {
                String suggestedCommand = generator.newCommand(oldCommand, oldParameters);
                return CommandUtil.createNewCommandReplacementText(suggestedCommand);
            };
        }

        public Component getReplacement(Command var1, CommandParameters var2);
    }

    public static interface NewCommandGenerator {
        public String newCommand(Command var1, CommandParameters var2);
    }
}

