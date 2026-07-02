/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit;

import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.util.formatting.text.TextComponent;
import com.sk89q.worldedit.util.formatting.text.TranslatableComponent;

public class UnknownDirectionException
extends WorldEditException {
    private final String dir;

    public UnknownDirectionException(String dir) {
        super(TranslatableComponent.of("worldedit.error.unknown-direction", TextComponent.of(dir)));
        this.dir = dir;
    }

    public String getDirection() {
        return this.dir;
    }
}

