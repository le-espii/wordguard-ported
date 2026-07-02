/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 */
package com.sk89q.worldguard.util.profiler;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.sk89q.worldguard.util.profiler.StackTraceNode;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

public class StackNode
implements Comparable<StackNode> {
    private final String name;
    private final Map<String, StackNode> children = Maps.newHashMap();
    private long totalTime;

    public StackNode(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public Collection<StackNode> getChildren() {
        ArrayList list = Lists.newArrayList(this.children.values());
        Collections.sort(list);
        return list;
    }

    public StackNode getChild(String name) {
        StackNode child = this.children.get(name);
        if (child == null) {
            child = new StackNode(name);
            this.children.put(name, child);
        }
        return child;
    }

    public StackNode getChild(String className, String methodName) {
        StackTraceNode node = new StackTraceNode(className, methodName);
        StackNode child = this.children.get(node.getName());
        if (child == null) {
            child = node;
            this.children.put(node.getName(), node);
        }
        return child;
    }

    public long getTotalTime() {
        return this.totalTime;
    }

    public void log(long time) {
        this.totalTime += time;
    }

    private void log(StackTraceElement[] elements, int skip, long time) {
        this.log(time);
        if (elements.length - skip == 0) {
            return;
        }
        StackTraceElement bottom = elements[elements.length - (skip + 1)];
        this.getChild(bottom.getClassName(), bottom.getMethodName()).log(elements, skip + 1, time);
    }

    public void log(StackTraceElement[] elements, long time) {
        this.log(elements, 0, time);
    }

    @Override
    public int compareTo(StackNode o) {
        return this.getName().compareTo(o.getName());
    }

    void writeString(StringBuilder builder, int indent) {
        StringBuilder b = new StringBuilder();
        for (int i = 0; i < indent; ++i) {
            b.append(" ");
        }
        String padding = b.toString();
        for (StackNode child : this.getChildren()) {
            builder.append(padding).append(child.getName());
            builder.append(" ");
            builder.append(child.getTotalTime()).append("ms");
            builder.append("\n");
            child.writeString(builder, indent + 1);
        }
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        this.writeString(builder, 0);
        return builder.toString();
    }
}

