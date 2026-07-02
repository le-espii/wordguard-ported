/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.world.storage;

import com.sk89q.worldedit.math.BlockVector2;
import com.sk89q.worldedit.world.DataException;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldedit.world.storage.ChunkStore;
import com.sk89q.worldedit.world.storage.McRegionReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.enginehub.linbus.stream.LinBinaryIO;
import org.enginehub.linbus.tree.LinCompoundTag;
import org.enginehub.linbus.tree.LinRootEntry;

public abstract class McRegionChunkStore
extends ChunkStore {
    protected String curFilename = null;
    protected McRegionReader cachedReader = null;

    public static String getFilename(BlockVector2 position) {
        int x = position.x();
        int z = position.z();
        return "r." + (x >> 5) + "." + (z >> 5) + ".mca";
    }

    protected McRegionReader getReader(BlockVector2 pos, String worldname) throws DataException, IOException {
        String filename = McRegionChunkStore.getFilename(pos);
        if (this.curFilename != null) {
            if (this.curFilename.equals(filename)) {
                return this.cachedReader;
            }
            try {
                this.cachedReader.close();
            }
            catch (IOException iOException) {
                // empty catch block
            }
        }
        InputStream stream = this.getInputStream(filename, worldname);
        this.cachedReader = new McRegionReader(stream);
        return this.cachedReader;
    }

    @Override
    public LinCompoundTag getChunkData(BlockVector2 position, World world) throws DataException, IOException {
        McRegionReader reader = this.getReader(position, world.getName());
        try (DataInputStream chunkStream = new DataInputStream(reader.getChunkInputStream(position));){
            LinCompoundTag linCompoundTag = LinBinaryIO.readUsing(chunkStream, LinRootEntry::readFrom).value();
            return linCompoundTag;
        }
    }

    protected abstract InputStream getInputStream(String var1, String var2) throws IOException, DataException;

    @Override
    public void close() throws IOException {
        if (this.cachedReader != null) {
            this.cachedReader.close();
        }
    }
}

