/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.world.snapshot.experimental;

import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.DataException;
import com.sk89q.worldedit.world.chunk.Chunk;
import com.sk89q.worldedit.world.snapshot.experimental.SnapshotInfo;
import com.sk89q.worldedit.world.storage.ChunkStoreHelper;
import java.io.Closeable;
import java.io.IOException;
import org.enginehub.linbus.tree.LinCompoundTag;

public interface Snapshot
extends Closeable {
    public SnapshotInfo getInfo();

    public LinCompoundTag getChunkTag(BlockVector3 var1) throws DataException, IOException;

    default public Chunk getChunk(BlockVector3 position) throws DataException, IOException {
        return ChunkStoreHelper.getChunk(this.getChunkTag(position));
    }

    @Override
    public void close() throws IOException;
}

