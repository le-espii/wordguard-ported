/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sk89q.worldedit.extent.Extent
 *  com.sk89q.worldedit.math.Vector3
 *  com.sk89q.worldedit.util.Location
 *  com.sk89q.worldedit.world.NullWorld
 *  com.sk89q.worldedit.world.World
 *  javax.annotation.Nullable
 */
package com.sk89q.worldguard.protection.flags;

import com.sk89q.worldedit.extent.Extent;
import com.sk89q.worldedit.math.Vector3;
import com.sk89q.worldedit.util.Location;
import com.sk89q.worldedit.world.NullWorld;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldguard.WorldGuard;
import java.util.Optional;
import javax.annotation.Nullable;

class LazyLocation
extends Location {
    private final String worldName;

    @Nullable
    private static World findWorld(String worldName) {
        return WorldGuard.getInstance().getPlatform().getMatcher().getWorldByName(worldName);
    }

    LazyLocation(String worldName, Vector3 position, float yaw, float pitch) {
        super((Extent)Optional.ofNullable(LazyLocation.findWorld(worldName)).orElse((World)NullWorld.getInstance()), position, yaw, pitch);
        this.worldName = worldName;
    }

    LazyLocation(String worldName, Vector3 position) {
        super((Extent)Optional.ofNullable(LazyLocation.findWorld(worldName)).orElse((World)NullWorld.getInstance()), position);
        this.worldName = worldName;
    }

    public Extent getExtent() {
        if (super.getExtent() != NullWorld.getInstance()) {
            return super.getExtent();
        }
        return (Extent)Optional.ofNullable(LazyLocation.findWorld(this.getWorldName())).orElse((World)new NullWorld(){

            public String getName() {
                return LazyLocation.this.worldName;
            }
        });
    }

    public String getWorldName() {
        return this.worldName;
    }

    public LazyLocation setAngles(float yaw, float pitch) {
        return new LazyLocation(this.worldName, this.toVector(), yaw, pitch);
    }

    public LazyLocation setPosition(Vector3 position) {
        return new LazyLocation(this.worldName, position, this.getYaw(), this.getPitch());
    }

    public LazyLocation add(Vector3 other) {
        return this.setPosition(this.toVector().add(other));
    }

    public LazyLocation add(double x, double y, double z) {
        return this.setPosition(this.toVector().add(x, y, z));
    }

    public String toString() {
        if (this.getPitch() == 0.0f && this.getYaw() == 0.0f) {
            return String.join((CharSequence)", ", this.worldName, String.valueOf((int)this.getX()), String.valueOf((int)this.getY()), String.valueOf((int)this.getZ()));
        }
        return String.join((CharSequence)", ", this.worldName, String.valueOf((int)this.getX()), String.valueOf((int)this.getY()), String.valueOf((int)this.getZ()), String.valueOf((int)this.getPitch()), String.valueOf((int)this.getYaw()));
    }
}

