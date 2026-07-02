/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.sk89q.worldedit.util.formatting.text.serializer.legacy;

import com.sk89q.worldedit.util.formatting.text.Component;
import com.sk89q.worldedit.util.formatting.text.TextComponent;
import com.sk89q.worldedit.util.formatting.text.format.Style;
import com.sk89q.worldedit.util.formatting.text.serializer.ComponentSerializer;
import com.sk89q.worldedit.util.formatting.text.serializer.legacy.LegacyComponentSerializerImpl;
import com.sk89q.worldedit.util.formatting.text.serializer.legacy.LinkingLegacyComponentSerializer;
import java.util.Objects;
import org.checkerframework.checker.nullness.qual.NonNull;

public interface LegacyComponentSerializer
extends ComponentSerializer<Component, TextComponent, String> {
    @Deprecated
    public static final LegacyComponentSerializer INSTANCE = LegacyComponentSerializerImpl.INSTANCE;
    public static final char CHARACTER = '\u00a7';

    public static @NonNull LegacyComponentSerializer legacy() {
        return LegacyComponentSerializerImpl.INSTANCE;
    }

    public static @NonNull LegacyComponentSerializer legacyLinking() {
        return LinkingLegacyComponentSerializer.NO_STYLE;
    }

    public static @NonNull LegacyComponentSerializer legacyLinking(@NonNull Style style) {
        return new LinkingLegacyComponentSerializer(Objects.requireNonNull(style));
    }

    @Override
    default public @NonNull TextComponent deserialize(@NonNull String input) {
        return this.deserialize(input, '\u00a7');
    }

    public @NonNull TextComponent deserialize(@NonNull String var1, char var2);

    @Override
    default public @NonNull String serialize(@NonNull Component component) {
        return this.serialize(component, '\u00a7');
    }

    public @NonNull String serialize(@NonNull Component var1, char var2);
}

