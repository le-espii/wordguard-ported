/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.sk89q.worldedit.util.formatting.text.util;

import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import org.checkerframework.checker.nullness.qual.NonNull;

public final class NameMap<E extends Enum<E>> {
    private final Map<String, E> nameToValue;
    private final Map<E, String> valueToName;

    private NameMap(Map<String, E> nameToValue, Map<E, String> valueToName) {
        this.nameToValue = nameToValue;
        this.valueToName = valueToName;
    }

    public static <E extends Enum<E>> @NonNull NameMap<E> create(Class<E> type, @NonNull Function<E, String> namer) {
        Enum[] constants = (Enum[])type.getEnumConstants();
        int length = constants.length;
        HashMap<String, Enum> nameToValue = new HashMap<String, Enum>(length);
        EnumMap<E, String> valueToName = new EnumMap<E, String>(type);
        for (int i = 0; i < length; ++i) {
            Enum constant = constants[i];
            String name = namer.apply(constant);
            nameToValue.put(name, constant);
            valueToName.put(constant, name);
        }
        return new NameMap(Collections.unmodifiableMap(nameToValue), Collections.unmodifiableMap(valueToName));
    }

    @Deprecated
    public static <E extends Enum<E>> @NonNull NameMap<E> create(E @NonNull [] constants, @NonNull Function<E, String> namer) {
        int length = constants.length;
        HashMap<String, E> nameToValue = new HashMap<String, E>(length);
        HashMap<E, String> valueToName = new HashMap<E, String>(length);
        for (int i = 0; i < length; ++i) {
            E constant = constants[i];
            String name = namer.apply(constant);
            nameToValue.put(name, constant);
            valueToName.put(constant, name);
        }
        return new NameMap(Collections.unmodifiableMap(nameToValue), Collections.unmodifiableMap(valueToName));
    }

    public @NonNull String name(@NonNull E value) {
        return this.valueToName.get(value);
    }

    public @NonNull Optional<E> value(@NonNull String name) {
        return Optional.ofNullable(this.nameToValue.get(name));
    }

    @Deprecated
    public @NonNull Optional<E> get(@NonNull String name) {
        return this.value(name);
    }
}

