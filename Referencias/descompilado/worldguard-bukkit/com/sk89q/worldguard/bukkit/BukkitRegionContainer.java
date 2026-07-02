/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.sk89q.worldedit.bukkit.BukkitAdapter
 *  com.sk89q.worldedit.bukkit.BukkitWorld
 *  com.sk89q.worldedit.math.BlockVector2
 *  com.sk89q.worldedit.world.World
 *  javax.annotation.Nullable
 *  org.bukkit.Bukkit
 *  org.bukkit.Chunk
 *  org.bukkit.World
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.Listener
 *  org.bukkit.event.world.ChunkLoadEvent
 *  org.bukkit.event.world.ChunkUnloadEvent
 *  org.bukkit.event.world.WorldLoadEvent
 *  org.bukkit.event.world.WorldUnloadEvent
 *  org.bukkit.plugin.Plugin
 */
package com.sk89q.worldguard.bukkit;

import com.google.common.base.Preconditions;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.math.BlockVector2;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.config.WorldConfiguration;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import java.util.ArrayList;
import javax.annotation.Nullable;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.world.WorldUnloadEvent;
import org.bukkit.plugin.Plugin;

public class BukkitRegionContainer
extends RegionContainer {
    private static final int CACHE_INVALIDATION_INTERVAL = 2;
    private final WorldGuardPlugin plugin;

    public BukkitRegionContainer(WorldGuardPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void initialize() {
        super.initialize();
        Bukkit.getPluginManager().registerEvents(new Listener(){

            @EventHandler
            public void onWorldLoad(WorldLoadEvent event) {
                BukkitRegionContainer.this.load(BukkitAdapter.adapt((org.bukkit.World)event.getWorld()));
            }

            @EventHandler
            public void onWorldUnload(WorldUnloadEvent event) {
                BukkitRegionContainer.this.unload(BukkitAdapter.adapt((org.bukkit.World)event.getWorld()));
            }

            @EventHandler
            public void onChunkLoad(ChunkLoadEvent event) {
                RegionManager manager = BukkitRegionContainer.this.get(BukkitAdapter.adapt((org.bukkit.World)event.getWorld()));
                if (manager != null) {
                    Chunk chunk = event.getChunk();
                    manager.loadChunk(BlockVector2.at((int)chunk.getX(), (int)chunk.getZ()));
                }
            }

            @EventHandler
            public void onChunkUnload(ChunkUnloadEvent event) {
                RegionManager manager = BukkitRegionContainer.this.get(BukkitAdapter.adapt((org.bukkit.World)event.getWorld()));
                if (manager != null) {
                    Chunk chunk = event.getChunk();
                    manager.unloadChunk(BlockVector2.at((int)chunk.getX(), (int)chunk.getZ()));
                }
            }
        }, (Plugin)this.plugin);
        Bukkit.getScheduler().scheduleSyncRepeatingTask((Plugin)this.plugin, this.cache::invalidateAll, 2L, 2L);
    }

    public void shutdown() {
        this.container.shutdown();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    @Nullable
    protected RegionManager load(World world) {
        RegionManager manager;
        Preconditions.checkNotNull((Object)world);
        WorldConfiguration config = WorldGuard.getInstance().getPlatform().getGlobalStateManager().get(world);
        if (!config.useRegions) {
            return null;
        }
        Object object = this.lock;
        synchronized (object) {
            manager = this.container.load(world.getName());
            if (manager != null) {
                ArrayList<BlockVector2> positions = new ArrayList<BlockVector2>();
                for (Chunk chunk : ((BukkitWorld)world).getWorld().getLoadedChunks()) {
                    positions.add(BlockVector2.at((int)chunk.getX(), (int)chunk.getZ()));
                }
                manager.loadChunks(positions);
            }
        }
        return manager;
    }
}

