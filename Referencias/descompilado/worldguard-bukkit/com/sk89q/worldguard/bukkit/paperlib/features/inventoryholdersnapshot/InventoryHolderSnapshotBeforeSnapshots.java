/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.inventory.Inventory
 */
package com.sk89q.worldguard.bukkit.paperlib.features.inventoryholdersnapshot;

import com.sk89q.worldguard.bukkit.paperlib.features.inventoryholdersnapshot.InventoryHolderSnapshot;
import com.sk89q.worldguard.bukkit.paperlib.features.inventoryholdersnapshot.InventoryHolderSnapshotResult;
import org.bukkit.inventory.Inventory;

public class InventoryHolderSnapshotBeforeSnapshots
implements InventoryHolderSnapshot {
    @Override
    public InventoryHolderSnapshotResult getHolder(Inventory inventory, boolean useSnapshot) {
        return new InventoryHolderSnapshotResult(false, inventory.getHolder());
    }
}

