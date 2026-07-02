/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldguard.internal.flywaydb.core.internal.util;

public class StopWatch {
    private long start;
    private long stop;

    public void start() {
        this.start = System.currentTimeMillis();
    }

    public void stop() {
        this.stop = System.currentTimeMillis();
    }

    public long getTotalTimeMillis() {
        return this.stop - this.start;
    }
}

