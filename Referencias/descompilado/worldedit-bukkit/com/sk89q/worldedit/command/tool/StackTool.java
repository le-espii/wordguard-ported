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
import com.sk89q.worldedit.extent.inventory.BlockBag;
import com.sk89q.worldedit.function.mask.Mask;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.util.Direction;
import com.sk89q.worldedit.util.Location;
import com.sk89q.worldedit.world.block.BaseBlock;
import javax.annotation.Nullable;

public class StackTool
implements BlockTool {
    private final int range;
    private final Mask mask;

    public StackTool(int range, Mask mask) {
        this.range = range;
        this.mask = mask;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Override
    public boolean actPrimary(Platform server, LocalConfiguration config, Player player, LocalSession session, Location clicked, @Nullable Direction face) {
        if (face == null) {
            return false;
        }
        BlockBag bag = session.getBlockBag(player);
        try (EditSession editSession = BlockTool.createEditSession(player, session, clicked);){
            BaseBlock block = editSession.getFullBlock(clicked.toVector().toBlockPoint());
            try {
                editSession.disableBuffering();
                BlockVector3 position = clicked.toVector().toBlockPoint();
                for (int i = 0; i < this.range && this.mask.test(position = position.add(face.toBlockVector())); ++i) {
                    editSession.setBlock(position, block);
                }
                session.remember(editSession);
                return true;
            }
            catch (MaxChangedBlocksException maxChangedBlocksException) {
                return true;
            }
            finally {
                session.remember(editSession);
            }
        }
        finally {
            if (bag != null) {
                bag.flushChanges();
            }
        }
    }

    @Override
    public boolean canUse(Actor actor) {
        return actor.hasPermission("worldedit.tool.stack");
    }
}

