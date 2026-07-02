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
import com.sk89q.worldedit.function.pattern.Pattern;
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

public class FloodFillTool
implements BlockTool {
    private final int range;
    private final Pattern pattern;

    public FloodFillTool(int range, Pattern pattern) {
        this.range = range;
        this.pattern = pattern;
    }

    @Override
    public boolean canUse(Actor player) {
        return player.hasPermission("worldedit.tool.flood-fill");
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean actPrimary(Platform server, LocalConfiguration config, Player player, LocalSession session, Location clicked, @Nullable Direction face) {
        BlockVector3 origin;
        World world = (World)clicked.getExtent();
        BlockType initialType = world.getBlock(origin = clicked.toVector().toBlockPoint()).getBlockType();
        if (initialType.getMaterial().isAir()) {
            return true;
        }
        if (initialType == BlockTypes.BEDROCK && !player.canDestroyBedrock()) {
            return true;
        }
        try (EditSession editSession = session.createEditSession(player);){
            try {
                this.recurse(editSession, origin, origin, this.range, initialType, new HashSet<BlockVector3>());
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

    private void recurse(EditSession editSession, BlockVector3 pos, BlockVector3 origin, int size, BlockType initialType, Set<BlockVector3> visited) throws MaxChangedBlocksException {
        if (origin.distance(pos) > (double)size || visited.contains((Object)pos)) {
            return;
        }
        visited.add(pos);
        if (editSession.getBlock(pos).getBlockType() != initialType) {
            return;
        }
        editSession.setBlock(pos, this.pattern.applyBlock(pos));
        this.recurse(editSession, pos.add(1, 0, 0), origin, size, initialType, visited);
        this.recurse(editSession, pos.add(-1, 0, 0), origin, size, initialType, visited);
        this.recurse(editSession, pos.add(0, 0, 1), origin, size, initialType, visited);
        this.recurse(editSession, pos.add(0, 0, -1), origin, size, initialType, visited);
        this.recurse(editSession, pos.add(0, 1, 0), origin, size, initialType, visited);
        this.recurse(editSession, pos.add(0, -1, 0), origin, size, initialType, visited);
    }
}

