/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.sk89q.worldedit.util.formatting.text.util;

import com.sk89q.worldedit.util.formatting.text.util.ShadyPines;
import java.util.Map;
import org.checkerframework.checker.nullness.qual.NonNull;

@Deprecated
public interface ToStringer {
    @Deprecated
    public static @NonNull String toString(@NonNull Object object, @NonNull Map<String, Object> builder) {
        return ShadyPines.toString(object, builder);
    }
}

