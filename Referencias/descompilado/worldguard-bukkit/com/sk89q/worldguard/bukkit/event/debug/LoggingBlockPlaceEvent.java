/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.block.Block
 *  org.bukkit.block.BlockState
 *  org.bukkit.entity.Player
 *  org.bukkit.event.block.BlockPlaceEvent
 *  org.bukkit.inventory.ItemStack
 */
package com.sk89q.worldguard.bukkit.event.debug;

import com.sk89q.worldguard.bukkit.event.debug.CancelAttempt;
import com.sk89q.worldguard.bukkit.event.debug.CancelLogger;
import com.sk89q.worldguard.bukkit.event.debug.CancelLogging;
import java.util.List;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

public class LoggingBlockPlaceEvent
extends BlockPlaceEvent
implements CancelLogging {
    private final CancelLogger logger = new CancelLogger();

    public LoggingBlockPlaceEvent(Block placedBlock, BlockState replacedBlockState, Block placedAgainst, ItemStack itemInHand, Player thePlayer, boolean canBuild) {
        super(placedBlock, replacedBlockState, placedAgainst, itemInHand, thePlayer, canBuild);
    }

    @Override
    public List<CancelAttempt> getCancels() {
        return this.logger.getCancels();
    }

    public void setCancelled(boolean cancel) {
        this.logger.log(this.isCancelled(), cancel, new Exception().getStackTrace());
        super.setCancelled(cancel);
    }
}

