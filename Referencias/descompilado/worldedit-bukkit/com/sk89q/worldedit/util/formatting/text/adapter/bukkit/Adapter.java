/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.command.CommandSender
 */
package com.sk89q.worldedit.util.formatting.text.adapter.bukkit;

import com.sk89q.worldedit.util.formatting.text.Component;
import java.util.List;
import org.bukkit.command.CommandSender;

interface Adapter {
    public void sendMessage(List<? extends CommandSender> var1, Component var2);

    public void sendActionBar(List<? extends CommandSender> var1, Component var2);
}

