/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sk89q.worldedit.math.BlockVector3
 *  com.sk89q.worldedit.util.report.DataReport
 */
package com.sk89q.worldguard.util.report;

import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.util.report.DataReport;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.util.report.RegionReport;

public class ApplicableRegionsReport
extends DataReport {
    public ApplicableRegionsReport(LocalPlayer player) {
        super("Applicable regions");
        BlockVector3 position = player.getBlockIn().toVector().toBlockPoint();
        this.append("Location", player.getWorld().getName() + " @ " + String.valueOf(position));
        RegionManager mgr = WorldGuard.getInstance().getPlatform().getRegionContainer().get(player.getWorld());
        if (mgr == null) {
            this.append("Regions", "Disabled for current world");
        } else {
            ApplicableRegionSet rgs = mgr.getApplicableRegions(position);
            if (rgs.getRegions().isEmpty()) {
                this.append("Regions", "None");
            } else {
                DataReport regions = new DataReport("Regions");
                for (ProtectedRegion region : rgs.getRegions()) {
                    boolean inherited = !region.contains(position);
                    regions.append(region.getId() + (inherited ? "*" : ""), (Object)new RegionReport(region));
                }
                this.append(regions.getTitle(), regions);
            }
        }
    }
}

