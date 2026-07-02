/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.util.auth;

import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.util.formatting.text.Component;

public class AuthorizationException
extends WorldEditException {
    public AuthorizationException() {
    }

    public AuthorizationException(Component message) {
        super(message);
    }

    @Deprecated
    public AuthorizationException(String message) {
        super(message);
    }

    public AuthorizationException(Component message, Throwable cause) {
        super(message, cause);
    }

    @Deprecated
    public AuthorizationException(String message, Throwable cause) {
        super(message, cause);
    }

    public AuthorizationException(Throwable cause) {
        super(cause);
    }
}

