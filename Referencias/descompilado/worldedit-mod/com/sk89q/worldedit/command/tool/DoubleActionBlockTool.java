/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.sk89q.worldedit.command.tool;

import com.sk89q.worldedit.LocalConfiguration;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.command.tool.BlockTool;
import com.sk89q.worldedit.entity.Player;
import com.sk89q.worldedit.extension.platform.Platform;
import com.sk89q.worldedit.internal.util.DeprecationUtil;
import com.sk89q.worldedit.internal.util.NonAbstractForCompatibility;
import com.sk89q.worldedit.util.Direction;
import com.sk89q.worldedit.util.Location;
import javax.annotation.Nullable;

public interface DoubleActionBlockTool
extends BlockTool {
    @Deprecated
    default public boolean actSecondary(Platform server, LocalConfiguration config, Player player, LocalSession session, Location clicked) {
        return this.actSecondary(server, config, player, session, clicked, null);
    }

    @NonAbstractForCompatibility(delegateName="actSecondary", delegateParams={Platform.class, LocalConfiguration.class, Player.class, LocalSession.class, Location.class})
    default public boolean actSecondary(Platform server, LocalConfiguration config, Player player, LocalSession session, Location clicked, @Nullable Direction face) {
        DeprecationUtil.checkDelegatingOverride(this.getClass());
        return this.actSecondary(server, config, player, session, clicked);
    }
}

