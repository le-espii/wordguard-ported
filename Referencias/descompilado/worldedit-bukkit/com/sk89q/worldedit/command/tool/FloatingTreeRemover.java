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
import com.sk89q.worldedit.world.block.BlockCategories;
import com.sk89q.worldedit.world.block.BlockState;
import com.sk89q.worldedit.world.block.BlockType;
import com.sk89q.worldedit.world.block.BlockTypes;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import javax.annotation.Nullable;

public class FloatingTreeRemover
implements BlockTool {
    private final int rangeSq;
    private final BlockVector3[] recurseDirections = new BlockVector3[]{Direction.NORTH.toBlockVector(), Direction.EAST.toBlockVector(), Direction.SOUTH.toBlockVector(), Direction.WEST.toBlockVector(), Direction.UP.toBlockVector(), Direction.DOWN.toBlockVector()};

    public FloatingTreeRemover() {
        this.rangeSq = 10000;
    }

    @Override
    public boolean canUse(Actor player) {
        return player.hasPermission("worldedit.tool.deltree");
    }

    private boolean isTreeBlock(BlockType type) {
        return BlockCategories.LEAVES.contains(type) || BlockCategories.LOGS.contains(type) || type == BlockTypes.RED_MUSHROOM_BLOCK || type == BlockTypes.BROWN_MUSHROOM_BLOCK || type == BlockTypes.MUSHROOM_STEM || type == BlockTypes.VINE;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean actPrimary(Platform server, LocalConfiguration config, Player player, LocalSession session, Location clicked, @Nullable Direction face) {
        World world = BlockTool.requireWorld(clicked);
        BlockState state = world.getBlock(clicked.toVector().toBlockPoint());
        if (!this.isTreeBlock(state.getBlockType())) {
            player.printError(TranslatableComponent.of("worldedit.tool.deltree.not-tree"));
            return true;
        }
        try (EditSession editSession = BlockTool.createEditSession(player, session, clicked);){
            try {
                Set<BlockVector3> blockSet = this.bfs(world, clicked.toVector().toBlockPoint());
                if (blockSet == null) {
                    player.printError(TranslatableComponent.of("worldedit.tool.deltree.not-floating"));
                    boolean bl = true;
                    return bl;
                }
                for (BlockVector3 blockVector : blockSet) {
                    BlockState otherState = editSession.getBlock(blockVector);
                    if (!this.isTreeBlock(otherState.getBlockType())) continue;
                    editSession.setBlock(blockVector, BlockTypes.AIR.getDefaultState());
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

    private Set<BlockVector3> bfs(World world, BlockVector3 origin) {
        HashSet<BlockVector3> visited = new HashSet<BlockVector3>();
        LinkedList<BlockVector3> queue = new LinkedList<BlockVector3>();
        queue.addLast(origin);
        visited.add(origin);
        while (!queue.isEmpty()) {
            BlockVector3 current = (BlockVector3)((Object)queue.removeFirst());
            for (BlockVector3 recurseDirection : this.recurseDirections) {
                BlockState state;
                BlockVector3 next = current.add(recurseDirection);
                if (origin.distanceSq(next) > this.rangeSq || !visited.add(next) || (state = world.getBlock(next)).getBlockType().getMaterial().isAir() || state.getBlockType() == BlockTypes.SNOW) continue;
                if (this.isTreeBlock(state.getBlockType())) {
                    queue.addLast(next);
                    continue;
                }
                BlockType currentType = world.getBlock(current).getBlockType();
                if (BlockCategories.LEAVES.contains(currentType) || currentType == BlockTypes.VINE) continue;
                return null;
            }
        }
        return visited;
    }
}

