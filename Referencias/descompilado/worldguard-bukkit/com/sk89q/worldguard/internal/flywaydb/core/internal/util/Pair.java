/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldguard.internal.flywaydb.core.internal.util;

public class Pair<L, R> {
    private L left;
    private R right;

    public static <L, R> Pair<L, R> of(L left, R right) {
        Pair<L, R> pair = new Pair<L, R>();
        pair.left = left;
        pair.right = right;
        return pair;
    }

    public L getLeft() {
        return this.left;
    }

    public R getRight() {
        return this.right;
    }
}

