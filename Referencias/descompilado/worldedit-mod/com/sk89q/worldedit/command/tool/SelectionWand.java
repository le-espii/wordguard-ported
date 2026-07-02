/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.sk89q.worldedit.command.tool;

import com.sk89q.worldedit.LocalConfiguration;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.command.tool.DoubleActionBlockTool;
import com.sk89q.worldedit.entity.Player;
import com.sk89q.worldedit.extension.platform.Actor;
import com.sk89q.worldedit.extension.platform.Platform;
import com.sk89q.worldedit.extension.platform.permission.ActorSelectorLimits;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.RegionSelector;
import com.sk89q.worldedit.util.Direction;
import com.sk89q.worldedit.util.Location;
import javax.annotation.Nullable;

public class SelectionWand
implements DoubleActionBlockTool {
    @Override
    public boolean actSecondary(Platform server, LocalConfiguration config, Player player, LocalSession session, Location clicked, @Nullable Direction face) {
        BlockVector3 blockPoint;
        RegionSelector selector = session.getRegionSelector(player.getWorld());
        if (selector.selectPrimary(blockPoint = clicked.toVector().toBlockPoint(), ActorSelectorLimits.forActor(player))) {
            selector.explainPrimarySelection(player, session, blockPoint);
        }
        return true;
    }

    @Override
    public boolean actPrimary(Platform server, LocalConfiguration config, Player player, LocalSession session, Location clicked, @Nullable Direction face) {
        BlockVector3 blockPoint;
        RegionSelector selector = session.getRegionSelector(player.getWorld());
        if (selector.selectSecondary(blockPoint = clicked.toVector().toBlockPoint(), ActorSelectorLimits.forActor(player))) {
            selector.explainSecondarySelection(player, session, blockPoint);
        }
        return true;
    }

    @Override
    public boolean canUse(Actor actor) {
        return actor.hasPermission("worldedit.selection.pos");
    }
}

