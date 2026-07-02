/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.sk89q.worldguard.protection.flags;

import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.FlagContext;
import com.sk89q.worldguard.protection.flags.InvalidFlagFormat;
import com.sk89q.worldguard.protection.flags.RegionGroup;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.temporal.TemporalAccessor;
import javax.annotation.Nullable;

public class TimestampFlag
extends Flag<Instant> {
    private static final DateTimeFormatter SERIALIZER = DateTimeFormatter.ISO_DATE_TIME.withZone(ZoneOffset.UTC);
    private static final DateTimeFormatter PARSER = new DateTimeFormatterBuilder().append(DateTimeFormatter.ISO_LOCAL_DATE_TIME).optionalStart().appendOffsetId().toFormatter();

    public TimestampFlag(String name, @Nullable RegionGroup defaultGroup) {
        super(name, defaultGroup);
    }

    public TimestampFlag(String name) {
        super(name);
    }

    @Override
    public Instant parseInput(FlagContext context) throws InvalidFlagFormat {
        String input = context.getUserInput();
        if ("now".equalsIgnoreCase(input)) {
            return Instant.now();
        }
        try {
            TemporalAccessor parsed = PARSER.parseBest(input, ZonedDateTime::from, LocalDateTime::from);
            if (parsed instanceof LocalDateTime) {
                return ((LocalDateTime)parsed).atZone(ZoneOffset.UTC).toInstant();
            }
            if (parsed instanceof ZonedDateTime) {
                return ((ZonedDateTime)parsed).toInstant();
            }
            throw new InvalidFlagFormat("Unrecognized input.");
        }
        catch (DateTimeParseException ignored) {
            throw new InvalidFlagFormat("Expected 'now' or ISO 8601 formatted input.");
        }
    }

    @Override
    public Instant unmarshal(@Nullable Object o) {
        if (o instanceof String) {
            try {
                return Instant.from(SERIALIZER.parse((String)o));
            }
            catch (DateTimeParseException ignored) {
                return null;
            }
        }
        return null;
    }

    @Override
    public Object marshal(Instant o) {
        return SERIALIZER.format(o);
    }
}

