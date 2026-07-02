/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Chunk
 *  org.bukkit.World
 *  org.bukkit.World$ChunkLoadCallback
 */
package com.sk89q.worldguard.bukkit.paperlib.features.asyncchunks;

import com.sk89q.worldguard.bukkit.paperlib.PaperLib;
import com.sk89q.worldguard.bukkit.paperlib.features.asyncchunks.AsyncChunks;
import java.util.concurrent.CompletableFuture;
import org.bukkit.Chunk;
import org.bukkit.World;

public class AsyncChunksPaper_9_12
implements AsyncChunks {
    @Override
    public CompletableFuture<Chunk> getChunkAtAsync(World world, int x, int z, boolean gen, boolean isUrgent) {
        CompletableFuture<Chunk> future = new CompletableFuture<Chunk>();
        if (!gen && PaperLib.getMinecraftVersion() >= 12 && !world.isChunkGenerated(x, z)) {
            future.complete(null);
        } else {
            World.ChunkLoadCallback chunkLoadCallback = future::complete;
            world.getChunkAtAsync(x, z, chunkLoadCallback);
        }
        return future;
    }
}

