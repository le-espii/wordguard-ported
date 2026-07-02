/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.util.formatting.component;

import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.util.formatting.text.Component;

public class InvalidComponentException
extends WorldEditException {
    @Deprecated
    public InvalidComponentException(String message) {
        super(message);
    }

    public InvalidComponentException(Component message) {
        super(message);
    }
}

