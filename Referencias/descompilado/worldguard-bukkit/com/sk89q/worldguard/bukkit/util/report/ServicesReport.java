/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sk89q.worldedit.util.report.DataReport
 *  org.bukkit.Bukkit
 *  org.bukkit.plugin.ServicesManager
 */
package com.sk89q.worldguard.bukkit.util.report;

import com.sk89q.worldedit.util.report.DataReport;
import java.util.Collection;
import org.bukkit.Bukkit;
import org.bukkit.plugin.ServicesManager;

public class ServicesReport
extends DataReport {
    public ServicesReport() {
        super("Services");
        ServicesManager manager = Bukkit.getServer().getServicesManager();
        Collection services = manager.getKnownServices();
        for (Class service : services) {
            Object provider = manager.load(service);
            if (provider == null) continue;
            this.append(service.getName(), provider);
        }
    }
}

