/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.function.block;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.extent.Extent;
import com.sk89q.worldedit.function.LayerFunction;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.registry.state.Property;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldedit.world.block.BlockState;
import com.sk89q.worldedit.world.block.BlockTypes;

public class SnowSimulator
implements LayerFunction {
    private final BlockState ice = BlockTypes.ICE.getDefaultState();
    private final BlockState snow = BlockTypes.SNOW.getDefaultState();
    private final BlockState snowBlock = BlockTypes.SNOW_BLOCK.getDefaultState();
    private final Property<Integer> snowLayersProperty = BlockTypes.SNOW.getProperty("layers");
    private final Property<Integer> waterLevelProperty = BlockTypes.WATER.getProperty("level");
    private final Extent extent;
    private final World world;
    private final boolean stack;
    private int affected;

    public SnowSimulator(Extent extent, boolean stack) {
        if (extent instanceof World) {
            this.world = (World)extent;
        } else if (extent instanceof EditSession && ((EditSession)extent).getWorld() != null) {
            this.world = ((EditSession)extent).getWorld();
        } else {
            throw new IllegalArgumentException("extent must be a world or EditSession with a world");
        }
        this.extent = extent;
        this.stack = stack;
        this.affected = 0;
    }

    public int getAffected() {
        return this.affected;
    }

    @Override
    public boolean isGround(BlockVector3 position) {
        BlockState block = this.extent.getBlock(position);
        if (block.getBlockType().getMaterial().isAir() || this.stack && block.getBlockType() == BlockTypes.SNOW) {
            return false;
        }
        if (block.getBlockType() == BlockTypes.WATER) {
            return true;
        }
        return block.getBlockType().getMaterial().isMovementBlocker();
    }

    @Override
    public boolean apply(BlockVector3 position, int depth) throws WorldEditException {
        if (depth > 0) {
            return false;
        }
        BlockState block = this.extent.getBlock(position);
        if (block.getBlockType() == BlockTypes.WATER) {
            if (block.getState(this.waterLevelProperty) == 0 && this.extent.setBlock(position, this.ice)) {
                ++this.affected;
            }
            return false;
        }
        if (position.y() == this.extent.getMaximumPoint().y()) {
            return false;
        }
        BlockVector3 abovePosition = position.add(0, 1, 0);
        BlockState above = this.extent.getBlock(abovePosition);
        if (!(above.getBlockType().getMaterial().isAir() || this.stack && above.getBlockType() == BlockTypes.SNOW)) {
            return false;
        }
        if (this.stack && above.getBlockType() == BlockTypes.SNOW) {
            int currentHeight = above.getState(this.snowLayersProperty);
            if (!this.snowLayersProperty.getValues().contains(currentHeight + 2)) {
                if (this.extent.setBlock(abovePosition, this.snowBlock)) {
                    ++this.affected;
                }
            } else if (this.extent.setBlock(abovePosition, above.with(this.snowLayersProperty, (Object)(currentHeight + 1)))) {
                ++this.affected;
            }
            return false;
        }
        if (!this.world.canPlaceAt(abovePosition, this.snow)) {
            return false;
        }
        if (this.extent.setBlock(abovePosition, this.snow)) {
            ++this.affected;
        }
        return false;
    }
}

