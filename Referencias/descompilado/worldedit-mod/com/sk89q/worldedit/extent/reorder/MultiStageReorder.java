/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.extent.reorder;

import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.extent.AbstractBufferingExtent;
import com.sk89q.worldedit.extent.Extent;
import com.sk89q.worldedit.extent.reorder.ReorderingExtent;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.OperationQueue;
import com.sk89q.worldedit.function.operation.RunContext;
import com.sk89q.worldedit.function.operation.SetBlockMap;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.util.collection.BlockMap;
import com.sk89q.worldedit.world.block.BaseBlock;
import com.sk89q.worldedit.world.block.BlockCategories;
import com.sk89q.worldedit.world.block.BlockCategory;
import com.sk89q.worldedit.world.block.BlockState;
import com.sk89q.worldedit.world.block.BlockStateHolder;
import com.sk89q.worldedit.world.block.BlockType;
import com.sk89q.worldedit.world.block.BlockTypes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Deprecated
public class MultiStageReorder
extends AbstractBufferingExtent
implements ReorderingExtent {
    private static final Map<BlockType, PlacementPriority> priorityMap = new HashMap<BlockType, PlacementPriority>();
    private final Map<PlacementPriority, BlockMap<BaseBlock>> stages = new HashMap<PlacementPriority, BlockMap<BaseBlock>>();
    private boolean enabled;

    public MultiStageReorder(Extent extent) {
        this(extent, true);
    }

    public MultiStageReorder(Extent extent, boolean enabled) {
        super(extent);
        this.enabled = enabled;
        for (PlacementPriority priority : PlacementPriority.values()) {
            this.stages.put(priority, BlockMap.createForBaseBlock());
        }
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean commitRequired() {
        return this.enabled;
    }

    private <B extends BlockStateHolder<B>> PlacementPriority getPlacementPriority(B block) {
        return priorityMap.getOrDefault(block.getBlockType(), PlacementPriority.FIRST);
    }

    public <B extends BlockStateHolder<B>> boolean setBlock(BlockVector3 location, B block) throws WorldEditException {
        if (!this.enabled) {
            return this.setDelegateBlock(location, block);
        }
        BlockState existing = this.getExtent().getBlock(location);
        PlacementPriority priority = this.getPlacementPriority(block);
        PlacementPriority srcPriority = this.getPlacementPriority(existing);
        if (srcPriority != PlacementPriority.FIRST) {
            BaseBlock replacement = (block.getBlockType().getMaterial().isAir() ? block : BlockTypes.AIR.getDefaultState()).toBaseBlock();
            switch (srcPriority.ordinal()) {
                case 6: {
                    this.stages.get((Object)PlacementPriority.CLEAR_FINAL).put(location, replacement);
                    break;
                }
                case 4: {
                    this.stages.get((Object)PlacementPriority.CLEAR_LATE).put(location, replacement);
                    break;
                }
                case 5: {
                    this.stages.get((Object)PlacementPriority.CLEAR_LAST).put(location, replacement);
                    break;
                }
            }
            if (block.getBlockType().getMaterial().isAir()) {
                return !existing.equalsFuzzy(block);
            }
        }
        this.stages.get((Object)priority).put(location, block.toBaseBlock());
        return !existing.equalsFuzzy(block);
    }

    @Override
    protected BaseBlock getBufferedFullBlock(BlockVector3 position) {
        if (!this.enabled) {
            return null;
        }
        for (BlockMap<BaseBlock> blocks : this.stages.values()) {
            BaseBlock baseBlock = blocks.get((Object)position);
            if (baseBlock == null) continue;
            return baseBlock;
        }
        return null;
    }

    @Override
    public Operation commitBefore() {
        if (!this.commitRequired()) {
            return null;
        }
        ArrayList<Operation> operations = new ArrayList<Operation>();
        for (PlacementPriority priority : PlacementPriority.values()) {
            final BlockMap<BaseBlock> blocks = this.stages.get((Object)priority);
            operations.add(new SetBlockMap(this, this.getExtent(), blocks){

                @Override
                public Operation resume(RunContext run) throws WorldEditException {
                    Operation operation = super.resume(run);
                    if (operation == null) {
                        blocks.clear();
                    }
                    return operation;
                }
            });
        }
        return new OperationQueue(operations);
    }

    static {
        priorityMap.put(BlockTypes.WATER, PlacementPriority.LATE);
        priorityMap.put(BlockTypes.LAVA, PlacementPriority.LATE);
        priorityMap.put(BlockTypes.SAND, PlacementPriority.LATE);
        priorityMap.put(BlockTypes.GRAVEL, PlacementPriority.LATE);
        BlockCategories.SAPLINGS.getAll().forEach(type -> priorityMap.put((BlockType)type, PlacementPriority.LAST));
        BlockCategories.FLOWER_POTS.getAll().forEach(type -> priorityMap.put((BlockType)type, PlacementPriority.LAST));
        BlockCategories.BUTTONS.getAll().forEach(type -> priorityMap.put((BlockType)type, PlacementPriority.LAST));
        BlockCategories.ANVIL.getAll().forEach(type -> priorityMap.put((BlockType)type, PlacementPriority.LAST));
        BlockCategories.WOODEN_PRESSURE_PLATES.getAll().forEach(type -> priorityMap.put((BlockType)type, PlacementPriority.LAST));
        BlockCategory carpets = BlockCategories.CARPETS;
        carpets.getAll().forEach(type -> priorityMap.put((BlockType)type, PlacementPriority.LAST));
        BlockCategories.WOOL_CARPETS.getAll().forEach(type -> priorityMap.put((BlockType)type, PlacementPriority.LAST));
        BlockCategories.RAILS.getAll().forEach(type -> priorityMap.put((BlockType)type, PlacementPriority.LAST));
        BlockCategories.BEDS.getAll().forEach(type -> priorityMap.put((BlockType)type, PlacementPriority.LAST));
        BlockCategories.SMALL_FLOWERS.getAll().forEach(type -> priorityMap.put((BlockType)type, PlacementPriority.LAST));
        priorityMap.put(BlockTypes.BLACK_BED, PlacementPriority.LAST);
        priorityMap.put(BlockTypes.BLUE_BED, PlacementPriority.LAST);
        priorityMap.put(BlockTypes.BROWN_BED, PlacementPriority.LAST);
        priorityMap.put(BlockTypes.CYAN_BED, PlacementPriority.LAST);
        priorityMap.put(BlockTypes.GRAY_BED, PlacementPriority.LAST);
        priorityMap.put(BlockTypes.GREEN_BED, PlacementPriority.LAST);
        priorityMap.put(BlockTypes.LIGHT_BLUE_BED, PlacementPriority.LAST);
        priorityMap.put(BlockTypes.LIGHT_GRAY_BED, PlacementPriority.LAST);
        priorityMap.put(BlockTypes.LIME_BED, PlacementPriority.LAST);
        priorityMap.put(BlockTypes.MAGENTA_BED, PlacementPriority.LAST);
        priorityMap.put(BlockTypes.ORANGE_BED, PlacementPriority.LAST);
        priorityMap.put(BlockTypes.PINK_BED, PlacementPriority.LAST);
        priorityMap.put(BlockTypes.PURPLE_BED, PlacementPriority.LAST);
        priorityMap.put(BlockTypes.RED_BED, PlacementPriority.LAST);
        priorityMap.put(BlockTypes.WHITE_BED, PlacementPriority.LAST);
        priorityMap.put(BlockTypes.YELLOW_BED, PlacementPriority.LAST);
        BlockType grass = BlockTypes.GRASS;
        priorityMap.put(grass, PlacementPriority.LAST);
        priorityMap.put(BlockTypes.SHORT_GRASS, PlacementPriority.LAST);
        priorityMap.put(BlockTypes.TALL_GRASS, PlacementPriority.LAST);
        priorityMap.put(BlockTypes.ROSE_BUSH, PlacementPriority.LAST);
        priorityMap.put(BlockTypes.DANDELION, PlacementPriority.LAST);
        priorityMap.put(BlockTypes.BROWN_MUSHROOM, PlacementPriority.LAST);
        priorityMap.put(BlockTypes.RED_MUSHROOM, PlacementPriority.LAST);
        priorityMap.put(BlockTypes.FERN, PlacementPriority.LAST);
        priorityMap.put(BlockTypes.LARGE_FERN, PlacementPriority.LAST);
        priorityMap.put(BlockTypes.OXEYE_DAISY, PlacementPriority.LAST);
        priorityMap.put(BlockTypes.AZURE_BLUET, PlacementPriority.LAST);
        priorityMap.put(BlockTypes.TORCH, PlacementPriority.LAST);
        priorityMap.put(BlockTypes.WALL_TORCH, PlacementPriority.LAST);
        priorityMap.put(BlockTypes.FIRE, PlacementPriority.LAST);
        priorityMap.put(BlockTypes.REDSTONE_WIRE, PlacementPriority.LAST);
        priorityMap.put(BlockTypes.CARROTS, PlacementPriority.LAST);
        priorityMap.put(BlockTypes.POTATOES, PlacementPriority.LAST);
        priorityMap.put(BlockTypes.WHEAT, PlacementPriority.LAST);
        priorityMap.put(BlockTypes.BEETROOTS, PlacementPriority.LAST);
        priorityMap.put(BlockTypes.COCOA, PlacementPriority.LAST);
        priorityMap.put(BlockTypes.LADDER, PlacementPriority.LAST);
        priorityMap.put(BlockTypes.LEVER, PlacementPriority.LAST);
        priorityMap.put(BlockTypes.REDSTONE_TORCH, PlacementPriority.LAST);
        priorityMap.put(BlockTypes.REDSTONE_WALL_TORCH, PlacementPriority.LAST);
        priorityMap.put(BlockTypes.SNOW, PlacementPriority.LAST);
        priorityMap.put(BlockTypes.NETHER_PORTAL, PlacementPriority.LAST);
        priorityMap.put(BlockTypes.END_PORTAL, PlacementPriority.LAST);
        priorityMap.put(BlockTypes.REPEATER, PlacementPriority.LAST);
        priorityMap.put(BlockTypes.VINE, PlacementPriority.LAST);
        priorityMap.put(BlockTypes.LILY_PAD, PlacementPriority.LAST);
        priorityMap.put(BlockTypes.NETHER_WART, PlacementPriority.LAST);
        priorityMap.put(BlockTypes.PISTON, PlacementPriority.LAST);
        priorityMap.put(BlockTypes.STICKY_PISTON, PlacementPriority.LAST);
        priorityMap.put(BlockTypes.TRIPWIRE_HOOK, PlacementPriority.LAST);
        priorityMap.put(BlockTypes.TRIPWIRE, PlacementPriority.LAST);
        priorityMap.put(BlockTypes.STONE_PRESSURE_PLATE, PlacementPriority.LAST);
        priorityMap.put(BlockTypes.HEAVY_WEIGHTED_PRESSURE_PLATE, PlacementPriority.LAST);
        priorityMap.put(BlockTypes.LIGHT_WEIGHTED_PRESSURE_PLATE, PlacementPriority.LAST);
        priorityMap.put(BlockTypes.COMPARATOR, PlacementPriority.LAST);
        priorityMap.put(BlockTypes.IRON_TRAPDOOR, PlacementPriority.LAST);
        priorityMap.put(BlockTypes.ACACIA_TRAPDOOR, PlacementPriority.LAST);
        priorityMap.put(BlockTypes.BIRCH_TRAPDOOR, PlacementPriority.LAST);
        priorityMap.put(BlockTypes.DARK_OAK_TRAPDOOR, PlacementPriority.LAST);
        priorityMap.put(BlockTypes.JUNGLE_TRAPDOOR, PlacementPriority.LAST);
        priorityMap.put(BlockTypes.OAK_TRAPDOOR, PlacementPriority.LAST);
        priorityMap.put(BlockTypes.SPRUCE_TRAPDOOR, PlacementPriority.LAST);
        priorityMap.put(BlockTypes.DAYLIGHT_DETECTOR, PlacementPriority.LAST);
        priorityMap.put(BlockTypes.CAKE, PlacementPriority.LAST);
        BlockCategories.DOORS.getAll().forEach(type -> priorityMap.put((BlockType)type, PlacementPriority.FINAL));
        BlockCategories.BANNERS.getAll().forEach(type -> priorityMap.put((BlockType)type, PlacementPriority.FINAL));
        BlockCategories.SIGNS.getAll().forEach(type -> priorityMap.put((BlockType)type, PlacementPriority.FINAL));
        BlockType sign = BlockTypes.SIGN;
        priorityMap.put(sign, PlacementPriority.FINAL);
        BlockType wallSign = BlockTypes.WALL_SIGN;
        priorityMap.put(wallSign, PlacementPriority.FINAL);
        priorityMap.put(BlockTypes.CACTUS, PlacementPriority.FINAL);
        priorityMap.put(BlockTypes.SUGAR_CANE, PlacementPriority.FINAL);
        priorityMap.put(BlockTypes.PISTON_HEAD, PlacementPriority.FINAL);
        priorityMap.put(BlockTypes.MOVING_PISTON, PlacementPriority.FINAL);
    }

    public static enum PlacementPriority {
        CLEAR_FINAL,
        CLEAR_LAST,
        CLEAR_LATE,
        FIRST,
        LATE,
        LAST,
        FINAL;

    }
}

