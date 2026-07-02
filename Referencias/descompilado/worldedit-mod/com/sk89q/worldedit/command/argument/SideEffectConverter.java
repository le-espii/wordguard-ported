/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.command.argument;

import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.util.SideEffect;
import com.sk89q.worldedit.util.formatting.text.Component;
import com.sk89q.worldedit.util.formatting.text.TextComponent;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import org.enginehub.piston.CommandManager;
import org.enginehub.piston.converter.ArgumentConverter;
import org.enginehub.piston.converter.ConversionResult;
import org.enginehub.piston.converter.FailedConversion;
import org.enginehub.piston.converter.SuccessfulConversion;
import org.enginehub.piston.converter.SuggestionHelper;
import org.enginehub.piston.inject.InjectedValueAccess;
import org.enginehub.piston.inject.Key;

public class SideEffectConverter
implements ArgumentConverter<SideEffect> {
    private final TextComponent choices = TextComponent.of("any side effect");

    public static void register(CommandManager commandManager) {
        commandManager.registerConverter(Key.of(SideEffect.class), new SideEffectConverter());
    }

    private SideEffectConverter() {
    }

    private Collection<SideEffect> getSideEffects() {
        return WorldEdit.getInstance().getPlatformManager().getSupportedSideEffects();
    }

    @Override
    public Component describeAcceptableArguments() {
        return this.choices;
    }

    @Override
    public List<String> getSuggestions(String input, InjectedValueAccess context) {
        return SuggestionHelper.limitByPrefix(this.getSideEffects().stream().filter(SideEffect::isExposed).map(sideEffect -> sideEffect.name().toLowerCase(Locale.US)), input);
    }

    @Override
    public ConversionResult<SideEffect> convert(String argument, InjectedValueAccess context) {
        try {
            return SuccessfulConversion.fromSingle(SideEffect.valueOf(argument.toUpperCase(Locale.US)));
        }
        catch (Exception e) {
            return FailedConversion.from(e);
        }
    }
}

