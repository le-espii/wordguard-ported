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
import com.sk89q.worldedit.util.TreeGenerator;
import com.sk89q.worldedit.util.formatting.text.TranslatableComponent;
import javax.annotation.Nullable;

public class TreePlanter
implements BlockTool {
    private final TreeGenerator.TreeType treeType;

    public TreePlanter(TreeGenerator.TreeType treeType) {
        this.treeType = treeType;
    }

    @Override
    public boolean canUse(Actor player) {
        return player.hasPermission("worldedit.tool.tree");
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean actPrimary(Platform server, LocalConfiguration config, Player player, LocalSession session, Location clicked, @Nullable Direction face) {
        try (EditSession editSession = BlockTool.createEditSession(player, session, clicked);){
            try {
                boolean successful = false;
                BlockVector3 pos = clicked.toVector().add(0.0, 1.0, 0.0).toBlockPoint();
                for (int i = 0; i < 10; ++i) {
                    if (!this.treeType.generate(editSession, pos)) continue;
                    successful = true;
                    break;
                }
                if (!successful) {
                    player.printError(TranslatableComponent.of("worldedit.tool.tree.obstructed"));
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
}

