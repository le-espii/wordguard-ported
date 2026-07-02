/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.command.Command
 *  org.bukkit.command.CommandSender
 */
package com.sk89q.bukkit.util;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public interface CommandInspector {
    public String getShortText(Command var1);

    public String getFullText(Command var1);

    public boolean testPermission(CommandSender var1, Command var2);
}

