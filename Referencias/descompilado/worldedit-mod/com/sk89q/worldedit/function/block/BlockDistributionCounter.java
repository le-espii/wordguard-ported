/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.function.block;

import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.extent.Extent;
import com.sk89q.worldedit.function.RegionFunction;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.util.Countable;
import com.sk89q.worldedit.world.block.BlockState;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BlockDistributionCounter
implements RegionFunction {
    private final Extent extent;
    private final boolean separateStates;
    private final List<Countable<BlockState>> distribution = new ArrayList<Countable<BlockState>>();
    private final Map<BlockState, Countable<BlockState>> map = new HashMap<BlockState, Countable<BlockState>>();

    public BlockDistributionCounter(Extent extent, boolean separateStates) {
        this.extent = extent;
        this.separateStates = separateStates;
    }

    @Override
    public boolean apply(BlockVector3 position) throws WorldEditException {
        BlockState blk = this.extent.getBlock(position);
        if (!this.separateStates) {
            blk = blk.getBlockType().getDefaultState();
        }
        if (this.map.containsKey(blk)) {
            this.map.get(blk).increment();
        } else {
            Countable<BlockState> c = new Countable<BlockState>(blk, 1);
            this.map.put(blk, c);
            this.distribution.add(c);
        }
        return true;
    }

    public List<Countable<BlockState>> getDistribution() {
        Collections.sort(this.distribution);
        Collections.reverse(this.distribution);
        return this.distribution;
    }
}

