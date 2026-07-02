/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldguard.blacklist.event;

import com.sk89q.worldguard.blacklist.event.BlacklistEvent;
import com.sk89q.worldguard.blacklist.event.BlockBreakBlacklistEvent;
import com.sk89q.worldguard.blacklist.event.BlockDispenseBlacklistEvent;
import com.sk89q.worldguard.blacklist.event.BlockInteractBlacklistEvent;
import com.sk89q.worldguard.blacklist.event.BlockPlaceBlacklistEvent;
import com.sk89q.worldguard.blacklist.event.ItemAcquireBlacklistEvent;
import com.sk89q.worldguard.blacklist.event.ItemDestroyWithBlacklistEvent;
import com.sk89q.worldguard.blacklist.event.ItemDropBlacklistEvent;
import com.sk89q.worldguard.blacklist.event.ItemEquipBlacklistEvent;
import com.sk89q.worldguard.blacklist.event.ItemUseBlacklistEvent;

public enum EventType {
    BREAK(BlockBreakBlacklistEvent.class, "on-break"),
    PLACE(BlockPlaceBlacklistEvent.class, "on-place"),
    INTERACT(BlockInteractBlacklistEvent.class, "on-interact"),
    DISPENSE(BlockDispenseBlacklistEvent.class, "on-dispense"),
    DESTROY_WITH(ItemDestroyWithBlacklistEvent.class, "on-destroy-with"),
    ACQUIRE(ItemAcquireBlacklistEvent.class, "on-acquire"),
    EQUIP(ItemEquipBlacklistEvent.class, "on-equip"),
    DROP(ItemDropBlacklistEvent.class, "on-drop"),
    USE(ItemUseBlacklistEvent.class, "on-use");

    private final Class<? extends BlacklistEvent> eventClass;
    private final String ruleName;

    private EventType(Class<? extends BlacklistEvent> eventClass, String ruleName) {
        this.eventClass = eventClass;
        this.ruleName = ruleName;
    }

    public Class<? extends BlacklistEvent> getEventClass() {
        return this.eventClass;
    }

    public String getRuleName() {
        return this.ruleName;
    }
}

