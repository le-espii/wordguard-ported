/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  javax.annotation.Nullable
 *  org.bukkit.entity.Entity
 */
package com.sk89q.worldedit.bukkit;

import com.google.common.base.Preconditions;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.bukkit.BukkitEntityProperties;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.bukkit.adapter.BukkitImplAdapter;
import com.sk89q.worldedit.entity.BaseEntity;
import com.sk89q.worldedit.entity.Entity;
import com.sk89q.worldedit.entity.Player;
import com.sk89q.worldedit.entity.metadata.EntityProperties;
import com.sk89q.worldedit.extent.Extent;
import com.sk89q.worldedit.util.Location;
import com.sk89q.worldedit.world.NullWorld;
import java.lang.ref.WeakReference;
import javax.annotation.Nullable;

class BukkitEntity
implements Entity {
    private final WeakReference<org.bukkit.entity.Entity> entityRef;

    BukkitEntity(org.bukkit.entity.Entity entity) {
        Preconditions.checkNotNull((Object)entity);
        this.entityRef = new WeakReference<org.bukkit.entity.Entity>(entity);
    }

    @Override
    public Extent getExtent() {
        org.bukkit.entity.Entity entity = (org.bukkit.entity.Entity)this.entityRef.get();
        if (entity != null) {
            return BukkitAdapter.adapt(entity.getWorld());
        }
        return NullWorld.getInstance();
    }

    @Override
    public Location getLocation() {
        org.bukkit.entity.Entity entity = (org.bukkit.entity.Entity)this.entityRef.get();
        if (entity != null) {
            return BukkitAdapter.adapt(entity.getLocation());
        }
        return new Location(NullWorld.getInstance());
    }

    @Override
    public boolean setLocation(Location location) {
        org.bukkit.entity.Entity entity = (org.bukkit.entity.Entity)this.entityRef.get();
        if (entity != null) {
            return entity.teleport(BukkitAdapter.adapt(location));
        }
        return false;
    }

    @Override
    public BaseEntity getState() {
        org.bukkit.entity.Entity entity = (org.bukkit.entity.Entity)this.entityRef.get();
        if (entity != null) {
            if (entity instanceof Player) {
                return null;
            }
            BukkitImplAdapter adapter = WorldEditPlugin.getInstance().getBukkitImplAdapter();
            if (adapter != null) {
                return adapter.getEntity(entity);
            }
            return null;
        }
        return null;
    }

    @Override
    public boolean remove() {
        org.bukkit.entity.Entity entity = (org.bukkit.entity.Entity)this.entityRef.get();
        if (entity != null) {
            try {
                entity.remove();
            }
            catch (UnsupportedOperationException e) {
                return false;
            }
            return entity.isDead();
        }
        return true;
    }

    @Override
    @Nullable
    public <T> T getFacet(Class<? extends T> cls) {
        org.bukkit.entity.Entity entity = (org.bukkit.entity.Entity)this.entityRef.get();
        if (entity != null && EntityProperties.class.isAssignableFrom(cls)) {
            return (T)new BukkitEntityProperties(entity);
        }
        return null;
    }
}

