/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sk89q.worldedit.util.Location
 *  com.sk89q.worldedit.world.block.BlockType
 *  com.sk89q.worldedit.world.block.BlockTypes
 */
package com.sk89q.worldguard.chest;

import com.sk89q.worldedit.util.Location;
import com.sk89q.worldedit.world.block.BlockType;
import com.sk89q.worldedit.world.block.BlockTypes;
import com.sk89q.worldguard.LocalPlayer;

public interface ChestProtection {
    public boolean isProtected(Location var1, LocalPlayer var2);

    public boolean isProtectedPlacement(Location var1, LocalPlayer var2);

    public boolean isAdjacentChestProtected(Location var1, LocalPlayer var2);

    default public boolean isChest(BlockType blockType) {
        return blockType == BlockTypes.CHEST || blockType == BlockTypes.DISPENSER || blockType == BlockTypes.FURNACE || blockType == BlockTypes.TRAPPED_CHEST || blockType == BlockTypes.DROPPER;
    }
}

