/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 */
package com.sk89q.worldguard.util.profiler;

import com.google.common.base.Preconditions;
import java.lang.management.ThreadInfo;
import java.util.function.Predicate;

public class ThreadNameFilter
implements Predicate<ThreadInfo> {
    private final String name;

    public ThreadNameFilter(String name) {
        Preconditions.checkNotNull((Object)name, (Object)"name");
        this.name = name;
    }

    @Override
    public boolean test(ThreadInfo threadInfo) {
        return threadInfo.getThreadName().equalsIgnoreCase(this.name);
    }
}

