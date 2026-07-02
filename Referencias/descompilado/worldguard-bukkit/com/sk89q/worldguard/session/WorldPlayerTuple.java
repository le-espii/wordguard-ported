/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sk89q.worldedit.world.World
 */
package com.sk89q.worldguard.session;

import com.sk89q.worldedit.world.World;
import com.sk89q.worldguard.LocalPlayer;

public class WorldPlayerTuple {
    private final World world;
    private final LocalPlayer player;

    public WorldPlayerTuple(World world, LocalPlayer player) {
        this.world = world;
        this.player = player;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        WorldPlayerTuple that = (WorldPlayerTuple)o;
        if (!this.player.equals(that.player)) {
            return false;
        }
        return this.world.equals((Object)that.world);
    }

    public LocalPlayer getPlayer() {
        return this.player;
    }

    public World getWorld() {
        return this.world;
    }

    public int hashCode() {
        int result = this.world.hashCode();
        result = 31 * result + this.player.hashCode();
        return result;
    }
}

