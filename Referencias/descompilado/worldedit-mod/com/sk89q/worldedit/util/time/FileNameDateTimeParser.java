/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Streams
 *  javax.annotation.Nullable
 */
package com.sk89q.worldedit.util.time;

import com.google.common.collect.Streams;
import com.sk89q.worldedit.util.io.file.MorePaths;
import com.sk89q.worldedit.util.time.SnapshotDateTimeParser;
import java.nio.file.Path;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.StreamSupport;
import javax.annotation.Nullable;

public class FileNameDateTimeParser
implements SnapshotDateTimeParser {
    private static final FileNameDateTimeParser INSTANCE = new FileNameDateTimeParser();
    private static final String SEP = "[ \\-_:]";
    private static final Pattern BASIC_FILTER = Pattern.compile("^(?<year>\\d{4})[ \\-_:](?<month>\\d{1,2})[ \\-_:](?<day>\\d{1,2})(?:[ \\-_:T](?<hour>\\d{1,2})[ \\-_:](?<minute>\\d{1,2})[ \\-_:](?<second>\\d{1,2}))?");

    public static FileNameDateTimeParser getInstance() {
        return INSTANCE;
    }

    private FileNameDateTimeParser() {
    }

    @Override
    @Nullable
    public ZonedDateTime detectDateTime(Path path) {
        Matcher matcher = Streams.findLast(StreamSupport.stream(MorePaths.optimizedSpliterator(path), false).map(p -> BASIC_FILTER.matcher(p.toString())).filter(Matcher::find)).orElse(null);
        if (matcher != null) {
            int year = FileNameDateTimeParser.matchAndParseOrZero(matcher, "year");
            int month = FileNameDateTimeParser.matchAndParseOrZero(matcher, "month");
            int day = FileNameDateTimeParser.matchAndParseOrZero(matcher, "day");
            int hour = FileNameDateTimeParser.matchAndParseOrZero(matcher, "hour");
            int minute = FileNameDateTimeParser.matchAndParseOrZero(matcher, "minute");
            int second = FileNameDateTimeParser.matchAndParseOrZero(matcher, "second");
            return ZonedDateTime.of(year, month, day, hour, minute, second, 0, ZoneId.systemDefault());
        }
        return null;
    }

    private static int matchAndParseOrZero(Matcher matcher, String group) {
        String match = matcher.group(group);
        if (match == null) {
            return 0;
        }
        return Integer.parseInt(match);
    }
}

