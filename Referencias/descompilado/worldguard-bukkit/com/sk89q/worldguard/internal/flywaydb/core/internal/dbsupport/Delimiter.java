/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldguard.internal.flywaydb.core.internal.dbsupport;

public class Delimiter {
    private final String delimiter;
    private final boolean aloneOnLine;

    public Delimiter(String delimiter, boolean aloneOnLine) {
        this.delimiter = delimiter;
        this.aloneOnLine = aloneOnLine;
    }

    public String getDelimiter() {
        return this.delimiter;
    }

    public boolean isAloneOnLine() {
        return this.aloneOnLine;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        Delimiter delimiter1 = (Delimiter)o;
        return this.aloneOnLine == delimiter1.aloneOnLine && this.delimiter.equals(delimiter1.delimiter);
    }

    public int hashCode() {
        int result = this.delimiter.hashCode();
        result = 31 * result + (this.aloneOnLine ? 1 : 0);
        return result;
    }
}

