/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.command.tool;

import com.sk89q.worldedit.LocalConfiguration;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.command.tool.DoubleActionTraceTool;
import com.sk89q.worldedit.entity.Player;
import com.sk89q.worldedit.extension.platform.Actor;
import com.sk89q.worldedit.extension.platform.Platform;
import com.sk89q.worldedit.util.Location;
import com.sk89q.worldedit.util.formatting.text.TranslatableComponent;

public class NavigationWand
implements DoubleActionTraceTool {
    private static final String PRIMARY_PERMISSION = "worldedit.navigation.thru.tool";
    private static final String SECONDARY_PERMISSION = "worldedit.navigation.jumpto.tool";

    @Override
    public boolean actSecondary(Platform server, LocalConfiguration config, Player player, LocalSession session) {
        if (!player.hasPermission(SECONDARY_PERMISSION)) {
            return false;
        }
        int maxDist = config.navigationWandMaxDistance;
        if (maxDist <= 0) {
            return false;
        }
        Location pos = player.getSolidBlockTrace(maxDist);
        if (pos != null) {
            player.findFreePosition(pos);
        } else {
            player.printError(TranslatableComponent.of("worldedit.jumpto.none"));
        }
        return true;
    }

    @Override
    public boolean actPrimary(Platform server, LocalConfiguration config, Player player, LocalSession session) {
        if (!player.hasPermission(PRIMARY_PERMISSION)) {
            return false;
        }
        int maxDist = config.navigationWandMaxDistance;
        if (maxDist <= 0) {
            return false;
        }
        if (!player.passThroughForwardWall(Math.max(1, maxDist - 10))) {
            player.printError(TranslatableComponent.of("worldedit.thru.obstructed"));
        }
        return true;
    }

    @Override
    public boolean canUse(Actor actor) {
        return actor.hasPermission(PRIMARY_PERMISSION) || actor.hasPermission(SECONDARY_PERMISSION);
    }
}

