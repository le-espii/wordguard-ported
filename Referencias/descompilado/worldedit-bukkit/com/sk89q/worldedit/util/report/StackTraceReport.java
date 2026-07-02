/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 */
package com.sk89q.worldedit.util.report;

import com.google.common.base.Preconditions;
import com.sk89q.worldedit.util.report.Report;

public class StackTraceReport
implements Report {
    private final StackTraceElement[] stackTrace;

    public StackTraceReport(StackTraceElement[] stackTrace) {
        Preconditions.checkNotNull((Object)stackTrace, (Object)"stackTrace");
        this.stackTrace = stackTrace;
    }

    @Override
    public String getTitle() {
        return "Stack Trace";
    }

    public String toString() {
        if (this.stackTrace.length > 0) {
            StringBuilder builder = new StringBuilder();
            boolean first = true;
            for (StackTraceElement element : this.stackTrace) {
                if (first) {
                    first = false;
                } else {
                    builder.append("\n");
                }
                builder.append(element.getClassName()).append(".").append(element.getMethodName()).append("() (").append(element.getFileName()).append(":").append(element.getLineNumber()).append(")");
            }
            return builder.toString();
        }
        return "No stack trace available.";
    }
}

