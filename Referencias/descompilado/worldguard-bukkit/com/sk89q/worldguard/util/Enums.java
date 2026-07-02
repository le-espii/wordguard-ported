/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  javax.annotation.Nullable
 */
package com.sk89q.worldguard.util;

import com.google.common.base.Preconditions;
import javax.annotation.Nullable;

public final class Enums {
    private Enums() {
    }

    @Nullable
    public static <T extends Enum<T>> T findByValue(Class<T> enumType, String ... values) {
        Preconditions.checkNotNull(enumType);
        Preconditions.checkNotNull((Object)values);
        for (String val : values) {
            try {
                return Enum.valueOf(enumType, val);
            }
            catch (IllegalArgumentException illegalArgumentException) {
            }
        }
        return null;
    }

    @Nullable
    public static <T extends Enum<T>> T findFuzzyByValue(Class<T> enumType, String ... values) {
        Preconditions.checkNotNull(enumType);
        Preconditions.checkNotNull((Object)values);
        for (String test : values) {
            test = test.replace("_", "");
            for (Enum value : (Enum[])enumType.getEnumConstants()) {
                if (!value.name().replace("_", "").equalsIgnoreCase(test)) continue;
                return (T)value;
            }
        }
        return null;
    }
}

