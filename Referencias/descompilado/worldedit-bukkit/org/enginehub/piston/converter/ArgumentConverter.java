/*
 * Decompiled with CFR 0.152.
 */
package org.enginehub.piston.converter;

import com.sk89q.worldedit.util.formatting.text.Component;
import java.util.Collections;
import java.util.List;
import org.enginehub.piston.converter.Converter;
import org.enginehub.piston.converter.SuggestionProvider;
import org.enginehub.piston.inject.InjectedValueAccess;

public interface ArgumentConverter<T>
extends Converter<T>,
SuggestionProvider {
    public Component describeAcceptableArguments();

    @Override
    default public List<String> getSuggestions(String input, InjectedValueAccess context) {
        return Collections.emptyList();
    }
}

