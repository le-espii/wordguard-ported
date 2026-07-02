/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sk89q.worldedit.util.report.DataReport
 *  org.bukkit.Bukkit
 *  org.bukkit.plugin.Plugin
 */
package com.sk89q.worldguard.bukkit.util.report;

import com.sk89q.worldedit.util.report.DataReport;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public class PluginReport
extends DataReport {
    public PluginReport() {
        super("Plugins");
        Plugin[] plugins = Bukkit.getServer().getPluginManager().getPlugins();
        this.append("Plugin Count", plugins.length);
        for (Plugin plugin : plugins) {
            DataReport report = new DataReport("Plugin: " + plugin.getName());
            report.append("Enabled?", plugin.isEnabled());
            report.append("Full Name", plugin.getDescription().getFullName());
            report.append("Version", plugin.getDescription().getVersion());
            report.append("Website", plugin.getDescription().getWebsite());
            report.append("Description", plugin.getDescription().getDescription());
            report.append("Authors", (Object)plugin.getDescription().getAuthors());
            report.append("Load Before", (Object)plugin.getDescription().getLoadBefore());
            report.append("Dependencies", (Object)plugin.getDescription().getDepend());
            report.append("Soft Dependencies", (Object)plugin.getDescription().getSoftDepend());
            report.append("Folder", (Object)plugin.getDataFolder().getAbsoluteFile());
            report.append("Entry Point", plugin.getDescription().getMain());
            report.append("Class", plugin.getClass().getName());
            report.append("Class Source", (Object)plugin.getClass().getProtectionDomain().getCodeSource().getLocation());
            this.append(report.getTitle(), report);
        }
    }
}

