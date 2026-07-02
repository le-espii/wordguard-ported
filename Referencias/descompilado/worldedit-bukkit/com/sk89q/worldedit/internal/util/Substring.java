/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 */
package com.sk89q.worldedit.internal.util;

import com.google.common.base.Preconditions;
import java.util.Objects;

public final class Substring {
    private final String substring;
    private final int start;
    private final int end;

    public static Substring from(String original, int start) {
        return new Substring(original.substring(start), start, original.length());
    }

    public static Substring from(String original, int start, int end) {
        return new Substring(original.substring(start, end), start, end);
    }

    public static Substring wrap(String substring, int start, int end) {
        Preconditions.checkArgument((0 <= start ? 1 : 0) != 0, (Object)"Start must be greater than or equal to zero");
        Preconditions.checkArgument((start <= end ? 1 : 0) != 0, (Object)"End must be greater than or equal to start");
        return new Substring(substring, start, end);
    }

    private Substring(String substring, int start, int end) {
        this.substring = substring;
        this.start = start;
        this.end = end;
    }

    public String getSubstring() {
        return this.substring;
    }

    public int getStart() {
        return this.start;
    }

    public int getEnd() {
        return this.end;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        Substring substring1 = (Substring)o;
        return this.start == substring1.start && this.end == substring1.end && this.substring.equals(substring1.substring);
    }

    public int hashCode() {
        return Objects.hash(this.substring, this.start, this.end);
    }

    public String toString() {
        return "Substring{substring='" + this.substring + "',start=" + this.start + ",end=" + this.end + "}";
    }
}

