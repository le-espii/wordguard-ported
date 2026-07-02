/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 */
package com.sk89q.worldedit.extent.validation;

import com.google.common.base.Preconditions;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.extent.AbstractDelegateExtent;
import com.sk89q.worldedit.extent.Extent;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldedit.world.biome.BiomeType;
import com.sk89q.worldedit.world.block.BlockStateHolder;
import com.sk89q.worldedit.world.block.BlockType;

public class DataValidatorExtent
extends AbstractDelegateExtent {
    private final int minY;
    private final int maxY;

    public DataValidatorExtent(Extent extent, World world) {
        this(extent, ((World)Preconditions.checkNotNull((Object)world)).getMinY(), world.getMaxY());
    }

    public DataValidatorExtent(Extent extent, int minY, int maxY) {
        super(extent);
        this.minY = minY;
        this.maxY = maxY;
    }

    public <B extends BlockStateHolder<B>> boolean setBlock(BlockVector3 location, B block) throws WorldEditException {
        int y = location.y();
        BlockType type = block.getBlockType();
        if (y < this.minY || y > this.maxY) {
            return false;
        }
        if (type == null) {
            return false;
        }
        return super.setBlock(location, block);
    }

    @Override
    public boolean setBiome(BlockVector3 location, BiomeType biome) {
        int y = location.y();
        if (y < this.minY || y > this.maxY) {
            return false;
        }
        return super.setBiome(location, biome);
    }
}

