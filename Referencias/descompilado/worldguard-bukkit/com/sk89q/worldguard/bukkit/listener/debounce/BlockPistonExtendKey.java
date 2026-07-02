/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.block.Block
 *  org.bukkit.event.block.BlockPistonExtendEvent
 */
package com.sk89q.worldguard.bukkit.listener.debounce;

import java.util.List;
import org.bukkit.block.Block;
import org.bukkit.event.block.BlockPistonExtendEvent;

public class BlockPistonExtendKey {
    private final Block piston;
    private final List<Block> blocks;
    private final int blocksHashCode;

    public BlockPistonExtendKey(BlockPistonExtendEvent event) {
        this.piston = event.getBlock();
        this.blocks = event.getBlocks();
        this.blocksHashCode = this.blocks.hashCode();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        BlockPistonExtendKey that = (BlockPistonExtendKey)o;
        if (!this.blocks.equals(that.blocks)) {
            return false;
        }
        return this.piston.equals((Object)that.piston);
    }

    public int hashCode() {
        int result = this.piston.hashCode();
        result = 31 * result + this.blocksHashCode;
        return result;
    }
}

