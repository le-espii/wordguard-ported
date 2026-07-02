/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.BlockChangeDelegate
 *  org.bukkit.block.data.BlockData
 */
package com.sk89q.worldedit.bukkit;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import org.bukkit.BlockChangeDelegate;
import org.bukkit.block.data.BlockData;

public class EditSessionBlockChangeDelegate
implements BlockChangeDelegate {
    private final EditSession editSession;

    public EditSessionBlockChangeDelegate(EditSession editSession) {
        this.editSession = editSession;
    }

    public boolean setBlockData(int x, int y, int z, BlockData blockData) {
        try {
            this.editSession.setBlock(BlockVector3.at(x, y, z), BukkitAdapter.adapt(blockData));
        }
        catch (MaxChangedBlocksException e) {
            return false;
        }
        return true;
    }

    public BlockData getBlockData(int x, int y, int z) {
        return BukkitAdapter.adapt(this.editSession.getBlock(BlockVector3.at(x, y, z)));
    }

    public int getHeight() {
        return this.editSession.getWorld().getMaxY() + 1;
    }

    public boolean isEmpty(int x, int y, int z) {
        return this.editSession.getBlock(BlockVector3.at(x, y, z)).getBlockType().getMaterial().isAir();
    }
}

