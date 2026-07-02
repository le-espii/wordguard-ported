/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 */
package com.sk89q.worldedit.command.argument;

import com.google.common.collect.ImmutableList;
import com.sk89q.worldedit.command.util.EntityRemover;
import com.sk89q.worldedit.util.formatting.text.Component;
import com.sk89q.worldedit.util.formatting.text.TextComponent;
import java.util.List;
import org.enginehub.piston.CommandManager;
import org.enginehub.piston.converter.ArgumentConverter;
import org.enginehub.piston.converter.ConversionResult;
import org.enginehub.piston.converter.FailedConversion;
import org.enginehub.piston.converter.SuccessfulConversion;
import org.enginehub.piston.converter.SuggestionHelper;
import org.enginehub.piston.inject.InjectedValueAccess;
import org.enginehub.piston.inject.Key;

public class EntityRemoverConverter
implements ArgumentConverter<EntityRemover> {
    private final List<String> suggestions = ImmutableList.of((Object)"projectiles", (Object)"items", (Object)"paintings", (Object)"itemframes", (Object)"boats", (Object)"minecarts", (Object)"tnt", (Object)"xp", (Object)"all");

    public static void register(CommandManager commandManager) {
        commandManager.registerConverter(Key.of(EntityRemover.class), new EntityRemoverConverter());
    }

    private EntityRemoverConverter() {
    }

    @Override
    public Component describeAcceptableArguments() {
        return TextComponent.of("projectiles, items, paintings, itemframes, boats, minecarts, tnt, xp, or all");
    }

    @Override
    public List<String> getSuggestions(String input, InjectedValueAccess context) {
        return SuggestionHelper.limitByPrefix(this.suggestions.stream(), input);
    }

    @Override
    public ConversionResult<EntityRemover> convert(String argument, InjectedValueAccess context) {
        try {
            return SuccessfulConversion.fromSingle(EntityRemover.fromString(argument));
        }
        catch (Exception e) {
            return FailedConversion.from(e);
        }
    }
}

