/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit;

import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.util.formatting.text.TranslatableComponent;

public class MissingWorldException
extends WorldEditException {
    public MissingWorldException() {
        super(TranslatableComponent.of("worldedit.error.missing-world"));
    }
}

