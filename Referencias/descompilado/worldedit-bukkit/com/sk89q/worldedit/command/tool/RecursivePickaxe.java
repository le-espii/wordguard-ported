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
import java.util.HashSet;
import java.util.Set;
import javax.annotation.Nullable;

public class RecursivePickaxe
implements BlockTool {
    private final double range;

    public RecursivePickaxe(double range) {
        this.range = range;
    }

    @Override
    public boolean canUse(Actor player) {
        return player.hasPermission("worldedit.superpickaxe.recursive");
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean actPrimary(Platform server, LocalConfiguration config, Player player, LocalSession session, Location clicked, @Nullable Direction face) {
        BlockVector3 origin;
        World world = BlockTool.requireWorld(clicked);
        BlockType initialType = world.getBlock(origin = clicked.toVector().toBlockPoint()).getBlockType();
        if (initialType.getMaterial().isAir()) {
            return false;
        }
        if (initialType == BlockTypes.BEDROCK && !player.canDestroyBedrock()) {
            return false;
        }
        try (EditSession editSession = BlockTool.createEditSession(player, session, clicked);){
            editSession.getSurvivalExtent().setToolUse(config.superPickaxeManyDrop);
            try {
                RecursivePickaxe.recurse(server, editSession, world, clicked.toVector().toBlockPoint(), clicked.toVector().toBlockPoint(), this.range, initialType, new HashSet<BlockVector3>());
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

    private static void recurse(Platform server, EditSession editSession, World world, BlockVector3 pos, BlockVector3 origin, double size, BlockType initialType, Set<BlockVector3> visited) throws MaxChangedBlocksException {
        double distanceSq = origin.distanceSq(pos);
        if (distanceSq > size * size || visited.contains((Object)pos)) {
            return;
        }
        visited.add(pos);
        if (editSession.getBlock(pos).getBlockType() != initialType) {
            return;
        }
        editSession.setBlock(pos, BlockTypes.AIR.getDefaultState());
        world.queueBlockBreakEffect(server, pos, initialType, distanceSq);
        RecursivePickaxe.recurse(server, editSession, world, pos.add(1, 0, 0), origin, size, initialType, visited);
        RecursivePickaxe.recurse(server, editSession, world, pos.add(-1, 0, 0), origin, size, initialType, visited);
        RecursivePickaxe.recurse(server, editSession, world, pos.add(0, 0, 1), origin, size, initialType, visited);
        RecursivePickaxe.recurse(server, editSession, world, pos.add(0, 0, -1), origin, size, initialType, visited);
        RecursivePickaxe.recurse(server, editSession, world, pos.add(0, 1, 0), origin, size, initialType, visited);
        RecursivePickaxe.recurse(server, editSession, world, pos.add(0, -1, 0), origin, size, initialType, visited);
    }
}

