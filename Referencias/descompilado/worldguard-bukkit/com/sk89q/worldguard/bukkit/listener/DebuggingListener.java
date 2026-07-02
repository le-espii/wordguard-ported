/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  javax.annotation.Nullable
 *  org.bukkit.Location
 *  org.bukkit.block.Block
 *  org.bukkit.event.Event
 *  org.bukkit.event.Event$Result
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.EventPriority
 *  org.bukkit.event.player.PlayerInteractEvent
 */
package com.sk89q.worldguard.bukkit.listener;

import com.google.common.base.Preconditions;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.bukkit.event.block.BreakBlockEvent;
import com.sk89q.worldguard.bukkit.event.block.PlaceBlockEvent;
import com.sk89q.worldguard.bukkit.event.block.UseBlockEvent;
import com.sk89q.worldguard.bukkit.event.entity.DamageEntityEvent;
import com.sk89q.worldguard.bukkit.event.entity.DestroyEntityEvent;
import com.sk89q.worldguard.bukkit.event.entity.SpawnEntityEvent;
import com.sk89q.worldguard.bukkit.event.entity.UseEntityEvent;
import com.sk89q.worldguard.bukkit.event.inventory.UseItemEvent;
import com.sk89q.worldguard.bukkit.listener.AbstractListener;
import java.util.List;
import java.util.logging.Logger;
import javax.annotation.Nullable;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerInteractEvent;

