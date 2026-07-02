/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Sets
 */
package com.sk89q.worldedit.util.report;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.sk89q.worldedit.util.report.Report;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DataReport
implements Report {
    private final String title;
    private final List<Line> lines = Lists.newArrayList();

    public DataReport(String title) {
        Preconditions.checkNotNull((Object)title, (Object)"title");
        this.title = title;
    }

    public void append(String key, String message) {
        Preconditions.checkNotNull((Object)key, (Object)"key");
        this.lines.add(new Line(key, message));
    }

    public void append(String key, String message, Object ... values) {
        Preconditions.checkNotNull((Object)message, (Object)"values");
        Preconditions.checkNotNull((Object)values, (Object)"values");
        this.append(key, String.format(message, values));
    }

    public void append(String key, byte value) {
        this.append(key, String.valueOf(value));
    }

    public void append(String key, short value) {
        this.append(key, String.valueOf(value));
    }

    public void append(String key, int value) {
        this.append(key, String.valueOf(value));
    }

    public void append(String key, long value) {
        this.append(key, String.valueOf(value));
    }

    public void append(String key, float value) {
        this.append(key, String.valueOf(value));
    }

    public void append(String key, double value) {
        this.append(key, String.valueOf(value));
    }

    public void append(String key, boolean value) {
        this.append(key, String.valueOf(value));
    }

    public void append(String key, char value) {
        this.append(key, String.valueOf(value));
    }

    public void append(String key, Object value) {
        this.append(key, DataReport.getStringValue(value, Sets.newHashSet()));
    }

    private static String getStringValue(Object value, Set<Object> seen) {
        if (seen.contains(value)) {
            return "<Recursive>";
        }
        seen.add(value);
        if (value instanceof Object[]) {
            value = Arrays.asList(value);
        }
        if (value instanceof Collection) {
            StringBuilder builder = new StringBuilder();
            boolean first = true;
            for (Object entry : (Collection)value) {
                if (first) {
                    first = false;
                } else {
                    builder.append("\n");
                }
                builder.append(DataReport.getStringValue(entry, Sets.newHashSet(seen)));
            }
            return builder.toString();
        }
        if (value instanceof Map) {
            StringBuilder builder = new StringBuilder();
            boolean first = true;
            for (Map.Entry entry : ((Map)((Object)value)).entrySet()) {
                if (first) {
                    first = false;
                } else {
                    builder.append("\n");
                }
                Object key = DataReport.getStringValue(entry.getKey(), Sets.newHashSet(seen)).replaceAll("[\r\n]", "");
                if (((String)key).length() > 60) {
                    key = ((String)key).substring(0, 60) + "...";
                }
                builder.append((String)key).append(": ").append(DataReport.getStringValue(entry.getValue(), Sets.newHashSet(seen)));
            }
            return builder.toString();
        }
        return String.valueOf(value);
    }

    @Override
    public String getTitle() {
        return this.title;
    }

    public String toString() {
        if (!this.lines.isEmpty()) {
            StringBuilder builder = new StringBuilder();
            boolean first = true;
            for (Line line : this.lines) {
                if (first) {
                    first = false;
                } else {
                    builder.append("\n");
                }
                builder.append(line.key).append(": ");
                if (line.value == null) {
                    builder.append("null");
                    continue;
                }
                if (line.value.contains("\n")) {
                    builder.append("\n");
                    builder.append(line.value.replaceAll("(?m)^", "\t"));
                    continue;
                }
                builder.append(line.value);
            }
            return builder.toString();
        }
        return "No data.";
    }

    private record Line(String key, String value) {
    }
}

