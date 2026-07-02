/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.sk89q.worldedit.world.snapshot;

import java.io.File;
import java.util.Calendar;
import javax.annotation.Nullable;

public interface SnapshotDateParser {
    @Nullable
    public Calendar detectDate(File var1);
}

