/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.math;

public final class BitMath {
    public static int mask(int bits) {
        return ~(-1 << bits);
    }

    public static int unpackX(long packed) {
        return BitMath.extractSigned(packed, 0, 26);
    }

    public static int unpackZ(long packed) {
        return BitMath.extractSigned(packed, 26, 26);
    }

    public static int unpackY(long packed) {
        return BitMath.extractSigned(packed, 52, 12);
    }

    public static int extractSigned(long i, int shift, int bits) {
        return BitMath.fixSign((int)(i >> shift) & BitMath.mask(bits), bits);
    }

    public static int fixSign(int i, int bits) {
        return i << 32 - bits >> 32 - bits;
    }

    private BitMath() {
    }
}

