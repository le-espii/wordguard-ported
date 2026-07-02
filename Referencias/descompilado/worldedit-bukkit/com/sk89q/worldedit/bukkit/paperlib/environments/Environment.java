/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.Chunk
 *  org.bukkit.Location
 *  org.bukkit.World
 *  org.bukkit.block.Block
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.Player
 *  org.bukkit.event.player.PlayerTeleportEvent$TeleportCause
 *  org.bukkit.inventory.Inventory
 */
package com.sk89q.worldedit.bukkit.paperlib.environments;

import com.sk89q.worldedit.bukkit.paperlib.features.asyncchunks.AsyncChunks;
import com.sk89q.worldedit.bukkit.paperlib.features.asyncchunks.AsyncChunksSync;
import com.sk89q.worldedit.bukkit.paperlib.features.asyncteleport.AsyncTeleport;
import com.sk89q.worldedit.bukkit.paperlib.features.asyncteleport.AsyncTeleportSync;
import com.sk89q.worldedit.bukkit.paperlib.features.bedspawnlocation.BedSpawnLocation;
import com.sk89q.worldedit.bukkit.paperlib.features.bedspawnlocation.BedSpawnLocationSync;
import com.sk89q.worldedit.bukkit.paperlib.features.blockstatesnapshot.BlockStateSnapshot;
import com.sk89q.worldedit.bukkit.paperlib.features.blockstatesnapshot.BlockStateSnapshotBeforeSnapshots;
import com.sk89q.worldedit.bukkit.paperlib.features.blockstatesnapshot.BlockStateSnapshotNoOption;
import com.sk89q.worldedit.bukkit.paperlib.features.blockstatesnapshot.BlockStateSnapshotResult;
import com.sk89q.worldedit.bukkit.paperlib.features.chunkisgenerated.ChunkIsGenerated;
import com.sk89q.worldedit.bukkit.paperlib.features.chunkisgenerated.ChunkIsGeneratedApiExists;
import com.sk89q.worldedit.bukkit.paperlib.features.chunkisgenerated.ChunkIsGeneratedUnknown;
import com.sk89q.worldedit.bukkit.paperlib.features.inventoryholdersnapshot.InventoryHolderSnapshot;
import com.sk89q.worldedit.bukkit.paperlib.features.inventoryholdersnapshot.InventoryHolderSnapshotBeforeSnapshots;
import com.sk89q.worldedit.bukkit.paperlib.features.inventoryholdersnapshot.InventoryHolderSnapshotNoOption;
import com.sk89q.worldedit.bukkit.paperlib.features.inventoryholdersnapshot.InventoryHolderSnapshotResult;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.Inventory;

public abstract class Environment {
    private final int minecraftVersion;
    private final int minecraftPatchVersion;
    private final int minecraftPreReleaseVersion;
    private final int minecraftReleaseCandidateVersion;
    protected AsyncChunks asyncChunksHandler = new AsyncChunksSync();
    protected AsyncTeleport asyncTeleportHandler = new AsyncTeleportSync();
    protected ChunkIsGenerated isGeneratedHandler = new ChunkIsGeneratedUnknown();
    protected BlockStateSnapshot blockStateSnapshotHandler;
    protected InventoryHolderSnapshot inventoryHolderSnapshotHandler;
    protected BedSpawnLocation bedSpawnLocationHandler = new BedSpawnLocationSync();

    public Environment() {
        this(Bukkit.getVersion());
    }

