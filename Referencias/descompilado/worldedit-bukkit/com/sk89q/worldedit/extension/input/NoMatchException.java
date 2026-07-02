/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.extension.input;

import com.sk89q.worldedit.extension.input.InputParseException;
import com.sk89q.worldedit.util.formatting.text.Component;

public class NoMatchException
extends InputParseException {
    public NoMatchException(Component message) {
        super(message);
    }

    @Deprecated
    public NoMatchException(String message) {
        super(message);
    }

    public NoMatchException(Component message, Throwable cause) {
        super(message, cause);
    }

    @Deprecated
    public NoMatchException(String message, Throwable cause) {
        super(message, cause);
    }
}

