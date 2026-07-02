/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Chunk
 *  org.bukkit.World
 */
package com.sk89q.worldguard.bukkit.paperlib.features.asyncchunks;

import com.sk89q.worldguard.bukkit.paperlib.PaperLib;
import com.sk89q.worldguard.bukkit.paperlib.features.asyncchunks.AsyncChunks;
import java.util.concurrent.CompletableFuture;
import org.bukkit.Chunk;
import org.bukkit.World;

public class AsyncChunksSync
implements AsyncChunks {
    @Override
    public CompletableFuture<Chunk> getChunkAtAsync(World world, int x, int z, boolean gen, boolean isUrgent) {
        if (!gen && !PaperLib.isChunkGenerated(world, x, z)) {
            return CompletableFuture.completedFuture(null);
        }
        return CompletableFuture.completedFuture(world.getChunkAt(x, z));
    }
}

