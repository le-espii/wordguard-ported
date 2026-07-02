/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Location
 *  org.bukkit.entity.Player
 */
package com.sk89q.worldguard.bukkit.paperlib.features.bedspawnlocation;

import com.sk89q.worldguard.bukkit.paperlib.features.bedspawnlocation.BedSpawnLocation;
import java.util.concurrent.CompletableFuture;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class BedSpawnLocationSync
implements BedSpawnLocation {
    @Override
    public CompletableFuture<Location> getBedSpawnLocationAsync(Player player, boolean isUrgent) {
        return CompletableFuture.completedFuture(player.getBedSpawnLocation());
    }
}

