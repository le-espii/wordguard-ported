/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.World
 */
package com.sk89q.worldedit.bukkit.paperlib.features.chunkisgenerated;

import com.sk89q.worldedit.bukkit.paperlib.features.chunkisgenerated.ChunkIsGenerated;
import org.bukkit.World;

public class ChunkIsGeneratedApiExists
implements ChunkIsGenerated {
    @Override
    public boolean isChunkGenerated(World world, int x, int z) {
        return world.isChunkGenerated(x, z);
    }
}

