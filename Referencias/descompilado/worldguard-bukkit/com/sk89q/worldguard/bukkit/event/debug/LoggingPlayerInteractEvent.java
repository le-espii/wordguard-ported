/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.block.Block
 *  org.bukkit.block.BlockFace
 *  org.bukkit.entity.Player
 *  org.bukkit.event.Event$Result
 *  org.bukkit.event.block.Action
 *  org.bukkit.event.player.PlayerInteractEvent
 *  org.bukkit.inventory.ItemStack
 */
package com.sk89q.worldguard.bukkit.event.debug;

import com.sk89q.worldguard.bukkit.event.debug.CancelAttempt;
import com.sk89q.worldguard.bukkit.event.debug.CancelLogger;
import com.sk89q.worldguard.bukkit.event.debug.CancelLogging;
import java.util.List;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class LoggingPlayerInteractEvent
extends PlayerInteractEvent
implements CancelLogging {
    private final CancelLogger logger = new CancelLogger();

    public LoggingPlayerInteractEvent(Player who, Action action, ItemStack item, Block clickedBlock, BlockFace clickedFace) {
        super(who, action, item, clickedBlock, clickedFace);
    }

    @Override
    public List<CancelAttempt> getCancels() {
        return this.logger.getCancels();
    }

    public void setUseInteractedBlock(Event.Result useInteractedBlock) {
        this.logger.log(this.useInteractedBlock() == Event.Result.DENY, useInteractedBlock == Event.Result.DENY, new Exception().getStackTrace());
        super.setUseInteractedBlock(useInteractedBlock);
    }

    public void setUseItemInHand(Event.Result useItemInHand) {
        this.logger.log(this.useItemInHand() == Event.Result.DENY, useItemInHand == Event.Result.DENY, new Exception().getStackTrace());
        super.setUseItemInHand(useItemInHand);
    }

    public void setCancelled(boolean cancel) {
        this.logger.log(this.isCancelled(), cancel, new Exception().getStackTrace());
        super.setCancelled(cancel);
    }
}

