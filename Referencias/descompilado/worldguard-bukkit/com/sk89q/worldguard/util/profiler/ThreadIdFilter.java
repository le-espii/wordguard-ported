/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldguard.util.profiler;

import java.lang.management.ThreadInfo;
import java.util.function.Predicate;

public class ThreadIdFilter
implements Predicate<ThreadInfo> {
    private final long id;

    public ThreadIdFilter(long id) {
        this.id = id;
    }

    @Override
    public boolean test(ThreadInfo threadInfo) {
        return threadInfo.getThreadId() == this.id;
    }
}

