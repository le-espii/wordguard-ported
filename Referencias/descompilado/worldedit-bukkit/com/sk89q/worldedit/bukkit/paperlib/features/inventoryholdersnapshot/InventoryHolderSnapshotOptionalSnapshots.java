/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.inventory.Inventory
 */
package com.sk89q.worldedit.bukkit.paperlib.features.inventoryholdersnapshot;

import com.sk89q.worldedit.bukkit.paperlib.features.inventoryholdersnapshot.InventoryHolderSnapshot;
import com.sk89q.worldedit.bukkit.paperlib.features.inventoryholdersnapshot.InventoryHolderSnapshotResult;
import org.bukkit.inventory.Inventory;

public class InventoryHolderSnapshotOptionalSnapshots
implements InventoryHolderSnapshot {
    @Override
    public InventoryHolderSnapshotResult getHolder(Inventory inventory, boolean useSnapshot) {
        return new InventoryHolderSnapshotResult(useSnapshot, inventory.getHolder(useSnapshot));
    }
}

