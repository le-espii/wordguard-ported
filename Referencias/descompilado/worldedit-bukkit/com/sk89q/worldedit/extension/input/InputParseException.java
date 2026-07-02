/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.extension.input;

import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.util.formatting.text.Component;

public class InputParseException
extends WorldEditException {
    public InputParseException(Component message) {
        super(message);
    }

    @Deprecated
    public InputParseException(String message) {
        super(message);
    }

    public InputParseException(Component message, Throwable cause) {
        super(message, cause);
    }

    @Deprecated
    public InputParseException(String message, Throwable cause) {
        super(message, cause);
    }
}

