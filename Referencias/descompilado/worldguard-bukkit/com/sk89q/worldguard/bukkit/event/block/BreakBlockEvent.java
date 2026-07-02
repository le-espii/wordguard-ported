/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  org.bukkit.Location
 *  org.bukkit.Material
 *  org.bukkit.World
 *  org.bukkit.block.Block
 *  org.bukkit.event.Event
 *  org.bukkit.event.HandlerList
 */
package com.sk89q.worldguard.bukkit.event.block;

import com.sk89q.worldguard.bukkit.cause.Cause;
import com.sk89q.worldguard.bukkit.event.block.AbstractBlockEvent;
import java.util.List;
import javax.annotation.Nullable;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class BreakBlockEvent
extends AbstractBlockEvent {
    private static final HandlerList handlers = new HandlerList();

    public BreakBlockEvent(@Nullable Event originalEvent, Cause cause, World world, List<Block> blocks, Material effectiveMaterial) {
        super(originalEvent, cause, world, blocks, effectiveMaterial);
    }

    public BreakBlockEvent(@Nullable Event originalEvent, Cause cause, Block block) {
        super(originalEvent, cause, block);
    }

    public BreakBlockEvent(@Nullable Event originalEvent, Cause cause, Location target, Material effectiveMaterial) {
        super(originalEvent, cause, target, effectiveMaterial);
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}

