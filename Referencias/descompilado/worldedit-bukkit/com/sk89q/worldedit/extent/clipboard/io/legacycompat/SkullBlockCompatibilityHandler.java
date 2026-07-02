/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.extent.clipboard.io.legacycompat;

import com.sk89q.worldedit.extent.clipboard.io.legacycompat.NBTCompatibilityHandler;
import com.sk89q.worldedit.registry.state.Property;
import com.sk89q.worldedit.util.Direction;
import com.sk89q.worldedit.world.block.BaseBlock;
import com.sk89q.worldedit.world.block.BlockState;
import com.sk89q.worldedit.world.block.BlockStateHolder;
import com.sk89q.worldedit.world.block.BlockType;
import com.sk89q.worldedit.world.block.BlockTypes;
import org.enginehub.linbus.tree.LinByteTag;
import org.enginehub.linbus.tree.LinCompoundTag;
import org.enginehub.linbus.tree.LinTagType;

public class SkullBlockCompatibilityHandler
implements NBTCompatibilityHandler {
    private static final Property<Direction> FACING_PROPERTY;

    @Override
    public BaseBlock updateNbt(BaseBlock block) {
        boolean isWall;
        BlockType blockType = block.getBlockType();
        boolean bl = isWall = blockType == BlockTypes.SKELETON_WALL_SKULL;
        if (blockType != BlockTypes.SKELETON_SKULL && !isWall) {
            return block;
        }
        if (FACING_PROPERTY == null) {
            return block;
        }
        LinCompoundTag tag = block.getNbt();
        if (tag == null) {
            return block;
        }
        LinByteTag typeTag = tag.findTag("SkullType", LinTagType.byteTag());
        if (typeTag == null) {
            return block;
        }
        String skullType = this.convertSkullType(typeTag.valueAsByte(), isWall);
        if (skullType == null) {
            return block;
        }
        BlockType type = BlockTypes.get("minecraft:" + skullType);
        if (type == null) {
            return block;
        }
        BlockStateHolder<BlockState> state = type.getDefaultState();
        if (isWall) {
            Property newProp = type.getProperty("facing");
            state = ((BlockState)state).with(newProp, (Object)block.getState(FACING_PROPERTY));
        } else {
            LinByteTag rotTag = tag.findTag("Rot", LinTagType.byteTag());
            if (rotTag != null) {
                Property newProp = type.getProperty("rotation");
                state = ((BlockState)state).with(newProp, (Object)rotTag.valueAsByte());
            }
        }
        LinCompoundTag newTag = tag.toBuilder().remove("SkullType").remove("Rot").build();
        return state.toBaseBlock(newTag);
    }

    private String convertSkullType(byte oldType, boolean isWall) {
        record SkullData(String kind, String suffix) {
        }
        SkullData skullData;
        switch (oldType) {
            case 0: {
                SkullData skullData2 = new SkullData("skeleton", "skull");
                break;
            }
            case 1: {
                SkullData skullData2 = new SkullData("wither_skeleton", "skull");
                break;
            }
            case 2: {
                SkullData skullData2 = new SkullData("zombie", "head");
                break;
            }
            case 3: {
                SkullData skullData2 = new SkullData("player", "head");
                break;
            }
            case 4: {
                SkullData skullData2 = new SkullData("creeper", "head");
                break;
            }
            case 5: {
                SkullData skullData2 = new SkullData("dragon", "head");
                break;
            }
            default: {
                SkullData skullData2 = skullData = null;
            }
        }
        if (skullData == null) {
            return null;
        }
        return skullData.kind + (isWall ? "_wall" : "") + "_" + skullData.suffix;
    }

    static {
        Property tempFacing;
        try {
            tempFacing = BlockTypes.SKELETON_WALL_SKULL.getProperty("facing");
        }
        catch (ClassCastException | IllegalArgumentException | NullPointerException e) {
            tempFacing = null;
        }
        FACING_PROPERTY = tempFacing;
    }
}

