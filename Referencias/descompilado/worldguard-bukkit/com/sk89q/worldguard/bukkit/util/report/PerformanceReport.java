/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  com.sk89q.worldedit.util.report.DataReport
 *  org.bukkit.Bukkit
 *  org.bukkit.Chunk
 *  org.bukkit.World
 *  org.bukkit.block.BlockState
 *  org.bukkit.entity.Entity
 */
package com.sk89q.worldguard.bukkit.util.report;

import com.google.common.collect.Maps;
import com.sk89q.worldedit.util.report.DataReport;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Entity;

public class PerformanceReport
extends DataReport {
    public PerformanceReport() {
        super("Performance");
        List worlds = Bukkit.getServer().getWorlds();
        this.append("World Count", worlds.size());
        for (World world : worlds) {
            int loadedChunkCount = world.getLoadedChunks().length;
            DataReport report = new DataReport("World: " + world.getName());
            report.append("Keep in Memory?", world.getKeepSpawnInMemory());
            report.append("Entity Count", world.getEntities().size());
            report.append("Chunk Count", loadedChunkCount);
            HashMap entityCounts = Maps.newHashMap();
            HashMap tileEntityCounts = Maps.newHashMap();
            int teCount = 0;
            for (Chunk chunk : world.getLoadedChunks()) {
                teCount += chunk.getTileEntities().length;
                for (BlockState state : chunk.getTileEntities()) {
                    Class cls = state.getClass();
                    if (tileEntityCounts.containsKey(cls)) {
                        tileEntityCounts.put(cls, (Integer)tileEntityCounts.get(cls) + 1);
                        continue;
                    }
                    tileEntityCounts.put(cls, 1);
                }
            }
            report.append("Tile Entity Count", teCount);
            for (Entity entity : world.getEntities()) {
                Class cls = entity.getClass();
                if (entityCounts.containsKey(cls)) {
                    entityCounts.put(cls, (Integer)entityCounts.get(cls) + 1);
                    continue;
                }
                entityCounts.put(cls, 1);
            }
            DataReport entities = new DataReport("Entity Distribution");
            for (Map.Entry entry : entityCounts.entrySet()) {
                entities.append(((Class)entry.getKey()).getSimpleName(), "%d [%f/chunk]", new Object[]{entry.getValue(), Float.valueOf((float)((double)((Integer)entry.getValue()).intValue() / (double)loadedChunkCount))});
            }
            report.append(entities.getTitle(), (Object)entities);
            DataReport tileEntities = new DataReport("Tile Entity Distribution");
            for (Map.Entry entry : tileEntityCounts.entrySet()) {
                tileEntities.append(((Class)entry.getKey()).getSimpleName(), "%d [%f/chunk]", new Object[]{entry.getValue(), Float.valueOf((float)((double)((Integer)entry.getValue()).intValue() / (double)loadedChunkCount))});
            }
            report.append(tileEntities.getTitle(), (Object)tileEntities);
            this.append(report.getTitle(), report);
        }
    }
}

