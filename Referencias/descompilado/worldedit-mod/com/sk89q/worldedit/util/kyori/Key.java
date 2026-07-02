/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.sk89q.worldedit.util.kyori;

import com.sk89q.worldedit.util.kyori.KeyImpl;
import org.checkerframework.checker.nullness.qual.NonNull;

public interface Key
extends Comparable<Key> {
    public static final String MINECRAFT_NAMESPACE = "minecraft";

    public static @NonNull Key of(@NonNull String string) {
        return Key.of(string, ':');
    }

    public static @NonNull Key of(@NonNull String string, char character) {
        int index = string.indexOf(character);
        String namespace = index >= 1 ? string.substring(0, index) : MINECRAFT_NAMESPACE;
        String value = index >= 0 ? string.substring(index + 1) : string;
        return Key.of(namespace, value);
    }

    public static @NonNull Key of(@NonNull String namespace, @NonNull String value) {
        return new KeyImpl(namespace, value);
    }

    public @NonNull String namespace();

    public @NonNull String value();

    public @NonNull String asString();

    public static final class ParseException
    extends RuntimeException {
        ParseException(String message) {
            super(message);
        }
    }
}

