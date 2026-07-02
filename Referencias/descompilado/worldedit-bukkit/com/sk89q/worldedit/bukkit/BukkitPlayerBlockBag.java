/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Player
 *  org.bukkit.inventory.ItemStack
 */
package com.sk89q.worldedit.bukkit;

import com.sk89q.worldedit.blocks.BaseItemStack;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.inventory.BlockBag;
import com.sk89q.worldedit.extent.inventory.BlockBagException;
import com.sk89q.worldedit.extent.inventory.OutOfBlocksException;
import com.sk89q.worldedit.extent.inventory.OutOfSpaceException;
import com.sk89q.worldedit.util.Location;
import com.sk89q.worldedit.world.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class BukkitPlayerBlockBag
extends BlockBag {
    private final Player player;
    private ItemStack[] items;

    public BukkitPlayerBlockBag(Player player) {
        this.player = player;
    }

    private void loadInventory() {
        if (this.items == null) {
            this.items = this.player.getInventory().getContents();
        }
    }

    public Player getPlayer() {
        return this.player;
    }

    @Override
    public void fetchBlock(BlockState blockState) throws BlockBagException {
        if (blockState.getBlockType().getMaterial().isAir()) {
            throw new IllegalArgumentException("Can't fetch air block");
        }
        this.loadInventory();
        boolean found = false;
        for (int slot = 0; slot < this.items.length; ++slot) {
            ItemStack bukkitItem = this.items[slot];
            if (bukkitItem == null || !BukkitAdapter.equals(blockState.getBlockType(), bukkitItem.getType())) continue;
            int currentAmount = bukkitItem.getAmount();
            if (currentAmount < 0) {
                return;
            }
            if (currentAmount > 1) {
                bukkitItem.setAmount(currentAmount - 1);
                found = true;
                break;
            }
            this.items[slot] = null;
            found = true;
            break;
        }
        if (!found) {
            throw new OutOfBlocksException();
        }
    }

    @Override
    public void storeBlock(BlockState blockState, int amount) throws BlockBagException {
        if (blockState.getBlockType().getMaterial().isAir()) {
            throw new IllegalArgumentException("Can't store air block");
        }
        if (!blockState.getBlockType().hasItemType()) {
            throw new IllegalArgumentException("This block cannot be stored");
        }
        this.loadInventory();
        int freeSlot = -1;
        for (int slot = 0; slot < this.items.length; ++slot) {
            ItemStack bukkitItem = this.items[slot];
            if (bukkitItem == null) {
                if (freeSlot != -1) continue;
                freeSlot = slot;
                continue;
            }
            if (!BukkitAdapter.equals(blockState.getBlockType(), bukkitItem.getType())) continue;
            int currentAmount = bukkitItem.getAmount();
            if (currentAmount < 0) {
                return;
            }
            if (currentAmount >= 64) continue;
            int spaceLeft = 64 - currentAmount;
            if (spaceLeft >= amount) {
                bukkitItem.setAmount(currentAmount + amount);
                return;
            }
            bukkitItem.setAmount(64);
            amount -= spaceLeft;
        }
        if (freeSlot > -1) {
            this.items[freeSlot] = BukkitAdapter.adapt(new BaseItemStack(blockState.getBlockType().getItemType(), amount));
            return;
        }
        throw new OutOfSpaceException(blockState.getBlockType());
    }

    @Override
    public void flushChanges() {
        if (this.items != null) {
            this.player.getInventory().setContents(this.items);
            this.items = null;
        }
    }

    @Override
    public void addSourcePosition(Location pos) {
    }

    @Override
    public void addSingleSourcePosition(Location pos) {
    }
}

