/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.block.Block
 */
package com.sk89q.worldedit.bukkit.paperlib.features.blockstatesnapshot;

import com.sk89q.worldedit.bukkit.paperlib.features.blockstatesnapshot.BlockStateSnapshot;
import com.sk89q.worldedit.bukkit.paperlib.features.blockstatesnapshot.BlockStateSnapshotResult;
import org.bukkit.block.Block;

public class BlockStateSnapshotOptionalSnapshots
implements BlockStateSnapshot {
    @Override
    public BlockStateSnapshotResult getBlockState(Block block, boolean useSnapshot) {
        return new BlockStateSnapshotResult(useSnapshot, block.getState(useSnapshot));
    }
}

