/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.util.report;

import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.util.report.DataReport;
import com.sk89q.worldedit.util.report.HierarchyObjectReport;

public class ConfigReport
extends DataReport {
    public ConfigReport() {
        super("WorldEdit Configuration");
        this.append("Configuration", new HierarchyObjectReport("Configuration", WorldEdit.getInstance().getConfiguration()));
    }
}

