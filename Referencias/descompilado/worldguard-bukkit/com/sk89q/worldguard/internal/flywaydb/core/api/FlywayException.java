/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldguard.internal.flywaydb.core.api;

public class FlywayException
extends RuntimeException {
    public FlywayException(String message, Throwable cause) {
        super(message, cause);
    }

    public FlywayException(String message) {
        super(message);
    }

    public FlywayException() {
    }
}

