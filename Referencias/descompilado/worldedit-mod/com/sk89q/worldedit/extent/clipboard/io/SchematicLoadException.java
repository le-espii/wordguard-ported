/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.extent.clipboard.io;

import com.sk89q.worldedit.util.formatting.text.Component;

public final class SchematicLoadException
extends RuntimeException {
    private final Component message;

    public SchematicLoadException(Component message) {
        this.message = message;
    }

    public Component getRichMessage() {
        return this.message;
    }
}

