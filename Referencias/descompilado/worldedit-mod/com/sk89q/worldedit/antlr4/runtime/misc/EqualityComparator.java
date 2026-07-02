/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.antlr4.runtime.misc;

public interface EqualityComparator<T> {
    public int hashCode(T var1);

    public boolean equals(T var1, T var2);
}

