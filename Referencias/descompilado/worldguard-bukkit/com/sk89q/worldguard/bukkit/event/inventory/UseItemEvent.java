/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  javax.annotation.Nullable
 *  org.bukkit.World
 *  org.bukkit.event.Event
 *  org.bukkit.event.HandlerList
 *  org.bukkit.inventory.ItemStack
 */
package com.sk89q.worldguard.bukkit.event.inventory;

import com.google.common.base.Preconditions;
import com.sk89q.worldguard.bukkit.cause.Cause;
import com.sk89q.worldguard.bukkit.event.DelegateEvent;
import javax.annotation.Nullable;
import org.bukkit.World;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

public class UseItemEvent
extends DelegateEvent {
    private static final HandlerList handlers = new HandlerList();
    private final World world;
    private final ItemStack itemStack;

    public UseItemEvent(@Nullable Event originalEvent, Cause cause, World world, ItemStack itemStack) {
        super(originalEvent, cause);
        Preconditions.checkNotNull((Object)world);
        Preconditions.checkNotNull((Object)itemStack);
        this.world = world;
        this.itemStack = itemStack;
    }

    public World getWorld() {
        return this.world;
    }

    public ItemStack getItemStack() {
        return this.itemStack;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}