public class DebuggingListener
extends AbstractListener {
    private final Logger logger;

    public DebuggingListener(WorldGuardPlugin plugin, Logger logger) {
        super(plugin);
        Preconditions.checkNotNull((Object)logger);
        this.logger = logger;
    }

    @EventHandler(priority=EventPriority.MONITOR)
    public void onPlaceBlock(PlaceBlockEvent event) {
        StringBuilder builder = new StringBuilder();
        builder.append("PLACE");
        builder.append(" ");
        builder.append(event.getEffectiveMaterial());
        builder.append(" ");
        builder.append("@").append(DebuggingListener.toBlockString(event.getBlocks()));
        builder.append(" ");
        builder.append("[").append(event.getCause()).append("]");
        builder.append(" ");
        builder.append(": ").append(this.getEventName(event.getOriginalEvent()));
        if (event.getResult() != Event.Result.DEFAULT) {
            builder.append(" [").append(event.getResult()).append("]");
        }
        this.logger.info(builder.toString());
    }

    @EventHandler(priority=EventPriority.MONITOR)
    public void onBreakBlock(BreakBlockEvent event) {
        StringBuilder builder = new StringBuilder();
        builder.append("DIG");
        builder.append(" ");
        builder.append(event.getEffectiveMaterial());
        builder.append(" ");
        builder.append("[").append(event.getCause()).append("]");
        builder.append(" ");
        builder.append("@").append(DebuggingListener.toBlockString(event.getBlocks()));
        builder.append(" ");
        builder.append(": ").append(this.getEventName(event.getOriginalEvent()));
        if (event.getResult() != Event.Result.DEFAULT) {
            builder.append(" [").append(event.getResult()).append("]");
        }
        this.logger.info(builder.toString());
    }

    @EventHandler(priority=EventPriority.MONITOR)
    public void onUseBlock(UseBlockEvent event) {
        StringBuilder builder = new StringBuilder();
        builder.append("INTERACT");
        builder.append(" ");
        builder.append(event.getEffectiveMaterial());
        builder.append(" ");
        builder.append("[").append(event.getCause()).append("]");
        builder.append(" ");
        builder.append("@").append(DebuggingListener.toBlockString(event.getBlocks()));
        builder.append(" ");
        builder.append(": ").append(this.getEventName(event.getOriginalEvent()));
        if (event.getOriginalEvent() instanceof PlayerInteractEvent) {
            builder.append(".").append(((PlayerInteractEvent)event.getOriginalEvent()).getAction());
        }
        if (event.getResult() != Event.Result.DEFAULT) {
            builder.append(" [").append(event.getResult()).append("]");
        }
        this.logger.info(builder.toString());
    }

    @EventHandler(priority=EventPriority.MONITOR)
    public void onSpawnEntity(SpawnEntityEvent event) {
        StringBuilder builder = new StringBuilder();
        builder.append("SPAWN");
        builder.append(" ");
        builder.append(event.getEffectiveType());
        builder.append(" ");
        builder.append("[").append(event.getCause()).append("]");
        builder.append(" ");
        builder.append("@").append(DebuggingListener.toBlockString(event.getTarget()));
        builder.append(" ");
        builder.append(": ").append(this.getEventName(event.getOriginalEvent()));
        if (event.getResult() != Event.Result.DEFAULT) {
            builder.append(" [").append(event.getResult()).append("]");
        }
        this.logger.info(builder.toString());
    }

    @EventHandler(priority=EventPriority.MONITOR)
    public void onDestroyEntity(DestroyEntityEvent event) {
        StringBuilder builder = new StringBuilder();
        builder.append("DESTROY");
        builder.append(" ");
        builder.append(event.getEntity().getType());
        builder.append(" ");
        builder.append("[").append(event.getCause()).append("]");
        builder.append(" ");
        builder.append("@").append(DebuggingListener.toBlockString(event.getTarget()));
        builder.append(" ");
        builder.append(": ").append(this.getEventName(event.getOriginalEvent()));
        if (event.getResult() != Event.Result.DEFAULT) {
            builder.append(" [").append(event.getResult()).append("]");
        }
        this.logger.info(builder.toString());
    }

    @EventHandler(priority=EventPriority.MONITOR)
    public void onUseEntity(UseEntityEvent event) {
        StringBuilder builder = new StringBuilder();
        builder.append("INTERACT");
        builder.append(" ");
        builder.append(event.getEntity().getType());
        builder.append(" ");
        builder.append("[").append(event.getCause()).append("]");
        builder.append(" ");
        builder.append("@").append(DebuggingListener.toBlockString(event.getTarget()));
        builder.append(" ");
        builder.append(": ").append(this.getEventName(event.getOriginalEvent()));
        if (event.getResult() != Event.Result.DEFAULT) {
            builder.append(" [").append(event.getResult()).append("]");
        }
        this.logger.info(builder.toString());
    }

    @EventHandler(priority=EventPriority.MONITOR)
    public void onDamageEntity(DamageEntityEvent event) {
        StringBuilder builder = new StringBuilder();
        builder.append("DAMAGE");
        builder.append(" ");
        builder.append(event.getEntity().getType());
        builder.append(" ");
        builder.append("[").append(event.getCause()).append("]");
        builder.append(" ");
        builder.append("@").append(DebuggingListener.toBlockString(event.getTarget()));
        builder.append(" ");
        builder.append(": ").append(this.getEventName(event.getOriginalEvent()));
        if (event.getResult() != Event.Result.DEFAULT) {
            builder.append(" [").append(event.getResult()).append("]");
        }
        this.logger.info(builder.toString());
    }

    @EventHandler(priority=EventPriority.MONITOR)
    public void onUseItem(UseItemEvent event) {
        StringBuilder builder = new StringBuilder();
        builder.append("USE");
        builder.append(" ");
        builder.append(event.getItemStack().getType());
        builder.append(" ");
        builder.append("[").append(event.getCause()).append("]");
        builder.append(" ");
        builder.append("@").append(event.getWorld().getName());
        builder.append(" ");
        builder.append(": ").append(this.getEventName(event.getOriginalEvent()));
        if (event.getResult() != Event.Result.DEFAULT) {
            builder.append(" [").append(event.getResult()).append("]");
        }
        this.logger.info(builder.toString());
    }

    private static String toBlockString(Location location) {
        return location.getBlockX() + "," + location.getBlockY() + "," + location.getBlockZ();
    }

    private static String toBlockString(List<Block> blocks) {
        StringBuilder builder = new StringBuilder();
        boolean first = true;
        for (Block block : blocks) {
            if (!first) {
                builder.append("|");
            }
            builder.append(block.getX()).append(",").append(block.getY()).append(",").append(block.getZ());
            first = false;
        }
        return builder.toString();
    }

    private String getEventName(@Nullable Event event) {
        return event != null ? event.getEventName() : "?";
    }
}

