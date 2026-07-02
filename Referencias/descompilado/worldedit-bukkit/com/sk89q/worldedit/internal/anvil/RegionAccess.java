/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.internal.anvil;

import com.sk89q.worldedit.math.BlockVector2;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Path;

class RegionAccess
implements AutoCloseable {
    private final RandomAccessFile raf;
    private int[] offsets;
    private int[] timestamps;

    RegionAccess(Path file) throws IOException {
        this(file, false);
    }

    RegionAccess(Path file, boolean preload) throws IOException {
        this.raf = new RandomAccessFile(file.toFile(), "rw");
        if (preload) {
            this.readHeaders();
        }
    }

    private void readHeaders() throws IOException {
        int idx;
        this.offsets = new int[1024];
        this.timestamps = new int[1024];
        for (idx = 0; idx < 1024; ++idx) {
            this.offsets[idx] = this.raf.readInt();
        }
        for (idx = 0; idx < 1024; ++idx) {
            this.timestamps[idx] = this.raf.readInt();
        }
    }

    private static int indexChunk(BlockVector2 pos) {
        int x = pos.x() & 0x1F;
        int z = pos.z() & 0x1F;
        return x + z * 32;
    }

    int getModificationTime(BlockVector2 pos) throws IOException {
        int idx = RegionAccess.indexChunk(pos);
        if (this.timestamps != null) {
            return this.timestamps[idx];
        }
        this.raf.seek((long)idx * 4L + 4096L);
        return this.raf.readInt();
    }

    int getChunkSize(BlockVector2 pos) throws IOException {
        int idx = RegionAccess.indexChunk(pos);
        if (this.offsets != null) {
            return this.offsets[idx] & 0xFF;
        }
        this.raf.seek((long)idx * 4L);
        this.raf.read();
        this.raf.read();
        this.raf.read();
        return this.raf.read();
    }

    void deleteChunk(BlockVector2 pos) throws IOException {
        int idx = RegionAccess.indexChunk(pos);
        this.raf.seek((long)idx * 4L);
        this.raf.writeInt(0);
        if (this.offsets != null) {
            this.offsets[idx] = 0;
        }
    }

    @Override
    public void close() throws IOException {
        this.raf.close();
    }
}

