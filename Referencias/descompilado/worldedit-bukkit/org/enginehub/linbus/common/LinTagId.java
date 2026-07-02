/*
 * Decompiled with CFR 0.152.
 */
package org.enginehub.linbus.common;

public enum LinTagId {
    END,
    BYTE,
    SHORT,
    INT,
    LONG,
    FLOAT,
    DOUBLE,
    BYTE_ARRAY,
    STRING,
    LIST,
    COMPOUND,
    INT_ARRAY,
    LONG_ARRAY;

    private final int id = this.ordinal();

    public static LinTagId fromId(int id) {
        if (id < 0 || id >= LinTagId.values().length) {
            throw new IllegalArgumentException("Invalid NBT ID: " + id);
        }
        return LinTagId.values()[id];
    }

    public int id() {
        return this.id;
    }

    public String toString() {
        return this.name() + "[id=" + this.id + "]";
    }
}

