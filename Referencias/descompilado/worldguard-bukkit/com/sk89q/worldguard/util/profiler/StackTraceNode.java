/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldguard.util.profiler;

import com.sk89q.worldguard.util.profiler.StackNode;

public class StackTraceNode
extends StackNode {
    private final String className;
    private final String methodName;

    public StackTraceNode(String className, String methodName) {
        super(className + "." + methodName + "()");
        this.className = className;
        this.methodName = methodName;
    }

    public String getClassName() {
        return this.className;
    }

    public String getMethodName() {
        return this.methodName;
    }

    @Override
    public int compareTo(StackNode o) {
        return Long.compare(o.getTotalTime(), this.getTotalTime());
    }
}

