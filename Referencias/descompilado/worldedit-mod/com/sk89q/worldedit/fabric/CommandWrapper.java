/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 *  com.mojang.brigadier.Command
 *  com.mojang.brigadier.CommandDispatcher
 *  com.mojang.brigadier.arguments.ArgumentType
 *  com.mojang.brigadier.arguments.StringArgumentType
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.context.StringRange
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.brigadier.suggestion.Suggestion
 *  com.mojang.brigadier.suggestion.Suggestions
 *  com.mojang.brigadier.suggestion.SuggestionsBuilder
 *  net.minecraft.class_2168
 *  net.minecraft.class_2170
 */
package com.sk89q.worldedit.fabric;

import com.google.common.collect.ImmutableList;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.context.StringRange;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.event.platform.CommandEvent;
import com.sk89q.worldedit.event.platform.CommandSuggestionEvent;
import com.sk89q.worldedit.extension.platform.Actor;
import com.sk89q.worldedit.fabric.FabricAdapter;
import com.sk89q.worldedit.internal.util.Substring;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;
import net.minecraft.class_2168;
import net.minecraft.class_2170;
import org.enginehub.piston.Command;
import org.enginehub.piston.inject.Key;
import org.enginehub.piston.inject.MapBackedValueStore;

public final class CommandWrapper {
    private CommandWrapper() {
    }

    public static void register(CommandDispatcher<class_2168> dispatcher, org.enginehub.piston.Command command) {
        ImmutableList.Builder aliases = ImmutableList.builder();
        aliases.add((Object)command.getName()).addAll(command.getAliases());
        Command commandRunner = ctx -> {
            WorldEdit.getInstance().getEventBus().post(new CommandEvent(FabricAdapter.adaptCommandSource((class_2168)ctx.getSource()), "/" + ctx.getInput()));
            return 0;
        };
        for (String alias : aliases.build()) {
            LiteralArgumentBuilder base = (LiteralArgumentBuilder)((LiteralArgumentBuilder)class_2170.method_9247((String)alias).executes(commandRunner)).then(class_2170.method_9244((String)"args", (ArgumentType)StringArgumentType.greedyString()).suggests(CommandWrapper::suggest).executes(commandRunner));
            if (command.getCondition() != Command.Condition.TRUE) {
                base.requires(CommandWrapper.requirementsFor(command));
            }
            dispatcher.register(base);
        }
    }

    private static Predicate<class_2168> requirementsFor(org.enginehub.piston.Command mapping) {
        return ctx -> {
            MapBackedValueStore store = MapBackedValueStore.create();
            Actor actor = FabricAdapter.adaptCommandSource(ctx);
            store.injectValue(Key.of(Actor.class), context -> Optional.of(actor));
            return mapping.getCondition().satisfied(store);
        };
    }

    private static CompletableFuture<Suggestions> suggest(CommandContext<class_2168> context, SuggestionsBuilder builder) throws CommandSyntaxException {
        CommandSuggestionEvent event = new CommandSuggestionEvent(FabricAdapter.adaptCommandSource((class_2168)context.getSource()), builder.getInput());
        WorldEdit.getInstance().getEventBus().post(event);
        List<Substring> suggestions = event.getSuggestions();
        ImmutableList.Builder result = ImmutableList.builder();
        for (Substring suggestion : suggestions) {
            Object suggestionText = suggestion.getSubstring();
            if (suggestion.getStart() == suggestion.getEnd() && suggestion.getEnd() == builder.getInput().length() && !builder.getInput().endsWith(" ") && !builder.getInput().endsWith("\"")) {
                suggestionText = " " + (String)suggestionText;
            }
            result.add((Object)new Suggestion(StringRange.between((int)suggestion.getStart(), (int)suggestion.getEnd()), (String)suggestionText));
        }
        return CompletableFuture.completedFuture(Suggestions.create((String)builder.getInput(), (Collection)result.build()));
    }
}

