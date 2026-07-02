/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.sk89q.worldedit.util.kyori;

import com.sk89q.worldedit.util.kyori.Key;
import java.util.Objects;
import java.util.function.IntPredicate;
import org.checkerframework.checker.nullness.qual.NonNull;

final class KeyImpl
implements Key {
    private static final IntPredicate NAMESPACE_PREDICATE = value -> value == 95 || value == 45 || value >= 97 && value <= 122 || value >= 48 && value <= 57 || value == 46;
    private static final IntPredicate VALUE_PREDICATE = value -> value == 95 || value == 45 || value >= 97 && value <= 122 || value >= 48 && value <= 57 || value == 47 || value == 46;
    private final String namespace;
    private final String value;

    KeyImpl(@NonNull String namespace, @NonNull String value) {
        if (!KeyImpl.namespaceValid(namespace)) {
            throw new Key.ParseException("Non [a-z0-9_.-] character in namespace of location: " + KeyImpl.asString(namespace, value));
        }
        if (!KeyImpl.valueValid(value)) {
            throw new Key.ParseException("Non [a-z0-9/._-] character in path of location: " + KeyImpl.asString(namespace, value));
        }
        this.namespace = namespace;
        this.value = value;
    }

    static boolean namespaceValid(@NonNull String namespace) {
        return namespace.chars().allMatch(NAMESPACE_PREDICATE);
    }

    static boolean valueValid(@NonNull String value) {
        return value.chars().allMatch(VALUE_PREDICATE);
    }

    @Override
    public @NonNull String namespace() {
        return this.namespace;
    }

    @Override
    public @NonNull String value() {
        return this.value;
    }

    @Override
    public @NonNull String asString() {
        return KeyImpl.asString(this.namespace, this.value);
    }

    private static @NonNull String asString(@NonNull String namespace, @NonNull String value) {
        return namespace + ':' + value;
    }

    public @NonNull String toString() {
        return this.asString();
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Key)) {
            return false;
        }
        Key that = (Key)other;
        return Objects.equals(this.namespace, that.namespace()) && Objects.equals(this.value, that.value());
    }

    public int hashCode() {
        int result = this.namespace.hashCode();
        result = 31 * result + this.value.hashCode();
        return result;
    }

    @Override
    public int compareTo(@NonNull Key that) {
        int value = this.value.compareTo(that.value());
        if (value != 0) {
            return KeyImpl.clampCompare(value);
        }
        return KeyImpl.clampCompare(this.namespace.compareTo(that.namespace()));
    }

    private static int clampCompare(int value) {
        if (value < 0) {
            return -1;
        }
        if (value > 0) {
            return 1;
        }
        return value;
    }
}

