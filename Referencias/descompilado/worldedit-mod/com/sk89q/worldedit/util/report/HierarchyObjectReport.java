/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.util.report;

import com.sk89q.worldedit.util.report.ShallowObjectReport;

public class HierarchyObjectReport
extends ShallowObjectReport {
    public HierarchyObjectReport(String title, Object object) {
        super(title, object);
        Class<?> type = object.getClass();
        while ((type = type.getSuperclass()) != null) {
            this.scanClass(object, type);
        }
    }
}

