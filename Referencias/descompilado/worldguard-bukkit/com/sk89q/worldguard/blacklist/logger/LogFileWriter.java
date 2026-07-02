/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.sk89q.worldguard.blacklist.logger;

import java.io.BufferedWriter;
import javax.annotation.Nullable;

public class LogFileWriter
implements Comparable<LogFileWriter> {
    public String path;
    private BufferedWriter writer;
    private long lastUse;

    public LogFileWriter(String path, BufferedWriter writer) {
        this.path = path;
        this.writer = writer;
        this.lastUse = System.currentTimeMillis();
    }

    public String getPath() {
        return this.path;
    }

    public BufferedWriter getWriter() {
        return this.writer;
    }

    public long getLastUse() {
        return this.lastUse;
    }

    public void updateLastUse() {
        this.lastUse = System.currentTimeMillis();
    }

    @Override
    public int compareTo(@Nullable LogFileWriter other) {
        if (other == null) {
            return 1;
        }
        if (this.lastUse > other.lastUse) {
            return 1;
        }
        if (this.lastUse < other.lastUse) {
            return -1;
        }
        return 0;
    }
}

