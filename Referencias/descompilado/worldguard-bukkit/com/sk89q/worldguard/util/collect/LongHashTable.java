/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldguard.util.collect;

import com.sk89q.worldguard.util.collect.EntryBase;
import com.sk89q.worldguard.util.collect.LongBaseHashTable;
import java.util.ArrayList;

public class LongHashTable<V>
extends LongBaseHashTable {
    public void put(int msw, int lsw, V value) {
        this.put(LongHashTable.toLong(msw, lsw), value);
    }

    public V get(int msw, int lsw) {
        return this.get(LongHashTable.toLong(msw, lsw));
    }

    public synchronized void put(long key, V value) {
        this.put(new Entry(this, key, value));
    }

    public synchronized V get(long key) {
        Entry entry = (Entry)this.getEntry(key);
        return entry != null ? (V)entry.value : null;
    }

    public synchronized ArrayList<V> values() {
        ArrayList ret = new ArrayList();
        ArrayList<EntryBase> entries = this.entries();
        for (EntryBase entry : entries) {
            ret.add(((Entry)entry).value);
        }
        return ret;
    }

    private class Entry
    extends EntryBase {
        V value;

        Entry(LongHashTable longHashTable, long k, V v) {
            super(k);
            this.value = v;
        }
    }
}

