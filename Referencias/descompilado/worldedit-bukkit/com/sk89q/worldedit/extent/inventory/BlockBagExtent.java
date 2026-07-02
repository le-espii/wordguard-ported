/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.sk89q.worldedit.extent.inventory;

import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.extent.AbstractDelegateExtent;
import com.sk89q.worldedit.extent.Extent;
import com.sk89q.worldedit.extent.inventory.BlockBag;
import com.sk89q.worldedit.extent.inventory.BlockBagException;
import com.sk89q.worldedit.extent.inventory.UnplaceableBlockException;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.block.BlockState;
import com.sk89q.worldedit.world.block.BlockStateHolder;
import com.sk89q.worldedit.world.block.BlockType;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nullable;

public class BlockBagExtent
extends AbstractDelegateExtent {
    private Map<BlockType, Integer> missingBlocks = new HashMap<BlockType, Integer>();
    private BlockBag blockBag;

    public BlockBagExtent(Extent extent, @Nullable BlockBag blockBag) {
        super(extent);
        this.blockBag = blockBag;
    }

    @Nullable
    public BlockBag getBlockBag() {
        return this.blockBag;
    }

    public void setBlockBag(@Nullable BlockBag blockBag) {
        this.blockBag = blockBag;
    }

    public Map<BlockType, Integer> popMissing() {
        Map<BlockType, Integer> missingBlocks = this.missingBlocks;
        this.missingBlocks = new HashMap<BlockType, Integer>();
        return missingBlocks;
    }

    public <B extends BlockStateHolder<B>> boolean setBlock(BlockVector3 position, B block) throws WorldEditException {
        if (this.blockBag != null) {
            BlockState existing = this.getExtent().getBlock(position);
            if (!block.getBlockType().equals(existing.getBlockType())) {
                if (!block.getBlockType().getMaterial().isAir()) {
                    try {
                        this.blockBag.fetchPlacedBlock(block.toImmutableState());
                    }
                    catch (UnplaceableBlockException e) {
                        return false;
                    }
                    catch (BlockBagException e) {
                        if (!this.missingBlocks.containsKey(block.getBlockType())) {
                            this.missingBlocks.put(block.getBlockType(), 1);
                        } else {
                            this.missingBlocks.put(block.getBlockType(), this.missingBlocks.get(block.getBlockType()) + 1);
                        }
                        return false;
                    }
                }
                if (!existing.getBlockType().getMaterial().isAir()) {
                    try {
                        this.blockBag.storeDroppedBlock(existing);
                    }
                    catch (BlockBagException blockBagException) {
                        // empty catch block
                    }
                }
            }
        }
        return super.setBlock(position, block);
    }
}

