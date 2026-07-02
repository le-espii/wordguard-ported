/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit;

import com.sk89q.worldedit.util.formatting.WorldEditText;
import com.sk89q.worldedit.util.formatting.text.Component;
import com.sk89q.worldedit.util.formatting.text.TextComponent;
import java.util.Locale;

public abstract class WorldEditException
extends Exception {
    private final Component message;

    protected WorldEditException() {
        this.message = null;
    }

    @Deprecated
    protected WorldEditException(String message) {
        super(message);
        this.message = TextComponent.of(message);
    }

    protected WorldEditException(Component message) {
        super(WorldEditText.reduceToText(message, Locale.getDefault()));
        this.message = message;
    }

    @Deprecated
    protected WorldEditException(String message, Throwable cause) {
        super(message, cause);
        this.message = TextComponent.of(message);
    }

    protected WorldEditException(Component message, Throwable cause) {
        super(WorldEditText.reduceToText(message, Locale.getDefault()), cause);
        this.message = message;
    }

    protected WorldEditException(Throwable cause) {
        super(cause);
        this.message = null;
    }

    public Component getRichMessage() {
        return this.message;
    }
}

