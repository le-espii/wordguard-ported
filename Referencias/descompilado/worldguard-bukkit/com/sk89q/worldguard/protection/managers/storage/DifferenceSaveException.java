/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldguard.protection.managers.storage;

import com.sk89q.worldguard.protection.managers.storage.StorageException;

public class DifferenceSaveException
extends StorageException {
    public DifferenceSaveException() {
    }

    public DifferenceSaveException(String message) {
        super(message);
    }

    public DifferenceSaveException(String message, Throwable cause) {
        super(message, cause);
    }

    public DifferenceSaveException(Throwable cause) {
        super(cause);
    }
}

