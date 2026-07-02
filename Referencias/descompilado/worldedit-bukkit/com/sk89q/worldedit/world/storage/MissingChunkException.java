/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.world.storage;

import com.sk89q.worldedit.math.Vector2;
import com.sk89q.worldedit.world.storage.ChunkStoreException;

public class MissingChunkException
extends ChunkStoreException {
    private Vector2 position;

    public MissingChunkException() {
    }

    public MissingChunkException(Vector2 position) {
        this.position = position;
    }

    public Vector2 getChunkPosition() {
        return this.position;
    }
}

