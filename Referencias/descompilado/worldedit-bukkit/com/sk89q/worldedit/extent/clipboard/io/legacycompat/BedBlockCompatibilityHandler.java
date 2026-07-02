/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.extent.clipboard.io.legacycompat;

import com.sk89q.worldedit.extent.clipboard.io.legacycompat.NBTCompatibilityHandler;
import com.sk89q.worldedit.registry.state.Property;
import com.sk89q.worldedit.util.Direction;
import com.sk89q.worldedit.util.concurrency.LazyReference;
import com.sk89q.worldedit.world.block.BaseBlock;
import com.sk89q.worldedit.world.block.BlockState;
import com.sk89q.worldedit.world.block.BlockStateHolder;
import com.sk89q.worldedit.world.block.BlockType;
import com.sk89q.worldedit.world.block.BlockTypes;
import org.enginehub.linbus.tree.LinCompoundTag;
import org.enginehub.linbus.tree.LinIntTag;
import org.enginehub.linbus.tree.LinTagType;

public class BedBlockCompatibilityHandler
implements NBTCompatibilityHandler {
    private static final Property<Direction> FACING_PROPERTY;
    private static final Property<String> PART_PROPERTY;

    @Override
    public BaseBlock updateNbt(BaseBlock block) {
        if (block.getBlockType() != BlockTypes.RED_BED) {
            return block;
        }
        LinCompoundTag tag = block.getNbt();
        if (tag == null) {
            return block;
        }
        LinIntTag typeTag = tag.findTag("color", LinTagType.intTag());
        if (typeTag == null) {
            return block;
        }
        String bedType = this.convertBedType(typeTag.valueAsInt());
        if (bedType == null) {
            return block;
        }
        BlockType type = BlockTypes.get("minecraft:" + bedType);
        if (type == null) {
            return block;
        }
        BlockStateHolder<BlockState> state = type.getDefaultState();
        Property facingProp = type.getProperty("facing");
        state = ((BlockState)state).with(facingProp, (Object)block.getState(FACING_PROPERTY));
        Property occupiedProp = type.getProperty("occupied");
        state = ((BlockState)state).with(occupiedProp, (Object)false);
        Property partProp = type.getProperty("part");
        state = ((BlockState)state).with(partProp, block.getState(PART_PROPERTY));
        LinCompoundTag.Builder newTag = tag.toBuilder();
        newTag.remove("color");
        return ((BlockState)state).toBaseBlock(LazyReference.computed(newTag.build()));
    }

    private String convertBedType(int oldType) {
        String color = switch (oldType) {
            case 0 -> "white";
            case 1 -> "orange";
            case 2 -> "magenta";
            case 3 -> "light_blue";
            case 4 -> "yellow";
            case 5 -> "lime";
            case 6 -> "pink";
            case 7 -> "gray";
            case 8 -> "light_gray";
            case 9 -> "cyan";
            case 10 -> "purple";
            case 11 -> "blue";
            case 12 -> "brown";
            case 13 -> "green";
            case 14 -> "red";
            case 15 -> "black";
            default -> null;
        };
        return color == null ? null : color + "_bed";
    }

    static {
        Property tempPart;
        Property tempFacing;
        try {
            tempFacing = BlockTypes.RED_BED.getProperty("facing");
            tempPart = BlockTypes.RED_BED.getProperty("part");
        }
        catch (ClassCastException | IllegalArgumentException | NullPointerException e) {
            tempFacing = null;
            tempPart = null;
        }
        FACING_PROPERTY = tempFacing;
        PART_PROPERTY = tempPart;
    }
}

