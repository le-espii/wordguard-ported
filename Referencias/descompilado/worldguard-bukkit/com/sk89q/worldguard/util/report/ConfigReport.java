/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sk89q.worldedit.WorldEdit
 *  com.sk89q.worldedit.extension.platform.Capability
 *  com.sk89q.worldedit.util.report.DataReport
 *  com.sk89q.worldedit.util.report.HierarchyObjectReport
 *  com.sk89q.worldedit.world.World
 */
package com.sk89q.worldguard.util.report;

import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.extension.platform.Capability;
import com.sk89q.worldedit.util.report.DataReport;
import com.sk89q.worldedit.util.report.HierarchyObjectReport;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.blacklist.Blacklist;
import com.sk89q.worldguard.config.WorldConfiguration;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.util.report.RegionReport;
import java.util.List;

public class ConfigReport
extends DataReport {
    public ConfigReport() {
        super("WorldGuard Configuration");
        List worlds = WorldEdit.getInstance().getPlatformManager().queryCapability(Capability.GAME_HOOKS).getWorlds();
        this.append("Configuration", new HierarchyObjectReport("Configuration", (Object)WorldGuard.getInstance().getPlatform().getGlobalStateManager()));
        for (World world : worlds) {
            WorldConfiguration config = WorldGuard.getInstance().getPlatform().getGlobalStateManager().get(world);
            DataReport report = new DataReport("World: " + world.getName());
            report.append("Configuration", (Object)new HierarchyObjectReport("Configuration", (Object)config));
            Blacklist blacklist = config.getBlacklist();
            if (blacklist != null) {
                DataReport section = new DataReport("Blacklist");
                section.append("Rule Count", blacklist.getItemCount());
                section.append("Whitelist Mode?", blacklist.isWhitelist());
                report.append(section.getTitle(), (Object)section);
            } else {
                report.append("Blacklist", "<Disabled>");
            }
            RegionManager regions = WorldGuard.getInstance().getPlatform().getRegionContainer().get(world);
            if (regions != null) {
                DataReport section = new DataReport("Regions");
                section.append("Region Count", regions.size());
                ProtectedRegion global = regions.getRegion("__global__");
                if (global != null) {
                    section.append("__global__", (Object)new RegionReport(global));
                } else {
                    section.append("__global__", "<Undefined>");
                }
                report.append(section.getTitle(), (Object)section);
            } else {
                report.append("Regions", "<Disabled>");
            }
            this.append(report.getTitle(), report);
        }
    }
}

