/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldguard.util.collect;

public abstract class LongHash {
    public static long toLong(int msw, int lsw) {
        return ((long)msw << 32) + (long)lsw - Integer.MIN_VALUE;
    }

    public static int msw(long l) {
        return (int)(l >> 32);
    }

    public static int lsw(long l) {
        return (int)(l & 0xFFFFFFFFFFFFFFFFL) + Integer.MIN_VALUE;
    }

    public boolean containsKey(int msw, int lsw) {
        return this.containsKey(LongHash.toLong(msw, lsw));
    }

    public void remove(int msw, int lsw) {
        this.remove(LongHash.toLong(msw, lsw));
    }

    public abstract boolean containsKey(long var1);

    public abstract void remove(long var1);
}

