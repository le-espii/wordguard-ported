/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 *  com.google.common.collect.Iterables
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 */
package com.sk89q.worldedit.util.formatting.text.adapter.bukkit;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.sk89q.worldedit.util.formatting.text.Component;
import com.sk89q.worldedit.util.formatting.text.adapter.bukkit.Adapter;
import com.sk89q.worldedit.util.formatting.text.adapter.bukkit.CraftBukkitAdapter;
import com.sk89q.worldedit.util.formatting.text.adapter.bukkit.LegacyAdapter;
import com.sk89q.worldedit.util.formatting.text.adapter.bukkit.SpigotAdapter;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

final class TextAdapter0 {
    private static final List<Adapter> ADAPTERS = TextAdapter0.pickAdapters();

    TextAdapter0() {
    }

    private static List<Adapter> pickAdapters() {
        ImmutableList.Builder adapters = ImmutableList.builder();
        if (TextAdapter0.isSpigotAdapterSupported()) {
            adapters.add((Object)new SpigotAdapter());
        }
        adapters.add((Object)new CraftBukkitAdapter());
        adapters.add((Object)new LegacyAdapter());
        return adapters.build();
    }

    private static boolean isSpigotAdapterSupported() {
        try {
            Class<?> spigot = Player.class.getMethod("spigot", new Class[0]).getReturnType();
            spigot.getMethod("sendMessage", Class.forName("net.md_5.bungee.api.ChatMessageType"), Class.forName("net.md_5.bungee.api.chat.BaseComponent"));
            return true;
        }
        catch (ClassNotFoundException | NoSuchMethodException e) {
            return false;
        }
    }

    static void sendComponent(Iterable<? extends CommandSender> viewers, Component component, boolean actionBar) {
        LinkedList list = new LinkedList();
        Iterables.addAll(list, viewers);
        Iterator<Adapter> it = ADAPTERS.iterator();
        while (it.hasNext() && !list.isEmpty()) {
            Adapter adapter = it.next();
            if (actionBar) {
                adapter.sendActionBar(list, component);
                continue;
            }
            adapter.sendMessage(list, component);
        }
    }
}

