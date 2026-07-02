/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.internal.util;

import java.util.NoSuchElementException;
import java.util.PrimitiveIterator;

public class VarIntIterator
implements PrimitiveIterator.OfInt {
    private final byte[] source;
    private int index;
    private boolean hasNextInt;
    private int nextInt;

    public VarIntIterator(byte[] source) {
        this.source = source;
    }

    @Override
    public boolean hasNext() {
        if (this.hasNextInt) {
            return true;
        }
        if (this.index >= this.source.length) {
            return false;
        }
        this.nextInt = this.readNextInt();
        this.hasNextInt = true;
        return true;
    }

    private int readNextInt() {
        int value = 0;
        int bitsRead = 0;
        while (true) {
            if (this.index >= this.source.length) {
                throw new IllegalStateException("Ran out of bytes while reading VarInt (probably corrupted data)");
            }
            byte next = this.source[this.index];
            ++this.index;
            value |= (next & 0x7F) << bitsRead;
            if (bitsRead > 35) {
                throw new IllegalStateException("VarInt too big (probably corrupted data)");
            }
            if ((next & 0x80) == 0) break;
            bitsRead += 7;
        }
        return value;
    }

    @Override
    public int nextInt() {
        if (!this.hasNext()) {
            throw new NoSuchElementException();
        }
        this.hasNextInt = false;
        return this.nextInt;
    }
}

