/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  com.sk89q.worldedit.WorldEditException
 *  com.sk89q.worldedit.math.BlockVector3
 *  com.sk89q.worldedit.registry.state.Property
 *  com.sk89q.worldedit.world.World
 *  com.sk89q.worldedit.world.block.BaseBlock
 *  com.sk89q.worldedit.world.block.BlockStateHolder
 *  com.sk89q.worldedit.world.block.BlockType
 *  com.sk89q.worldedit.world.block.BlockTypes
 */
package com.sk89q.worldguard.util;

import com.google.common.collect.Maps;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.registry.state.Property;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldedit.world.block.BaseBlock;
import com.sk89q.worldedit.world.block.BlockStateHolder;
import com.sk89q.worldedit.world.block.BlockType;
import com.sk89q.worldedit.world.block.BlockTypes;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.config.WorldConfiguration;
import java.util.Map;

public final class SpongeUtil {
    private static Map<BlockType, Property<Object>> waterloggable = Maps.newHashMap();

    private SpongeUtil() {
    }

    private static boolean isReplacable(BlockType blockType) {
        return blockType == BlockTypes.WATER || blockType == BlockTypes.SEAGRASS || blockType == BlockTypes.TALL_SEAGRASS || blockType == BlockTypes.KELP_PLANT || blockType == BlockTypes.KELP;
    }

    public static void clearSpongeWater(World world, int ox, int oy, int oz) {
        WorldConfiguration wcfg = WorldGuard.getInstance().getPlatform().getGlobalStateManager().get(world);
        for (int cx = -wcfg.spongeRadius; cx <= wcfg.spongeRadius; ++cx) {
            for (int cy = -wcfg.spongeRadius; cy <= wcfg.spongeRadius; ++cy) {
                for (int cz = -wcfg.spongeRadius; cz <= wcfg.spongeRadius; ++cz) {
                    BlockVector3 vector = BlockVector3.at((int)(ox + cx), (int)(oy + cy), (int)(oz + cz));
                    BaseBlock block = world.getFullBlock(vector);
                    BlockType blockType = block.getBlockType();
                    if (SpongeUtil.isReplacable(blockType)) {
                        try {
                            world.setBlock(vector, (BlockStateHolder)BlockTypes.AIR.getDefaultState());
                        }
                        catch (WorldEditException e) {
                            e.printStackTrace();
                        }
                        continue;
                    }
                    Property waterloggedProp = waterloggable.computeIfAbsent(blockType, bt -> (Property)bt.getPropertyMap().get("waterlogged"));
                    if (waterloggedProp == null) continue;
                    try {
                        world.setBlock(vector, (BlockStateHolder)block.with(waterloggedProp, (Object)false));
                        continue;
                    }
                    catch (WorldEditException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private static void setBlockToWater(World world, int ox, int oy, int oz) throws WorldEditException {
        BlockVector3 vector = BlockVector3.at((int)ox, (int)oy, (int)oz);
        if (world.getBlock(vector).getBlockType().getMaterial().isAir()) {
            world.setBlock(vector, (BlockStateHolder)BlockTypes.WATER.getDefaultState());
        }
    }

    public static void addSpongeWater(World world, int ox, int oy, int oz) {
        BlockVector3 vector;
        int cz;
        int cy;
        WorldConfiguration wcfg = WorldGuard.getInstance().getPlatform().getGlobalStateManager().get(world);
        int cx = ox - wcfg.spongeRadius - 1;
        for (cy = oy - wcfg.spongeRadius - 1; cy <= oy + wcfg.spongeRadius + 1; ++cy) {
            for (cz = oz - wcfg.spongeRadius - 1; cz <= oz + wcfg.spongeRadius + 1; ++cz) {
                vector = BlockVector3.at((int)cx, (int)cy, (int)cz);
                if (!SpongeUtil.isReplacable(world.getBlock(vector).getBlockType())) continue;
                try {
                    SpongeUtil.setBlockToWater(world, cx + 1, cy, cz);
                    continue;
                }
                catch (WorldEditException e) {
                    e.printStackTrace();
                }
            }
        }
        cx = ox + wcfg.spongeRadius + 1;
        for (cy = oy - wcfg.spongeRadius - 1; cy <= oy + wcfg.spongeRadius + 1; ++cy) {
            for (cz = oz - wcfg.spongeRadius - 1; cz <= oz + wcfg.spongeRadius + 1; ++cz) {
                vector = BlockVector3.at((int)cx, (int)cy, (int)cz);
                if (!SpongeUtil.isReplacable(world.getBlock(vector).getBlockType())) continue;
                try {
                    SpongeUtil.setBlockToWater(world, cx - 1, cy, cz);
                    continue;
                }
                catch (WorldEditException e) {
                    e.printStackTrace();
                }
            }
        }
        cy = oy - wcfg.spongeRadius - 1;
        for (cx = ox - wcfg.spongeRadius - 1; cx <= ox + wcfg.spongeRadius + 1; ++cx) {
            for (cz = oz - wcfg.spongeRadius - 1; cz <= oz + wcfg.spongeRadius + 1; ++cz) {
                vector = BlockVector3.at((int)cx, (int)cy, (int)cz);
                if (!SpongeUtil.isReplacable(world.getBlock(vector).getBlockType())) continue;
                try {
                    SpongeUtil.setBlockToWater(world, cx, cy + 1, cz);
                    continue;
                }
                catch (WorldEditException e) {
                    e.printStackTrace();
                }
            }
        }
        cy = oy + wcfg.spongeRadius + 1;
        for (cx = ox - wcfg.spongeRadius - 1; cx <= ox + wcfg.spongeRadius + 1; ++cx) {
            for (cz = oz - wcfg.spongeRadius - 1; cz <= oz + wcfg.spongeRadius + 1; ++cz) {
                vector = BlockVector3.at((int)cx, (int)cy, (int)cz);
                if (!SpongeUtil.isReplacable(world.getBlock(vector).getBlockType())) continue;
                try {
                    SpongeUtil.setBlockToWater(world, cx, cy - 1, cz);
                    continue;
                }
                catch (WorldEditException e) {
                    e.printStackTrace();
                }
            }
        }
        cz = oz - wcfg.spongeRadius - 1;
        for (cx = ox - wcfg.spongeRadius - 1; cx <= ox + wcfg.spongeRadius + 1; ++cx) {
            for (cy = oy - wcfg.spongeRadius - 1; cy <= oy + wcfg.spongeRadius + 1; ++cy) {
                vector = BlockVector3.at((int)cx, (int)cy, (int)cz);
                if (!SpongeUtil.isReplacable(world.getBlock(vector).getBlockType())) continue;
                try {
                    SpongeUtil.setBlockToWater(world, cx, cy, cz + 1);
                    continue;
                }
                catch (WorldEditException e) {
                    e.printStackTrace();
                }
            }
        }
        cz = oz + wcfg.spongeRadius + 1;
        for (cx = ox - wcfg.spongeRadius - 1; cx <= ox + wcfg.spongeRadius + 1; ++cx) {
            for (cy = oy - wcfg.spongeRadius - 1; cy <= oy + wcfg.spongeRadius + 1; ++cy) {
                vector = BlockVector3.at((int)cx, (int)cy, (int)cz);
                if (!SpongeUtil.isReplacable(world.getBlock(vector).getBlockType())) continue;
                try {
                    SpongeUtil.setBlockToWater(world, cx, cy, cz - 1);
                    continue;
                }
                catch (WorldEditException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

