/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.sk89q.worldedit.util.formatting.text.serializer.legacy;

import com.sk89q.worldedit.util.formatting.text.TextComponent;
import com.sk89q.worldedit.util.formatting.text.serializer.legacy.AbstractLegacyComponentSerializer;
import com.sk89q.worldedit.util.formatting.text.serializer.legacy.LegacyComponentSerializer;
import org.checkerframework.checker.nullness.qual.NonNull;

final class LegacyComponentSerializerImpl
extends AbstractLegacyComponentSerializer {
    static final LegacyComponentSerializer INSTANCE = new LegacyComponentSerializerImpl();

    LegacyComponentSerializerImpl() {
    }

    @Override
    protected @NonNull TextComponent finish(@NonNull TextComponent component) {
        return component;
    }
}

