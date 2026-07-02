/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.util.time;

import com.sk89q.worldedit.util.time.SnapshotDateTimeParser;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class ModificationDateTimeParser
implements SnapshotDateTimeParser {
    private static final ModificationDateTimeParser INSTANCE = new ModificationDateTimeParser();

    public static ModificationDateTimeParser getInstance() {
        return INSTANCE;
    }

    private ModificationDateTimeParser() {
    }

    @Override
    public ZonedDateTime detectDateTime(Path path) {
        if (!Files.exists(path, new LinkOption[0])) {
            return null;
        }
        try {
            return Files.getLastModifiedTime(path, new LinkOption[0]).toInstant().atZone(ZoneId.systemDefault());
        }
        catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}

