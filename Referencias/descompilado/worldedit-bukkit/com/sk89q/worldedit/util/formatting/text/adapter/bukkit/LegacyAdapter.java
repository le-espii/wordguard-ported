/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.command.CommandSender
 */
package com.sk89q.worldedit.util.formatting.text.adapter.bukkit;

import com.sk89q.worldedit.util.formatting.text.Component;
import com.sk89q.worldedit.util.formatting.text.adapter.bukkit.Adapter;
import com.sk89q.worldedit.util.formatting.text.serializer.legacy.LegacyComponentSerializer;
import java.util.List;
import org.bukkit.command.CommandSender;

final class LegacyAdapter
implements Adapter {
    LegacyAdapter() {
    }

    @Override
    public void sendMessage(List<? extends CommandSender> viewers, Component component) {
        String legacy = LegacyComponentSerializer.legacy().serialize(component);
        for (CommandSender commandSender : viewers) {
            commandSender.sendMessage(legacy);
        }
        viewers.clear();
    }

    @Override
    public void sendActionBar(List<? extends CommandSender> viewers, Component component) {
    }
}

