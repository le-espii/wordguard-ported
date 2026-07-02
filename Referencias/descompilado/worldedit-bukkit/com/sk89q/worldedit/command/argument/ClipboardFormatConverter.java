/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.command.argument;

import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.share.ClipboardShareDestination;
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

public class ClipboardFormatConverter
implements ArgumentConverter<ClipboardFormat> {
    private final TextComponent choices = TextComponent.of("any clipboard format");

    public static void register(CommandManager commandManager) {
        commandManager.registerConverter(Key.of(ClipboardFormat.class), new ClipboardFormatConverter());
    }

    private ClipboardFormatConverter() {
    }

    @Override
    public Component describeAcceptableArguments() {
        return this.choices;
    }

    @Override
    public List<String> getSuggestions(String input, InjectedValueAccess context) {
        ClipboardShareDestination destination = context.injectedValue(Key.of(ClipboardShareDestination.class)).orElse(null);
        return SuggestionHelper.limitByPrefix(ClipboardFormats.getAll().stream().filter(format -> destination == null || destination.supportsFormat((ClipboardFormat)format)).map(ClipboardFormat::getAliases).flatMap(Collection::stream), input);
    }

    @Override
    public ConversionResult<ClipboardFormat> convert(String s, InjectedValueAccess injectedValueAccess) {
        ClipboardFormat result = ClipboardFormats.findByAlias(s);
        return result == null ? FailedConversion.from(new IllegalArgumentException("Not a valid schematic format: " + s)) : SuccessfulConversion.fromSingle(result);
    }
}