    Environment(String bukkitVersion) {
        Pattern versionPattern = Pattern.compile("(?i)\\(MC: (\\d)\\.(\\d+)\\.?(\\d+?)?(?: (Pre-Release|Release Candidate) )?(\\d)?\\)");
        Matcher matcher = versionPattern.matcher(bukkitVersion);
        int version = 0;
        int patchVersion = 0;
        int preReleaseVersion = -1;
        int releaseCandidateVersion = -1;
        if (matcher.find()) {
            MatchResult matchResult = matcher.toMatchResult();
            try {
                version = Integer.parseInt(matchResult.group(2), 10);
            }
            catch (Exception exception) {
                // empty catch block
            }
            if (matchResult.groupCount() >= 3) {
                try {
                    patchVersion = Integer.parseInt(matchResult.group(3), 10);
                }
                catch (Exception exception) {
                    // empty catch block
                }
            }
            if (matchResult.groupCount() >= 5) {
                try {
                    int ver = Integer.parseInt(matcher.group(5));
                    if (matcher.group(4).toLowerCase(Locale.ENGLISH).contains("pre")) {
                        preReleaseVersion = ver;
                    } else {
                        releaseCandidateVersion = ver;
                    }
                }
                catch (Exception exception) {
                    // empty catch block
                }
            }
        }
        this.minecraftVersion = version;
        this.minecraftPatchVersion = patchVersion;
        this.minecraftPreReleaseVersion = preReleaseVersion;
        this.minecraftReleaseCandidateVersion = releaseCandidateVersion;
        if (this.isVersion(13, 1)) {
            this.isGeneratedHandler = new ChunkIsGeneratedApiExists();
        }
        if (!this.isVersion(12)) {
            this.blockStateSnapshotHandler = new BlockStateSnapshotBeforeSnapshots();
            this.inventoryHolderSnapshotHandler = new InventoryHolderSnapshotBeforeSnapshots();
        } else {
            this.blockStateSnapshotHandler = new BlockStateSnapshotNoOption();
            this.inventoryHolderSnapshotHandler = new InventoryHolderSnapshotNoOption();
        }
    }

    public abstract String getName();

    public CompletableFuture<Chunk> getChunkAtAsync(World world, int x, int z, boolean gen) {
        return this.asyncChunksHandler.getChunkAtAsync(world, x, z, gen, false);
    }

    public CompletableFuture<Chunk> getChunkAtAsync(World world, int x, int z, boolean gen, boolean isUrgent) {
        return this.asyncChunksHandler.getChunkAtAsync(world, x, z, gen, isUrgent);
    }

    public CompletableFuture<Chunk> getChunkAtAsyncUrgently(World world, int x, int z, boolean gen) {
        return this.asyncChunksHandler.getChunkAtAsync(world, x, z, gen, true);
    }

    public CompletableFuture<Boolean> teleport(Entity entity, Location location, PlayerTeleportEvent.TeleportCause cause) {
        return this.asyncTeleportHandler.teleportAsync(entity, location, cause);
    }

    public boolean isChunkGenerated(World world, int x, int z) {
        return this.isGeneratedHandler.isChunkGenerated(world, x, z);
    }

    public BlockStateSnapshotResult getBlockState(Block block, boolean useSnapshot) {
        return this.blockStateSnapshotHandler.getBlockState(block, useSnapshot);
    }

    public InventoryHolderSnapshotResult getHolder(Inventory inventory, boolean useSnapshot) {
        return this.inventoryHolderSnapshotHandler.getHolder(inventory, useSnapshot);
    }

    public CompletableFuture<Location> getBedSpawnLocationAsync(Player player, boolean isUrgent) {
        return this.bedSpawnLocationHandler.getBedSpawnLocationAsync(player, isUrgent);
    }

    public boolean isVersion(int minor) {
        return this.isVersion(minor, 0);
    }

    public boolean isVersion(int minor, int patch) {
        return this.minecraftVersion > minor || this.minecraftVersion >= minor && this.minecraftPatchVersion >= patch;
    }

    public int getMinecraftVersion() {
        return this.minecraftVersion;
    }

    public int getMinecraftPatchVersion() {
        return this.minecraftPatchVersion;
    }

    public int getMinecraftPreReleaseVersion() {
        return this.minecraftPreReleaseVersion;
    }

    public int getMinecraftReleaseCandidateVersion() {
        return this.minecraftReleaseCandidateVersion;
    }

    public boolean isSpigot() {
        return false;
    }

    public boolean isPaper() {
        return false;
    }
}

