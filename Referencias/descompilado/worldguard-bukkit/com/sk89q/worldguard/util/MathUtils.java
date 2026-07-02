/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldguard.util;

public final class MathUtils {
    private MathUtils() {
    }

    private static void checkNoOverflow(boolean condition) {
        if (!condition) {
            throw new ArithmeticException("overflow");
        }
    }

    public static long checkedMultiply(long a, long b) {
        int leadingZeros = Long.numberOfLeadingZeros(a) + Long.numberOfLeadingZeros(a ^ 0xFFFFFFFFFFFFFFFFL) + Long.numberOfLeadingZeros(b) + Long.numberOfLeadingZeros(b ^ 0xFFFFFFFFFFFFFFFFL);
        if (leadingZeros > 65) {
            return a * b;
        }
        MathUtils.checkNoOverflow(leadingZeros >= 64);
        MathUtils.checkNoOverflow(a >= 0L | b != Long.MIN_VALUE);
        long result = a * b;
        MathUtils.checkNoOverflow(a == 0L || result / a == b);
        return result;
    }
}

