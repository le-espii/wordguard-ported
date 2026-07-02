/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit;

import com.sk89q.worldedit.WorldEditException;

@Deprecated
public class DisallowedItemException
extends WorldEditException {
    private final String type;

    public DisallowedItemException(String type) {
        this.type = type;
    }

    public DisallowedItemException(String type, String message) {
        super(message);
        this.type = type;
    }

    public String getID() {
        return this.type;
    }
}

