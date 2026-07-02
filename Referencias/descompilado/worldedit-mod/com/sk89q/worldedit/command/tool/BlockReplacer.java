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
import com.sk89q.worldedit.command.tool.DoubleActionBlockTool;
import com.sk89q.worldedit.entity.Player;
import com.sk89q.worldedit.extension.platform.Actor;
import com.sk89q.worldedit.extension.platform.Platform;
import com.sk89q.worldedit.extent.inventory.BlockBag;
import com.sk89q.worldedit.function.pattern.Pattern;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.util.Direction;
import com.sk89q.worldedit.util.Location;
import com.sk89q.worldedit.util.formatting.text.TranslatableComponent;
import com.sk89q.worldedit.world.block.BaseBlock;
import javax.annotation.Nullable;

public class BlockReplacer
implements DoubleActionBlockTool {
    private Pattern pattern;

    public BlockReplacer(Pattern pattern) {
        this.pattern = pattern;
    }

    @Override
    public boolean canUse(Actor player) {
        return player.hasPermission("worldedit.tool.replacer");
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Override
    public boolean actPrimary(Platform server, LocalConfiguration config, Player player, LocalSession session, Location clicked, @Nullable Direction face) {
        BlockBag bag = session.getBlockBag(player);
        try (EditSession editSession = session.createEditSession(player);){
            try {
                editSession.disableBuffering();
                BlockVector3 position = clicked.toVector().toBlockPoint();
                editSession.setBlock(position, this.pattern);
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
    public boolean actSecondary(Platform server, LocalConfiguration config, Player player, LocalSession session, Location clicked, @Nullable Direction face) {
        BaseBlock targetBlock = player.getWorld().getFullBlock(clicked.toVector().toBlockPoint());
        if (targetBlock != null) {
            this.pattern = targetBlock;
            player.printInfo(TranslatableComponent.of("worldedit.tool.repl.switched", targetBlock.getBlockType().getRichName()));
        }
        return true;
    }
}

