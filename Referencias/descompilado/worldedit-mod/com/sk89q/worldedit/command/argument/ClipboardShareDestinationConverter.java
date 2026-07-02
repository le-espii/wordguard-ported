/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.command.argument;

import com.sk89q.worldedit.extent.clipboard.io.share.ClipboardShareDestination;
import com.sk89q.worldedit.extent.clipboard.io.share.ClipboardShareDestinations;
import com.sk89q.worldedit.util.formatting.text.Component;
import com.sk89q.worldedit.util.formatting.text.TextComponent;
import java.util.Collection;
import java.util.List;
import org.enginehub.piston.CommandManager;
import org.enginehub.piston.converter.ArgumentConverter;
import org.enginehub.piston.converter.ConversionResult;
import org.enginehub.piston.converter.FailedConversion;
import org.enginehub.piston.converter.SuccessfulConversion;
import org.enginehub.piston.converter.SuggestionHelper;
import org.enginehub.piston.inject.InjectedValueAccess;
import org.enginehub.piston.inject.Key;

public class ClipboardShareDestinationConverter
implements ArgumentConverter<ClipboardShareDestination> {
    private final TextComponent choices = TextComponent.of("any clipboard share destination");

    public static void register(CommandManager commandManager) {
        commandManager.registerConverter(Key.of(ClipboardShareDestination.class), new ClipboardShareDestinationConverter());
    }

    private ClipboardShareDestinationConverter() {
    }

    @Override
    public Component describeAcceptableArguments() {
        return this.choices;
    }

    @Override
    public List<String> getSuggestions(String input, InjectedValueAccess context) {
        return SuggestionHelper.limitByPrefix(ClipboardShareDestinations.getAll().stream().map(ClipboardShareDestination::getAliases).flatMap(Collection::stream), input);
    }

    @Override
    public ConversionResult<ClipboardShareDestination> convert(String s, InjectedValueAccess injectedValueAccess) {
        ClipboardShareDestination result = ClipboardShareDestinations.findByAlias(s);
        return result == null ? FailedConversion.from(new IllegalArgumentException("Not a valid clipboard share destination: " + s)) : SuccessfulConversion.fromSingle(result);
    }
}

