/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit;

import com.sk89q.worldedit.DisallowedItemException;

@Deprecated
public class InvalidItemException
extends DisallowedItemException {
    public InvalidItemException(String type, String message) {
        super(type, message);
    }
}

