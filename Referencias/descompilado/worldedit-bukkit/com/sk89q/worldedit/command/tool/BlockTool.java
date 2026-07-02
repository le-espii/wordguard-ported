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
import com.sk89q.worldedit.command.tool.Tool;
import com.sk89q.worldedit.entity.Player;
import com.sk89q.worldedit.extension.platform.Platform;
import com.sk89q.worldedit.extent.Extent;
import com.sk89q.worldedit.internal.util.DeprecationUtil;
import com.sk89q.worldedit.internal.util.NonAbstractForCompatibility;
import com.sk89q.worldedit.util.Direction;
import com.sk89q.worldedit.util.Location;
import com.sk89q.worldedit.world.World;
import javax.annotation.Nullable;

public interface BlockTool
extends Tool {
    public static World requireWorld(Location clicked) {
        Extent extent = clicked.getExtent();
        if (!(extent instanceof World)) {
            throw new IllegalArgumentException("Location is not in a world: " + String.valueOf(clicked));
        }
        World world = (World)extent;
        return world;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static EditSession createEditSession(Player player, LocalSession session, Location clicked) {
        World overrideToRestore = session.getWorldOverride();
        session.setWorldOverride(BlockTool.requireWorld(clicked));
        try {
            EditSession editSession = session.createEditSession(player);
            return editSession;
        }
        finally {
            session.setWorldOverride(overrideToRestore);
        }
    }

    @Deprecated
    default public boolean actPrimary(Platform server, LocalConfiguration config, Player player, LocalSession session, Location clicked) {
        return this.actPrimary(server, config, player, session, clicked, null);
    }

    @NonAbstractForCompatibility(delegateName="actPrimary", delegateParams={Platform.class, LocalConfiguration.class, Player.class, LocalSession.class, Location.class})
    default public boolean actPrimary(Platform server, LocalConfiguration config, Player player, LocalSession session, Location clicked, @Nullable Direction face) {
        DeprecationUtil.checkDelegatingOverride(this.getClass());
        return this.actPrimary(server, config, player, session, clicked);
    }
}

