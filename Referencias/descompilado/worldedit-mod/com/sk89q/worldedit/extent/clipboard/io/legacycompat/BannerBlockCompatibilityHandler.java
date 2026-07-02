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
import java.util.Iterator;
import org.enginehub.linbus.tree.LinCompoundTag;
import org.enginehub.linbus.tree.LinIntTag;
import org.enginehub.linbus.tree.LinListTag;
import org.enginehub.linbus.tree.LinTagType;

public class BannerBlockCompatibilityHandler
implements NBTCompatibilityHandler {
    private static final Property<Direction> FacingProperty;
    private static final Property<Integer> RotationProperty;

    @Override
    public BaseBlock updateNbt(BaseBlock block) {
        BlockType blockType = block.getBlockType();
        if (blockType != BlockTypes.WHITE_BANNER && blockType != BlockTypes.WHITE_WALL_BANNER) {
            return block;
        }
        LinCompoundTag nbt = block.getNbt();
        if (nbt == null) {
            return block;
        }
        LinIntTag typeTag = nbt.findTag("Base", LinTagType.intTag());
        if (typeTag != null) {
            BlockType type;
            boolean isWall = block.getBlockType() == BlockTypes.WHITE_WALL_BANNER;
            String bannerType = BannerBlockCompatibilityHandler.convertBannerType(typeTag.valueAsInt(), isWall);
            if (bannerType != null && (type = BlockTypes.get("minecraft:" + bannerType)) != null) {
                BlockStateHolder<BlockState> state = type.getDefaultState();
                if (isWall) {
                    Property facingProp = type.getProperty("facing");
                    state = ((BlockState)state).with(facingProp, (Object)block.getState(FacingProperty));
                } else {
                    Property rotationProp = type.getProperty("rotation");
                    state = ((BlockState)state).with(rotationProp, block.getState(RotationProperty));
                }
                LinCompoundTag.Builder nbtBuilder = nbt.toBuilder();
                nbtBuilder.remove("Base");
                LinListTag<LinCompoundTag> patternsTag = nbt.findListTag("Patterns", LinTagType.compoundTag());
                if (patternsTag != null) {
                    LinListTag.Builder<LinCompoundTag> newPatterns = LinListTag.builder(LinTagType.compoundTag());
                    Iterator iterator = patternsTag.value().iterator();
                    while (iterator.hasNext()) {
                        LinCompoundTag pattern = (LinCompoundTag)iterator.next();
                        LinIntTag color = pattern.findTag("Color", LinTagType.intTag());
                        if (color != null) {
                            newPatterns.add(pattern.toBuilder().putInt("Color", 15 - color.valueAsInt()).build());
                            continue;
                        }
                        newPatterns.add(pattern);
                    }
                    nbtBuilder.put("Patterns", newPatterns.build());
                }
                return state.toBaseBlock(nbtBuilder.build());
            }
        }
        return block;
    }

    private static String convertBannerType(int oldType, boolean isWall) {
        String color;
        switch (oldType) {
            case 0: {
                String string = "black";
                break;
            }
            case 1: {
                String string = "red";
                break;
            }
            case 2: {
                String string = "green";
                break;
            }
            case 3: {
                String string = "brown";
                break;
            }
            case 4: {
                String string = "blue";
                break;
            }
            case 5: {
                String string = "purple";
                break;
            }
            case 6: {
                String string = "cyan";
                break;
            }
            case 7: {
                String string = "light_gray";
                break;
            }
            case 8: {
                String string = "gray";
                break;
            }
            case 9: {
                String string = "pink";
                break;
            }
            case 10: {
                String string = "lime";
                break;
            }
            case 11: {
                String string = "yellow";
                break;
            }
            case 12: {
                String string = "light_blue";
                break;
            }
            case 13: {
                String string = "magenta";
                break;
            }
            case 14: {
                String string = "orange";
                break;
            }
            case 15: {
                String string = "white";
                break;
            }
            default: {
                String string = color = null;
            }
        }
        if (color == null) {
            return null;
        }
        return color + (isWall ? "_wall_banner" : "_banner");
    }

    static {
        Property tempRotation;
        Property tempFacing;
        try {
            tempFacing = BlockTypes.WHITE_WALL_BANNER.getProperty("facing");
            tempRotation = BlockTypes.WHITE_BANNER.getProperty("rotation");
        }
        catch (ClassCastException | IllegalArgumentException | NullPointerException e) {
            tempFacing = null;
            tempRotation = null;
        }
        FacingProperty = tempFacing;
        RotationProperty = tempRotation;
    }
}

