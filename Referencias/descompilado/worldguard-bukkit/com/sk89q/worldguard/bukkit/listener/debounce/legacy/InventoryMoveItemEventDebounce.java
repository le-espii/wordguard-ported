/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Material
 *  org.bukkit.block.Block
 *  org.bukkit.block.BlockState
 *  org.bukkit.block.Chest
 *  org.bukkit.block.DoubleChest
 *  org.bukkit.event.Cancellable
 *  org.bukkit.event.inventory.InventoryMoveItemEvent
 *  org.bukkit.inventory.InventoryHolder
 */
package com.sk89q.worldguard.bukkit.listener.debounce.legacy;

import com.sk89q.worldguard.bukkit.listener.debounce.legacy.AbstractEventDebounce;
import com.sk89q.worldguard.bukkit.paperlib.PaperLib;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.event.Cancellable;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.inventory.InventoryHolder;

public class InventoryMoveItemEventDebounce
extends AbstractEventDebounce<Key> {
    public InventoryMoveItemEventDebounce(int debounceTime) {
        super(debounceTime);
    }

    public AbstractEventDebounce.Entry tryDebounce(InventoryMoveItemEvent event) {
        return super.getEntry(new Key(event), (Cancellable)event);
    }

    protected static class Key {
        private final Object cause;
        private final Object source;
        private final Object target;

        public Key(InventoryMoveItemEvent event) {
            this.cause = this.transform(PaperLib.getHolder(event.getInitiator(), false).getHolder());
            this.source = this.transform(PaperLib.getHolder(event.getSource(), false).getHolder());
            this.target = this.transform(PaperLib.getHolder(event.getDestination(), false).getHolder());
        }

        private Object transform(InventoryHolder holder) {
            if (holder instanceof BlockState) {
                BlockState blockState = (BlockState)holder;
                return new BlockMaterialKey(blockState);
            }
            if (holder instanceof DoubleChest) {
                InventoryHolder left;
                DoubleChest doubleChest = (DoubleChest)holder;
                InventoryHolder inventoryHolder = left = PaperLib.isPaper() ? doubleChest.getLeftSide(false) : doubleChest.getLeftSide();
                if (left instanceof Chest) {
                    Chest chest = (Chest)left;
                    return new BlockMaterialKey((BlockState)chest);
                }
                return holder;
            }
            return holder;
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            Key key = (Key)o;
            if (this.cause != null ? !this.cause.equals(key.cause) : key.cause != null) {
                return false;
            }
            if (this.source != null ? !this.source.equals(key.source) : key.source != null) {
                return false;
            }
            return !(this.target != null ? !this.target.equals(key.target) : key.target != null);
        }

        public int hashCode() {
            int result = this.cause != null ? this.cause.hashCode() : 0;
            result = 31 * result + (this.source != null ? this.source.hashCode() : 0);
            result = 31 * result + (this.target != null ? this.target.hashCode() : 0);
            return result;
        }
    }

    private static class BlockMaterialKey {
        private final Block block;
        private final Material material;

        private BlockMaterialKey(BlockState block) {
            this.block = block.getBlock();
            this.material = block.getType();
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            BlockMaterialKey that = (BlockMaterialKey)o;
            if (!this.block.equals((Object)that.block)) {
                return false;
            }
            return this.material == that.material;
        }

        public int hashCode() {
            int result = this.block.hashCode();
            result = 31 * result + this.material.hashCode();
            return result;
        }
    }
}

