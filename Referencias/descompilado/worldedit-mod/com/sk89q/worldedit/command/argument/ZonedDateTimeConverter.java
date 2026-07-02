/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.command.argument;

import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.util.formatting.text.Component;
import com.sk89q.worldedit.util.formatting.text.TextComponent;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Calendar;
import org.enginehub.piston.CommandManager;
import org.enginehub.piston.converter.ArgumentConverter;
import org.enginehub.piston.converter.ConversionResult;
import org.enginehub.piston.converter.FailedConversion;
import org.enginehub.piston.converter.SuccessfulConversion;
import org.enginehub.piston.inject.InjectedValueAccess;
import org.enginehub.piston.inject.Key;

public class ZonedDateTimeConverter
implements ArgumentConverter<ZonedDateTime> {
    public static void register(CommandManager commandManager) {
        commandManager.registerConverter(Key.of(ZonedDateTime.class), new ZonedDateTimeConverter());
    }

    private ZonedDateTimeConverter() {
    }

    @Override
    public Component describeAcceptableArguments() {
        return TextComponent.of("any date");
    }

    @Override
    public ConversionResult<ZonedDateTime> convert(String argument, InjectedValueAccess context) {
        LocalSession session = context.injectedValue(Key.of(LocalSession.class)).orElseThrow(() -> new IllegalStateException("Need a local session"));
        Calendar date = session.detectDate(argument);
        if (date == null) {
            return FailedConversion.from(new IllegalArgumentException("Not a date: " + argument));
        }
        return SuccessfulConversion.fromSingle(date.toInstant().atZone(ZoneOffset.UTC));
    }
}

