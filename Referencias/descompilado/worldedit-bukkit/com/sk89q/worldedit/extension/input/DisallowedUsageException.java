/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.extension.input;

import com.sk89q.worldedit.extension.input.InputParseException;
import com.sk89q.worldedit.util.formatting.text.Component;

public class DisallowedUsageException
extends InputParseException {
    public DisallowedUsageException(Component message) {
        super(message);
    }

    @Deprecated
    public DisallowedUsageException(String message) {
        super(message);
    }

    public DisallowedUsageException(Component message, Throwable cause) {
        super(message, cause);
    }

    @Deprecated
    public DisallowedUsageException(String message, Throwable cause) {
        super(message, cause);
    }
}

