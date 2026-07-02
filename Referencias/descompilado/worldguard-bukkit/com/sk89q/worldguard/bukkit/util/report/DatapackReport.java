/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sk89q.worldedit.util.report.DataReport
 *  io.papermc.paper.datapack.Datapack
 *  org.bukkit.Bukkit
 */
package com.sk89q.worldguard.bukkit.util.report;

import com.sk89q.worldedit.util.report.DataReport;
import io.papermc.paper.datapack.Datapack;
import java.util.Collection;
import org.bukkit.Bukkit;

public class DatapackReport
extends DataReport {
    public DatapackReport() {
        super("DataPacks");
        Collection packs = Bukkit.getDatapackManager().getPacks();
        this.append("Datapack Count", packs.size());
        this.append("Datapack Enabled Count", Bukkit.getDatapackManager().getEnabledPacks().size());
        for (Datapack pack : packs) {
            DataReport report = new DataReport("DataPack: " + pack.getName());
            report.append("Enabled?", pack.isEnabled());
            report.append("Name", pack.getName());
            report.append("Compatibility", pack.getCompatibility().name());
            this.append(report.getTitle(), report);
        }
    }
}

