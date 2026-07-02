/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.extent.inventory;

import com.sk89q.worldedit.extent.inventory.BlockBagException;
import com.sk89q.worldedit.extent.inventory.OutOfBlocksException;
import com.sk89q.worldedit.util.Location;
import com.sk89q.worldedit.world.block.BlockState;

public abstract class BlockBag {
    public void storeDroppedBlock(BlockState blockState) throws BlockBagException {
        BlockState dropped = blockState;
        if (dropped == null) {
            return;
        }
        if (dropped.getBlockType().getMaterial().isAir()) {
            return;
        }
        this.storeBlock(dropped);
    }

    public void fetchPlacedBlock(BlockState blockState) throws BlockBagException {
        try {
            if (blockState.getBlockType().getMaterial().isReplacedDuringPlacement()) {
                return;
            }
            this.fetchBlock(blockState);
        }
        catch (OutOfBlocksException e) {
            BlockState placed = blockState;
            if (placed.getBlockType().getMaterial().isAir()) {
                throw e;
            }
            this.fetchBlock(placed);
        }
    }

    public abstract void fetchBlock(BlockState var1) throws BlockBagException;

    public void storeBlock(BlockState blockState) throws BlockBagException {
        this.storeBlock(blockState, 1);
    }

    public abstract void storeBlock(BlockState var1, int var2) throws BlockBagException;

    public boolean peekBlock(BlockState blockState) {
        try {
            this.fetchBlock(blockState);
            this.storeBlock(blockState);
            return true;
        }
        catch (BlockBagException e) {
            return false;
        }
    }

    public abstract void flushChanges();

    public abstract void addSourcePosition(Location var1);

    public abstract void addSingleSourcePosition(Location var1);
}

