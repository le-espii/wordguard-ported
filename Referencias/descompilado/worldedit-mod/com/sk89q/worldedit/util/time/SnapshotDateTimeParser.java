/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.sk89q.worldedit.util.time;

import java.nio.file.Path;
import java.time.ZonedDateTime;
import javax.annotation.Nullable;

public interface SnapshotDateTimeParser {
    @Nullable
    public ZonedDateTime detectDateTime(Path var1);
}

