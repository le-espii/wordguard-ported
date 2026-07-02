/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.command.argument;

import com.sk89q.worldedit.command.argument.CommaSeparatedValuesConverter;
import com.sk89q.worldedit.util.SideEffect;
import com.sk89q.worldedit.util.SideEffectSet;
import com.sk89q.worldedit.util.formatting.text.Component;
import com.sk89q.worldedit.util.formatting.text.TextComponent;
import java.util.List;
import org.enginehub.piston.CommandManager;
import org.enginehub.piston.converter.ArgumentConverter;
import org.enginehub.piston.converter.ConversionResult;
import org.enginehub.piston.converter.FailedConversion;
import org.enginehub.piston.converter.SuccessfulConversion;
import org.enginehub.piston.inject.InjectedValueAccess;
import org.enginehub.piston.inject.Key;

public class SideEffectSetConverter
implements ArgumentConverter<SideEffectSet> {
    private final TextComponent choices = TextComponent.of("any side effects");
    private final CommaSeparatedValuesConverter<SideEffect> sideEffectConverter;

    public static void register(CommandManager commandManager) {
        ArgumentConverter<SideEffect> sideEffectConverter = commandManager.getConverter(Key.of(SideEffect.class)).orElseThrow(() -> new IllegalStateException("SideEffectSetConverter must be registered after SideEffectConverter"));
        commandManager.registerConverter(Key.of(SideEffectSet.class), new SideEffectSetConverter(CommaSeparatedValuesConverter.wrapNoRepeats(sideEffectConverter)));
    }

    private SideEffectSetConverter(CommaSeparatedValuesConverter<SideEffect> sideEffectConverter) {
        this.sideEffectConverter = sideEffectConverter;
    }

    @Override
    public Component describeAcceptableArguments() {
        return this.choices;
    }

    @Override
    public List<String> getSuggestions(String input, InjectedValueAccess context) {
        return this.sideEffectConverter.getSuggestions(input, context);
    }

    @Override
    public ConversionResult<SideEffectSet> convert(String argument, InjectedValueAccess context) {
        try {
            ConversionResult<SideEffect> result = this.sideEffectConverter.convert(argument, context);
            if (result.isSuccessful()) {
                SideEffectSet set = SideEffectSet.none();
                for (SideEffect sideEffect : result.get()) {
                    set = set.with(sideEffect, SideEffect.State.ON);
                }
                return SuccessfulConversion.fromSingle(set);
            }
            return result.failureAsAny();
        }
        catch (Exception e) {
            return FailedConversion.from(e);
        }
    }
}

