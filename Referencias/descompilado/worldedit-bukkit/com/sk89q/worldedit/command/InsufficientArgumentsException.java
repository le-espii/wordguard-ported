/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.command;

import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.util.formatting.text.Component;

public class InsufficientArgumentsException
extends WorldEditException {
    @Deprecated
    public InsufficientArgumentsException(String error) {
        super(error);
    }

    public InsufficientArgumentsException(Component error) {
        super(error);
    }
}

