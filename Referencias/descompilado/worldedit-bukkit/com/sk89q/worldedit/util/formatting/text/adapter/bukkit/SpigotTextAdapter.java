/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.md_5.bungee.api.chat.BaseComponent
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.sk89q.worldedit.util.formatting.text.adapter.bukkit;

import com.sk89q.worldedit.util.formatting.text.Component;
import com.sk89q.worldedit.util.formatting.text.adapter.bukkit.SpigotAdapter;
import net.md_5.bungee.api.chat.BaseComponent;
import org.checkerframework.checker.nullness.qual.NonNull;

public interface SpigotTextAdapter {
    public static @NonNull BaseComponent[] toBungeeCord(@NonNull Component component) {
        return SpigotAdapter.toBungeeCord(component);
    }
}

