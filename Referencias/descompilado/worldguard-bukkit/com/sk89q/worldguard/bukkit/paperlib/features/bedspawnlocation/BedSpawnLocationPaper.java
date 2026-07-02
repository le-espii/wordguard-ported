/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Location
 *  org.bukkit.entity.Player
 */
package com.sk89q.worldguard.bukkit.paperlib.features.bedspawnlocation;

import com.sk89q.worldguard.bukkit.paperlib.PaperLib;
import com.sk89q.worldguard.bukkit.paperlib.features.bedspawnlocation.BedSpawnLocation;
import java.util.concurrent.CompletableFuture;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class BedSpawnLocationPaper
implements BedSpawnLocation {
    @Override
    public CompletableFuture<Location> getBedSpawnLocationAsync(Player player, boolean isUrgent) {
        Location bedLocation = player.getPotentialBedLocation();
        if (bedLocation == null || bedLocation.getWorld() == null) {
            return CompletableFuture.completedFuture(null);
        }
        return PaperLib.getChunkAtAsync(bedLocation.getWorld(), bedLocation.getBlockX() >> 4, bedLocation.getBlockZ() >> 4, false, isUrgent).thenCompose(chunk -> CompletableFuture.completedFuture(player.getBedSpawnLocation()));
    }
}

