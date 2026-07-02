/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.world.storage;

import com.sk89q.worldedit.math.BlockVector2;
import com.sk89q.worldedit.util.io.ForwardSeekableInputStream;
import com.sk89q.worldedit.world.DataException;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.InflaterInputStream;

public class McRegionReader {
    protected static final int VERSION_GZIP = 1;
    protected static final int VERSION_DEFLATE = 2;
    protected static final int SECTOR_BYTES = 4096;
    protected static final int SECTOR_INTS = 1024;
    public static final int CHUNK_HEADER_SIZE = 5;
    protected ForwardSeekableInputStream stream;
    protected DataInputStream dataStream;
    protected int[] offsets;

    public McRegionReader(InputStream stream) throws IOException {
        this.stream = new ForwardSeekableInputStream(stream);
        this.dataStream = new DataInputStream(this.stream);
        this.readHeader();
    }

    private void readHeader() throws IOException {
        this.offsets = new int[1024];
        for (int i = 0; i < 1024; ++i) {
            int offset;
            this.offsets[i] = offset = this.dataStream.readInt();
        }
    }

    public synchronized InputStream getChunkInputStream(BlockVector2 position) throws IOException, DataException {
        int z;
        int x = position.x() & 0x1F;
        int offset = this.getOffset(x, z = position.z() & 0x1F);
        if (offset == 0) {
            throw new DataException("The chunk at " + String.valueOf((Object)position) + " is not generated");
        }
        int sectorNumber = offset >> 8;
        int numSectors = offset & 0xFF;
        this.stream.seek((long)sectorNumber * 4096L);
        int length = this.dataStream.readInt();
        if (length > 4096 * numSectors) {
            throw new DataException("MCRegion chunk at " + x + "," + z + " has an invalid length of " + length);
        }
        byte version = this.dataStream.readByte();
        if (version == 1) {
            byte[] data = new byte[length - 1];
            if (this.dataStream.read(data) < length - 1) {
                throw new DataException("MCRegion file does not contain " + x + "," + z + " in full");
            }
            return new GZIPInputStream(new ByteArrayInputStream(data));
        }
        if (version == 2) {
            byte[] data = new byte[length - 1];
            if (this.dataStream.read(data) < length - 1) {
                throw new DataException("MCRegion file does not contain " + x + "," + z + " in full");
            }
            return new InflaterInputStream(new ByteArrayInputStream(data));
        }
        throw new DataException("MCRegion chunk at " + x + "," + z + " has an unsupported version of " + version);
    }

    private int getOffset(int x, int z) {
        return this.offsets[x + z * 32];
    }

    public boolean hasChunk(int x, int z) {
        return this.getOffset(x, z) != 0;
    }

    public void close() throws IOException {
        this.stream.close();
    }
}

