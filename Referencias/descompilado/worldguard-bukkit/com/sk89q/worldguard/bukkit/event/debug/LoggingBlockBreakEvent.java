/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.block.Block
 *  org.bukkit.entity.Player
 *  org.bukkit.event.block.BlockBreakEvent
 */
package com.sk89q.worldguard.bukkit.event.debug;

import com.sk89q.worldguard.bukkit.event.debug.CancelAttempt;
import com.sk89q.worldguard.bukkit.event.debug.CancelLogger;
import com.sk89q.worldguard.bukkit.event.debug.CancelLogging;
import java.util.List;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;

public class LoggingBlockBreakEvent
extends BlockBreakEvent
implements CancelLogging {
    private final CancelLogger logger = new CancelLogger();

    public LoggingBlockBreakEvent(Block block, Player player) {
        super(block, player);
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

