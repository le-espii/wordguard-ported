/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.command.tool;

import com.sk89q.worldedit.LocalConfiguration;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.command.tool.BrushTool;
import com.sk89q.worldedit.command.tool.DoubleActionTraceTool;
import com.sk89q.worldedit.entity.Player;
import com.sk89q.worldedit.extension.platform.Platform;
import com.sk89q.worldedit.extension.platform.permission.ActorSelectorLimits;
import com.sk89q.worldedit.function.mask.Mask;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.RegionSelector;
import com.sk89q.worldedit.util.Location;
import com.sk89q.worldedit.util.formatting.text.TranslatableComponent;

public class DistanceWand
extends BrushTool
implements DoubleActionTraceTool {
    public DistanceWand() {
        super("worldedit.selection.pos");
    }

    @Override
    public boolean actSecondary(Platform server, LocalConfiguration config, Player player, LocalSession session) {
        BlockVector3 blockPoint;
        Location target = this.getTarget(player);
        if (target == null) {
            return true;
        }
        RegionSelector selector = session.getRegionSelector(player.getWorld());
        if (selector.selectPrimary(blockPoint = target.toVector().toBlockPoint(), ActorSelectorLimits.forActor(player))) {
            selector.explainPrimarySelection(player, session, blockPoint);
        }
        return true;
    }

    @Override
    public boolean actPrimary(Platform server, LocalConfiguration config, Player player, LocalSession session) {
        BlockVector3 blockPoint;
        Location target = this.getTarget(player);
        if (target == null) {
            return true;
        }
        RegionSelector selector = session.getRegionSelector(player.getWorld());
        if (selector.selectSecondary(blockPoint = target.toVector().toBlockPoint(), ActorSelectorLimits.forActor(player))) {
            selector.explainSecondarySelection(player, session, blockPoint);
        }
        return true;
    }

    private Location getTarget(Player player) {
        Mask mask = this.getTraceMask();
        Location target = this.range > -1 ? player.getBlockTrace(this.getRange(), true, mask) : player.getBlockTrace(MAX_RANGE, false, mask);
        if (target == null) {
            player.printError(TranslatableComponent.of("worldedit.tool.no-block"));
            return null;
        }
        return target;
    }
}

