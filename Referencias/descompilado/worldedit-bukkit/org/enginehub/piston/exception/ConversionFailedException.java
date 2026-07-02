/*
 * Decompiled with CFR 0.152.
 */
package org.enginehub.piston.exception;

import com.sk89q.worldedit.util.formatting.text.Component;
import com.sk89q.worldedit.util.formatting.text.TextComponent;
import java.util.stream.Stream;
import org.enginehub.piston.CommandParseResult;
import org.enginehub.piston.converter.ArgumentConverter;
import org.enginehub.piston.converter.FailedConversion;
import org.enginehub.piston.converter.FailedConversionMapper;
import org.enginehub.piston.exception.UsageException;

public class ConversionFailedException
extends UsageException {
    private final ArgumentConverter<?> converter;
    private final FailedConversion<?> conversion;

    private static Component getMessage(Component conversionTarget, ArgumentConverter<?> converter, FailedConversion<?> conversion) {
        TextComponent.Builder builder = (TextComponent.Builder)((TextComponent.Builder)TextComponent.builder("").append((Component)TextComponent.of("Invalid value for "))).append(conversionTarget);
        if (conversion.getError().getMessage() != null) {
            builder.append((Component)TextComponent.of(" (" + conversion.getError().getMessage() + ")"));
        }
        return ((TextComponent.Builder)((TextComponent.Builder)builder.append((Component)TextComponent.of(", acceptable values are "))).append(converter.describeAcceptableArguments())).build();
    }

    public ConversionFailedException(CommandParseResult parseResult, Component conversionTarget, ArgumentConverter<?> converter, FailedConversion<?> conversion) {
        super(ConversionFailedException.getMessage(conversionTarget, converter, conversion), parseResult);
        this.converter = converter;
        this.conversion = conversion;
        FailedConversionMapper.mapOnto(() -> this, conversion);
    }

    public FailedConversion<?> getConversion() {
        return this.conversion;
    }

    public Stream<Throwable> getAllErrors() {
        return Stream.concat(Stream.of(this.getCause()), Stream.of(this.getSuppressed()));
    }

    public ArgumentConverter<?> getConverter() {
        return this.converter;
    }
}

