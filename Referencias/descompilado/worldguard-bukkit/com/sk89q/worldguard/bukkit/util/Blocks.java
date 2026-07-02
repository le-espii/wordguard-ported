/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.block.Block
 *  org.bukkit.block.BlockState
 *  org.bukkit.block.data.BlockData
 *  org.bukkit.block.data.type.Bed
 *  org.bukkit.block.data.type.Bed$Part
 *  org.bukkit.block.data.type.Chest
 *  org.bukkit.block.data.type.Chest$Type
 *  org.bukkit.util.Vector
 */
package com.sk89q.worldguard.bukkit.util;

import com.sk89q.worldguard.bukkit.paperlib.PaperLib;
import java.util.Collections;
import java.util.List;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Bed;
import org.bukkit.block.data.type.Chest;
import org.bukkit.util.Vector;

public final class Blocks {
    private Blocks() {
    }

    public static List<Block> getConnected(Block block) {
        BlockState state = PaperLib.getBlockState(block, false).getState();
        BlockData data = state.getBlockData();
        if (data instanceof Bed) {
            Bed bed = (Bed)data;
            return Collections.singletonList(block.getRelative(bed.getPart() == Bed.Part.FOOT ? bed.getFacing() : bed.getFacing().getOppositeFace()));
        }
        if (data instanceof Chest) {
            Chest chest = (Chest)data;
            Chest.Type type = chest.getType();
            if (type == Chest.Type.SINGLE) {
                return Collections.emptyList();
            }
            Vector offset = chest.getFacing().getDirection().rotateAroundY(1.5707963267948966 * (double)(type == Chest.Type.LEFT ? -1 : 1));
            return Collections.singletonList(block.getRelative((int)Math.round(offset.getX()), 0, (int)Math.round(offset.getZ())));
        }
        return Collections.emptyList();
    }
}

