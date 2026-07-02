/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.sk89q.worldedit.util.formatting.text.util;

import java.util.Collections;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;
import java.util.function.Consumer;
import org.checkerframework.checker.nullness.qual.NonNull;

public final class ShadyPines {
    private ShadyPines() {
    }

    @SafeVarargs
    public static <E extends Enum<E>> @NonNull Set<E> enumSet(Class<E> type, E ... constants) {
        EnumSet<E> set = EnumSet.noneOf(type);
        Collections.addAll(set, constants);
        return Collections.unmodifiableSet(set);
    }

    public static @NonNull String toString(@NonNull Object object, @NonNull Consumer<Map<String, Object>> consumer) {
        LinkedHashMap<String, Object> builder = new LinkedHashMap<String, Object>();
        consumer.accept(builder);
        return ShadyPines.toString(object, builder);
    }

    public static @NonNull String toString(@NonNull Object object, @NonNull Map<String, Object> builder) {
        StringJoiner joiner = new StringJoiner(", ", object.getClass().getSimpleName() + "{", "}");
        builder.forEach((key, value) -> joiner.add(key + "=" + value));
        return joiner.toString();
    }
}

