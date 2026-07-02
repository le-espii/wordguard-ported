/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.block.Block
 *  org.bukkit.event.block.BlockPistonRetractEvent
 */
package com.sk89q.worldguard.bukkit.listener.debounce;

import org.bukkit.block.Block;
import org.bukkit.event.block.BlockPistonRetractEvent;

public class BlockPistonRetractKey {
    private final Block piston;
    private final Block retract;

    public BlockPistonRetractKey(BlockPistonRetractEvent event) {
        this.piston = event.getBlock();
        this.retract = event.getRetractLocation().getBlock();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        BlockPistonRetractKey that = (BlockPistonRetractKey)o;
        if (!this.piston.equals((Object)that.piston)) {
            return false;
        }
        return this.retract.equals((Object)that.retract);
    }

    public int hashCode() {
        int result = this.piston.hashCode();
        result = 31 * result + this.retract.hashCode();
        return result;
    }
}

