/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.sk89q.worldedit.command.tool;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.LocalConfiguration;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.command.tool.BlockTool;
import com.sk89q.worldedit.entity.Player;
import com.sk89q.worldedit.extension.platform.Actor;
import com.sk89q.worldedit.extension.platform.Platform;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.util.Direction;
import com.sk89q.worldedit.util.Location;
import com.sk89q.worldedit.util.formatting.text.TranslatableComponent;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldedit.world.block.BlockType;
import com.sk89q.worldedit.world.block.BlockTypes;
import javax.annotation.Nullable;

public class AreaPickaxe
implements BlockTool {
    private final int range;

    public AreaPickaxe(int range) {
        this.range = range;
    }

    @Override
    public boolean canUse(Actor player) {
        return player.hasPermission("worldedit.superpickaxe.area");
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean actPrimary(Platform server, LocalConfiguration config, Player player, LocalSession session, Location clicked, @Nullable Direction face) {
        World world = BlockTool.requireWorld(clicked);
        int ox = clicked.getBlockX();
        int oy = clicked.getBlockY();
        int oz = clicked.getBlockZ();
        BlockType initialType = world.getBlock(clicked.toVector().toBlockPoint()).getBlockType();
        if (initialType.getMaterial().isAir()) {
            return false;
        }
        if (initialType == BlockTypes.BEDROCK && !player.canDestroyBedrock()) {
            return false;
        }
        try (EditSession editSession = BlockTool.createEditSession(player, session, clicked);){
            editSession.getSurvivalExtent().setToolUse(config.superPickaxeManyDrop);
            try {
                for (int x = ox - this.range; x <= ox + this.range; ++x) {
                    for (int y = oy - this.range; y <= oy + this.range; ++y) {
                        for (int z = oz - this.range; z <= oz + this.range; ++z) {
                            BlockVector3 pos = BlockVector3.at(x, y, z);
                            if (editSession.getBlock(pos).getBlockType() != initialType) continue;
                            editSession.setBlock(pos, BlockTypes.AIR.getDefaultState());
                            world.queueBlockBreakEffect(server, pos, initialType, clicked.toVector().toBlockPoint().distanceSq(pos));
                        }
                    }
                }
            }
            catch (MaxChangedBlocksException e) {
                player.printError(TranslatableComponent.of("worldedit.tool.max-block-changes"));
            }
            finally {
                session.remember(editSession);
            }
        }
        return true;
    }
}

