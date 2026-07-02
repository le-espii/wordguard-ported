/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.command.CommandSender
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.sk89q.worldedit.util.formatting.text.adapter.bukkit;

import com.sk89q.worldedit.util.formatting.text.Component;
import com.sk89q.worldedit.util.formatting.text.adapter.bukkit.TextAdapter0;
import java.util.Collections;
import org.bukkit.command.CommandSender;
import org.checkerframework.checker.nullness.qual.NonNull;

public interface TextAdapter {
    public static void sendMessage(@NonNull CommandSender viewer, @NonNull Component component) {
        TextAdapter.sendComponent(Collections.singleton(viewer), component);
    }

    public static void sendMessage(@NonNull Iterable<? extends CommandSender> viewers, @NonNull Component component) {
        TextAdapter0.sendComponent(viewers, component, false);
    }

    @Deprecated
    public static void sendComponent(@NonNull CommandSender viewer, @NonNull Component component) {
        TextAdapter.sendComponent(Collections.singleton(viewer), component);
    }

    @Deprecated
    public static void sendComponent(@NonNull Iterable<? extends CommandSender> viewers, @NonNull Component component) {
        TextAdapter0.sendComponent(viewers, component, false);
    }

    public static void sendActionBar(@NonNull CommandSender viewer, @NonNull Component component) {
        TextAdapter.sendActionBar(Collections.singleton(viewer), component);
    }

    public static void sendActionBar(@NonNull Iterable<? extends CommandSender> viewers, @NonNull Component component) {
        TextAdapter0.sendComponent(viewers, component, true);
    }
}

