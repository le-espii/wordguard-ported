/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.inventory.Inventory
 */
package com.sk89q.worldedit.bukkit.paperlib.features.inventoryholdersnapshot;

import com.sk89q.worldedit.bukkit.paperlib.features.inventoryholdersnapshot.InventoryHolderSnapshotResult;
import org.bukkit.inventory.Inventory;

public interface InventoryHolderSnapshot {
    public InventoryHolderSnapshotResult getHolder(Inventory var1, boolean var2);
}

