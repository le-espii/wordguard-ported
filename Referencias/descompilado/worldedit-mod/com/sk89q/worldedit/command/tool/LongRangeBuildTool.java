/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.command.tool;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.LocalConfiguration;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.command.tool.BrushTool;
import com.sk89q.worldedit.command.tool.DoubleActionTraceTool;
import com.sk89q.worldedit.entity.Player;
import com.sk89q.worldedit.extension.platform.Actor;
import com.sk89q.worldedit.extension.platform.Platform;
import com.sk89q.worldedit.extent.inventory.BlockBag;
import com.sk89q.worldedit.function.mask.Mask;
import com.sk89q.worldedit.function.pattern.Pattern;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.util.Location;
import com.sk89q.worldedit.util.formatting.text.TranslatableComponent;
import com.sk89q.worldedit.world.block.BaseBlock;

public class LongRangeBuildTool
extends BrushTool
implements DoubleActionTraceTool {
    private final Pattern primary;
    private final Pattern secondary;

    public LongRangeBuildTool(Pattern secondary, Pattern primary) {
        super("worldedit.tool.lrbuild");
        this.primary = primary;
        this.secondary = secondary;
    }

    @Override
    public boolean canUse(Actor player) {
        return player.hasPermission("worldedit.tool.lrbuild");
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Override
    public boolean actSecondary(Platform server, LocalConfiguration config, Player player, LocalSession session) {
        Location pos = this.getTargetFace(player);
        if (pos == null) {
            return false;
        }
        BlockBag bag = session.getBlockBag(player);
        try (EditSession editSession = session.createEditSession(player);){
            try {
                editSession.disableBuffering();
                BlockVector3 blockPoint = pos.toVector().toBlockPoint();
                BaseBlock applied = this.secondary.applyBlock(blockPoint);
                if (applied.getBlockType().getMaterial().isAir()) {
                    editSession.setBlock(blockPoint, this.secondary);
                } else {
                    editSession.setBlock(pos.toVector().subtract(pos.getDirection()).toBlockPoint(), this.secondary);
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

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Override
    public boolean actPrimary(Platform server, LocalConfiguration config, Player player, LocalSession session) {
        Location pos = this.getTargetFace(player);
        if (pos == null) {
            return false;
        }
        BlockBag bag = session.getBlockBag(player);
        try (EditSession editSession = session.createEditSession(player);){
            try {
                editSession.disableBuffering();
                BlockVector3 blockPoint = pos.toVector().toBlockPoint();
                BaseBlock applied = this.primary.applyBlock(blockPoint);
                if (applied.getBlockType().getMaterial().isAir()) {
                    editSession.setBlock(blockPoint, this.primary);
                } else {
                    editSession.setBlock(pos.toVector().subtract(pos.getDirection()).toBlockPoint(), this.primary);
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

    private Location getTargetFace(Player player) {
        Mask mask = this.getTraceMask();
        Location target = this.range > -1 ? player.getBlockTrace(this.getRange(), true, mask) : player.getBlockTrace(MAX_RANGE, false, mask);
        if (target == null) {
            player.printError(TranslatableComponent.of("worldedit.tool.no-block"));
            return null;
        }
        return target;
    }
}

