/*
 * Decompiled with CFR 0.152.
 */
package org.enginehub.piston.converter;

import com.sk89q.worldedit.util.formatting.text.Component;
import com.sk89q.worldedit.util.formatting.text.TextComponent;
import java.util.List;
import org.enginehub.piston.converter.ArgumentConverter;
import org.enginehub.piston.converter.ConversionResult;
import org.enginehub.piston.converter.Converter;
import org.enginehub.piston.converter.ForwardingArgumentConverter;
import org.enginehub.piston.converter.SuggestionProvider;
import org.enginehub.piston.inject.InjectedValueAccess;

public class SimpleArgumentConverter<T>
implements ArgumentConverter<T> {
    private final Converter<T> converter;
    private final Component description;

    public static <T> SimpleArgumentConverter<T> from(Converter<T> converter, String description) {
        return SimpleArgumentConverter.from(converter, TextComponent.of(description));
    }

    public static <T> SimpleArgumentConverter<T> from(Converter<T> converter, Component description) {
        return new SimpleArgumentConverter<T>(converter, description);
    }

    private SimpleArgumentConverter(Converter<T> converter, Component description) {
        this.converter = converter;
        this.description = description;
    }

    @Override
    public ConversionResult<T> convert(String argument, InjectedValueAccess context) {
        return this.converter.convert(argument, context);
    }

    @Override
    public Component describeAcceptableArguments() {
        return this.description;
    }

    public ArgumentConverter<T> withSuggestions(final SuggestionProvider suggestions) {
        return new ForwardingArgumentConverter<T>(this){

            @Override
            public List<String> getSuggestions(String input, InjectedValueAccess context) {
                return suggestions.getSuggestions(input, context);
            }
        };
    }
}

