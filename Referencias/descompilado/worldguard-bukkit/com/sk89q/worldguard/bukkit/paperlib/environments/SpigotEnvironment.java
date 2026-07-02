/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldguard.bukkit.paperlib.environments;

import com.sk89q.worldguard.bukkit.paperlib.environments.CraftBukkitEnvironment;

public class SpigotEnvironment
extends CraftBukkitEnvironment {
    @Override
    public String getName() {
        return "Spigot";
    }

    @Override
    public boolean isSpigot() {
        return true;
    }
}

