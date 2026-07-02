/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 */
package org.enginehub.piston.converter;

import com.google.common.collect.ImmutableMap;
import com.sk89q.worldedit.util.formatting.text.Component;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.enginehub.piston.config.ColorConfig;
import org.enginehub.piston.converter.ArgumentConverter;
import org.enginehub.piston.converter.ConversionResult;
import org.enginehub.piston.converter.FailedConversion;
import org.enginehub.piston.converter.SuccessfulConversion;
import org.enginehub.piston.converter.SuggestionHelper;
import org.enginehub.piston.inject.InjectedValueAccess;
import org.enginehub.piston.util.ComponentHelper;

public final class MapArgumentConverter<T>
implements ArgumentConverter<T> {
    private final ImmutableMap<String, T> map;

    public static MapArgumentConverter<String> forChoices(Set<String> choices) {
        ImmutableMap.Builder map = ImmutableMap.builder();
        choices.forEach(c -> map.put(c, c));
        return MapArgumentConverter.from(map.build());
    }

    public static <T> MapArgumentConverter<T> from(Map<String, T> map) {
        return new MapArgumentConverter<T>(map);
    }

    private MapArgumentConverter(Map<String, T> map) {
        this.map = ImmutableMap.copyOf(map);
    }

    @Override
    public ConversionResult<T> convert(String argument, InjectedValueAccess context) {
        Object result = this.map.get((Object)argument);
        if (result == null) {
            return FailedConversion.from(new IllegalArgumentException("Invalid value: " + argument));
        }
        return SuccessfulConversion.fromSingle(result);
    }

    @Override
    public Component describeAcceptableArguments() {
        return this.map.keySet().stream().map(ColorConfig.mainText()::wrap).collect(ComponentHelper.joiningWithBar());
    }

    @Override
    public List<String> getSuggestions(String input, InjectedValueAccess context) {
        return SuggestionHelper.limitByPrefix(this.map.keySet().stream(), input);
    }
}

