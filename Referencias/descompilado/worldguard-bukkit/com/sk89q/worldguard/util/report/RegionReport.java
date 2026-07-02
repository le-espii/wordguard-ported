/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sk89q.worldedit.util.report.DataReport
 */
package com.sk89q.worldguard.util.report;

import com.sk89q.worldedit.util.report.DataReport;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class RegionReport
extends DataReport {
    public RegionReport(ProtectedRegion region) {
        super("Region: " + region.getId());
        this.append("Type", (Object)region.getType());
        this.append("Priority", region.getPriority());
        this.append("Parent", region.getParent() == null ? "<none>" : region.getParent().getId());
        this.append("Owners", region.getOwners());
        this.append("Members", region.getMembers());
        this.append("Flags", region.getFlags());
        this.append("Bounds", String.valueOf(region.getMinimumPoint()) + " -> " + String.valueOf(region.getMaximumPoint()));
    }
}

