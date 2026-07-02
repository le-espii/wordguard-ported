/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldguard.util.collect;

import com.sk89q.worldguard.util.collect.EntryBase;
import com.sk89q.worldguard.util.collect.LongHash;
import java.util.ArrayList;
import java.util.Arrays;

public class LongBaseHashTable
extends LongHash {
    EntryBase[][][] values = new EntryBase[256][][];
    EntryBase cache = null;

    public void put(int msw, int lsw, EntryBase entry) {
        this.put(entry);
    }

    public EntryBase getEntry(int msw, int lsw) {
        return this.getEntry(LongBaseHashTable.toLong(msw, lsw));
    }

    public synchronized void put(EntryBase entry) {
        int outerIdx;
        EntryBase[] inner;
        int mainIdx = (int)(entry.key & 0xFFL);
        EntryBase[][] outer = this.values[mainIdx];
        if (outer == null) {
            outer = new EntryBase[256][];
            this.values[mainIdx] = outer;
        }
        if ((inner = outer[outerIdx = (int)(entry.key >> 32 & 0xFFL)]) == null) {
            inner = new EntryBase[5];
            outer[outerIdx] = inner;
            inner[0] = this.cache = entry;
        } else {
            int i;
            for (i = 0; i < inner.length; ++i) {
                if (inner[i] != null && inner[i].key != entry.key) continue;
                inner[i] = this.cache = entry;
                return;
            }
            inner = Arrays.copyOf(inner, i + i);
            outer[outerIdx] = inner;
            inner[i] = entry;
        }
    }

    public synchronized EntryBase getEntry(long key) {
        return this.containsKey(key) ? this.cache : null;
    }

    @Override
    public synchronized boolean containsKey(long key) {
        if (this.cache != null && this.cache.key == key) {
            return true;
        }
        int outerIdx = (int)(key >> 32 & 0xFFL);
        EntryBase[][] outer = this.values[(int)(key & 0xFFL)];
        if (outer == null) {
            return false;
        }
        EntryBase[] inner = outer[outerIdx];
        if (inner == null) {
            return false;
        }
        for (int i = 0; i < inner.length; ++i) {
            EntryBase e = inner[i];
            if (e == null) {
                return false;
            }
            if (e.key != key) continue;
            this.cache = e;
            return true;
        }
        return false;
    }

    @Override
    public synchronized void remove(long key) {
        EntryBase[][] outer = this.values[(int)(key & 0xFFL)];
        if (outer == null) {
            return;
        }
        EntryBase[] inner = outer[(int)(key >> 32 & 0xFFL)];
        if (inner == null) {
            return;
        }
        for (int i = 0; i < inner.length; ++i) {
            if (inner[i] == null || inner[i].key != key) continue;
            ++i;
            while (i < inner.length && inner[i] != null) {
                inner[i - 1] = inner[i];
                ++i;
            }
            inner[i - 1] = null;
            this.cache = null;
            return;
        }
    }

    public synchronized ArrayList<EntryBase> entries() {
        ArrayList<EntryBase> ret = new ArrayList<EntryBase>();
        for (EntryBase[][] outer : this.values) {
            if (outer == null) continue;
            for (EntryBase[] inner : outer) {
                if (inner == null) continue;
                for (EntryBase entry : inner) {
                    if (entry == null) break;
                    ret.add(entry);
                }
            }
        }
        return ret;
    }
}

