/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Location
 *  org.bukkit.entity.Player
 */
package com.sk89q.worldedit.bukkit.paperlib.features.bedspawnlocation;

import java.util.concurrent.CompletableFuture;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public interface BedSpawnLocation {
    public CompletableFuture<Location> getBedSpawnLocationAsync(Player var1, boolean var2);
}

