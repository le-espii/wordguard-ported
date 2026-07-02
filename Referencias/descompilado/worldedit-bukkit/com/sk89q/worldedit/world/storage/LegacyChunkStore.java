/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.world.storage;

import com.sk89q.worldedit.math.BlockVector2;
import com.sk89q.worldedit.world.DataException;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldedit.world.storage.ChunkStore;
import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;
import org.enginehub.linbus.stream.LinBinaryIO;
import org.enginehub.linbus.tree.LinCompoundTag;
import org.enginehub.linbus.tree.LinRootEntry;

public abstract class LegacyChunkStore
extends ChunkStore {
    public static String getFilename(BlockVector2 position, String separator) {
        int x = position.x();
        int z = position.z();
        String folder1 = Integer.toString(LegacyChunkStore.divisorMod(x, 64), 36);
        String folder2 = Integer.toString(LegacyChunkStore.divisorMod(z, 64), 36);
        String filename = "c." + Integer.toString(x, 36) + "." + Integer.toString(z, 36) + ".dat";
        return folder1 + separator + folder2 + separator + filename;
    }

    public static String getFilename(BlockVector2 position) {
        return LegacyChunkStore.getFilename(position, File.separator);
    }

    @Override
    public LinCompoundTag getChunkData(BlockVector2 position, World world) throws DataException, IOException {
        int x = position.x();
        int z = position.z();
        String folder1 = Integer.toString(LegacyChunkStore.divisorMod(x, 64), 36);
        String folder2 = Integer.toString(LegacyChunkStore.divisorMod(z, 64), 36);
        String filename = "c." + Integer.toString(x, 36) + "." + Integer.toString(z, 36) + ".dat";
        try (DataInputStream chunkStream = new DataInputStream(new GZIPInputStream(this.getInputStream(folder1, folder2, filename)));){
            LinCompoundTag linCompoundTag = LinBinaryIO.readUsing(chunkStream, LinRootEntry::readFrom).toLinTag();
            return linCompoundTag;
        }
    }

    private static int divisorMod(int a, int n) {
        return (int)((double)a - (double)n * Math.floor(Math.floor(a) / (double)n));
    }

    protected abstract InputStream getInputStream(String var1, String var2, String var3) throws IOException, DataException;
}

