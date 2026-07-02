/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 */
package com.sk89q.worldedit.world.chunk;

import com.google.common.base.Preconditions;

public class PackedIntArrayReader {
    private static final int[] FACTORS = new int[64];
    private static final int SIZE = 4096;
    private final long[] data;
    private final int elementBits;
    private final long maxValue;
    private final int elementsPerLong;
    private final int factor;

    public PackedIntArrayReader(long[] data) {
        this.data = data;
        this.elementBits = data.length * 64 / 4096;
        this.maxValue = (1L << this.elementBits) - 1L;
        this.elementsPerLong = 64 / this.elementBits;
        this.factor = FACTORS[this.elementsPerLong - 1];
        int j = (4096 + this.elementsPerLong - 1) / this.elementsPerLong;
        if (j != data.length) {
            throw new IllegalStateException("Invalid packed-int array provided, should be of length " + j);
        }
    }

    public int get(int index) {
        Preconditions.checkElementIndex((int)index, (int)4096);
        int i = this.adjustIndex(index);
        long l = this.data[i];
        int j = (index - i * this.elementsPerLong) * this.elementBits;
        return (int)(l >> j & this.maxValue);
    }

    private int adjustIndex(int i) {
        return (int)((long)i * (long)this.factor + (long)this.factor >> 32);
    }

    static {
        PackedIntArrayReader.FACTORS[0] = -1;
        for (int i = 2; i <= 64; ++i) {
            PackedIntArrayReader.FACTORS[i - 1] = (int)(Integer.toUnsignedLong(-1) / (long)i);
        }
    }
}

