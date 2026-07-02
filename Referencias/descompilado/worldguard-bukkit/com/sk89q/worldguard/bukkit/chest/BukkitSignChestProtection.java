/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sk89q.worldedit.bukkit.BukkitAdapter
 *  com.sk89q.worldedit.util.Location
 *  org.bukkit.block.BlockState
 *  org.bukkit.block.Sign
 */
package com.sk89q.worldguard.bukkit.chest;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.util.Location;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.chest.SignChestProtection;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;

public class BukkitSignChestProtection
extends SignChestProtection {
    private Boolean isProtectedSign(Sign sign, LocalPlayer player) {
        if (sign.getLine(0).equalsIgnoreCase("[Lock]")) {
            if (player == null) {
                return true;
            }
            String name = player.getName();
            return !name.equalsIgnoreCase(sign.getLine(1).trim()) && !name.equalsIgnoreCase(sign.getLine(2).trim()) && !name.equalsIgnoreCase(sign.getLine(3).trim());
        }
        return null;
    }

    @Override
    public Boolean isProtectedSign(Location block, LocalPlayer player) {
        BlockState state = BukkitAdapter.adapt((Location)block).getBlock().getState();
        if (!(state instanceof Sign)) {
            return null;
        }
        return this.isProtectedSign((Sign)state, player);
    }
}

