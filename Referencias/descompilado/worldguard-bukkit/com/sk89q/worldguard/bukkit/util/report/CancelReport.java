/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.sk89q.worldedit.util.report.Report
 *  com.sk89q.worldedit.util.report.StackTraceReport
 *  org.bukkit.event.Cancellable
 *  org.bukkit.event.Event
 *  org.bukkit.plugin.Plugin
 */
package com.sk89q.worldguard.bukkit.util.report;

import com.google.common.base.Preconditions;
import com.sk89q.worldedit.util.report.Report;
import com.sk89q.worldedit.util.report.StackTraceReport;
import com.sk89q.worldguard.bukkit.event.debug.CancelAttempt;
import com.sk89q.worldguard.bukkit.util.HandlerTracer;
import java.util.Arrays;
import java.util.List;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.plugin.Plugin;

public class CancelReport
implements Report {
    private final Event event;
    private final Cancellable cancellable;
    private final List<CancelAttempt> cancels;
    private final HandlerTracer tracer;
    private final int stackTruncateLength;
    private boolean detectingPlugin = true;

    public <T extends Event> CancelReport(T event, List<CancelAttempt> cancels, int stackTruncateLength) {
        Preconditions.checkNotNull(event, (Object)"event");
        Preconditions.checkNotNull(cancels, (Object)"cancels");
        this.event = event;
        this.cancellable = (Cancellable)event;
        this.cancels = cancels;
        this.tracer = new HandlerTracer(event);
        this.stackTruncateLength = stackTruncateLength;
    }

    public boolean isDetectingPlugin() {
        return this.detectingPlugin;
    }

    public void setDetectingPlugin(boolean detectingPlugin) {
        this.detectingPlugin = detectingPlugin;
    }

    private StackTraceElement[] truncateStackTrace(StackTraceElement[] elements) {
        int newLength = elements.length - this.stackTruncateLength;
        if (newLength <= 0) {
            return new StackTraceElement[0];
        }
        return Arrays.copyOf(elements, newLength);
    }

    public String getTitle() {
        return null;
    }

    public String toString() {
        if (!this.cancels.isEmpty()) {
            StringBuilder builder = new StringBuilder();
            builder.append("Was the action blocked? ").append(this.cancellable.isCancelled() ? "YES" : "NO").append("\n");
            if (this.cancels.size() != 1) {
                builder.append("Entry #1 had the last word.\n");
            }
            for (int i = this.cancels.size() - 1; i >= 0; --i) {
                CancelAttempt cancel = this.cancels.get(i);
                int index = this.cancels.size() - i;
                StackTraceElement[] stackTrace = this.truncateStackTrace(cancel.getStackTrace());
                Plugin cause = this.tracer.detectPlugin(stackTrace);
                builder.append("#").append(index).append(" ");
                builder.append(CancelReport.getCancelText(cancel.getAfter()));
                builder.append(" by ");
                if (this.detectingPlugin && cause != null) {
                    builder.append(cause.getName());
                } else {
                    builder.append(" (NOT KNOWN - use the stack trace below)");
                    builder.append("\n");
                    builder.append(new StackTraceReport(stackTrace).toString().replaceAll("(?m)^", "\t"));
                }
                builder.append("\n");
            }
            return builder.toString();
        }
        return "No plugins cancelled the event. Other causes for cancellation: (1) Bukkit may be using a different event for the action  (example: buckets have their own bucket events); or (2) Minecraft's spawn protection has not been disabled.";
    }

    private static String getCancelText(boolean flag) {
        return flag ? "BLOCKED" : "ALLOWED";
    }
}

