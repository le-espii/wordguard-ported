/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  org.bukkit.Location
 *  org.bukkit.Material
 *  org.bukkit.block.Block
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.EntityType
 */
package com.sk89q.worldguard.bukkit;

import com.sk89q.worldguard.bukkit.cause.Cause;
import com.sk89q.worldguard.bukkit.event.DelegateEvents;
import com.sk89q.worldguard.bukkit.event.block.BreakBlockEvent;
import com.sk89q.worldguard.bukkit.event.block.PlaceBlockEvent;
import com.sk89q.worldguard.bukkit.event.block.UseBlockEvent;
import com.sk89q.worldguard.bukkit.event.entity.DamageEntityEvent;
import com.sk89q.worldguard.bukkit.event.entity.SpawnEntityEvent;
import com.sk89q.worldguard.bukkit.event.entity.UseEntityEvent;
import com.sk89q.worldguard.bukkit.util.Events;
import javax.annotation.Nullable;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

public class ProtectionQuery {
    public boolean testBlockPlace(@Nullable Object cause, Location location, Material newMaterial) {
        return !Events.fireAndTestCancel(DelegateEvents.setSilent(new PlaceBlockEvent(null, Cause.create(cause), location, newMaterial)));
    }

    public boolean testBlockBreak(@Nullable Object cause, Block block) {
        return !Events.fireAndTestCancel(DelegateEvents.setSilent(new BreakBlockEvent(null, Cause.create(cause), block)));
    }

    public boolean testBlockInteract(@Nullable Object cause, Block block) {
        return !Events.fireAndTestCancel(DelegateEvents.setSilent(new UseBlockEvent(null, Cause.create(cause), block)));
    }

    public boolean testEntityPlace(@Nullable Object cause, Location location, EntityType type) {
        return !Events.fireAndTestCancel(DelegateEvents.setSilent(new SpawnEntityEvent(null, Cause.create(cause), location, type)));
    }

    public boolean testEntityDestroy(@Nullable Object cause, Entity entity) {
        return !Events.fireAndTestCancel(DelegateEvents.setSilent(new SpawnEntityEvent(null, Cause.create(cause), entity)));
    }

    public boolean testEntityInteract(@Nullable Object cause, Entity entity) {
        return !Events.fireAndTestCancel(DelegateEvents.setSilent(new UseEntityEvent(null, Cause.create(cause), entity)));
    }

    public boolean testEntityDamage(@Nullable Object cause, Entity entity) {
        return !Events.fireAndTestCancel(DelegateEvents.setSilent(new DamageEntityEvent(null, Cause.create(cause), entity)));
    }
}

