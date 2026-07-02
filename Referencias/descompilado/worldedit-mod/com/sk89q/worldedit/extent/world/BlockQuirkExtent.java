/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 */
package com.sk89q.worldedit.extent.world;

import com.google.common.base.Preconditions;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.extent.AbstractDelegateExtent;
import com.sk89q.worldedit.extent.Extent;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldedit.world.block.BlockStateHolder;
import com.sk89q.worldedit.world.block.BlockType;
import com.sk89q.worldedit.world.block.BlockTypes;

@Deprecated
public class BlockQuirkExtent
extends AbstractDelegateExtent {
    private final World world;

    public BlockQuirkExtent(Extent extent, World world) {
        super(extent);
        Preconditions.checkNotNull((Object)world);
        this.world = world;
    }

    public <B extends BlockStateHolder<B>> boolean setBlock(BlockVector3 position, B block) throws WorldEditException {
        BlockType existing = this.getExtent().getBlock(position).getBlockType();
        if (existing.getMaterial().hasContainer()) {
            this.world.clearContainerBlockContents(position);
        } else if (existing == BlockTypes.ICE) {
            this.world.setBlock(position, BlockTypes.AIR.getDefaultState());
        }
        return super.setBlock(position, block);
    }
}

