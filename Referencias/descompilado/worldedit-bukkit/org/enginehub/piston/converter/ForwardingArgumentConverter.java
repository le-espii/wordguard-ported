/*
 * Decompiled with CFR 0.152.
 */
package org.enginehub.piston.converter;

import com.sk89q.worldedit.util.formatting.text.Component;
import java.util.List;
import org.enginehub.piston.converter.ArgumentConverter;
import org.enginehub.piston.converter.ConversionResult;
import org.enginehub.piston.inject.InjectedValueAccess;

public class ForwardingArgumentConverter<T>
implements ArgumentConverter<T> {
    private final ArgumentConverter<T> delegate;

    protected ForwardingArgumentConverter(ArgumentConverter<T> delegate) {
        this.delegate = delegate;
    }

    @Override
    public ConversionResult<T> convert(String argument, InjectedValueAccess context) {
        return this.delegate.convert(argument, context);
    }

    @Override
    public Component describeAcceptableArguments() {
        return this.delegate.describeAcceptableArguments();
    }

    @Override
    public List<String> getSuggestions(String input, InjectedValueAccess context) {
        return this.delegate.getSuggestions(input, context);
    }
}

