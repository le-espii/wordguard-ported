/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.world;

import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.util.formatting.text.TranslatableComponent;

public class WorldUnloadedException
extends WorldEditException {
    public WorldUnloadedException() {
        super(TranslatableComponent.of("worldedit.error.world-unloaded"));
    }
}

