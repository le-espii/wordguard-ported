/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.bukkit.fastutil.longs;

import com.sk89q.worldedit.bukkit.fastutil.HashCommon;
import com.sk89q.worldedit.bukkit.fastutil.longs.AbstractLongCollection;
import com.sk89q.worldedit.bukkit.fastutil.longs.LongIterator;
import com.sk89q.worldedit.bukkit.fastutil.longs.LongSet;
import java.util.Set;

public abstract class AbstractLongSet
extends AbstractLongCollection
implements Cloneable,
LongSet {
    protected AbstractLongSet() {
    }

    @Override
    public abstract LongIterator iterator();

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof Set)) {
            return false;
        }
        Set s = (Set)o;
        if (s.size() != this.size()) {
            return false;
        }
        if (s instanceof LongSet) {
            return this.containsAll((LongSet)s);
        }
        return this.containsAll(s);
    }

    @Override
    public int hashCode() {
        int h = 0;
        int n = this.size();
        LongIterator i = this.iterator();
        while (n-- != 0) {
            long k = i.nextLong();
            h += HashCommon.long2int(k);
        }
        return h;
    }

    @Override
    public boolean remove(long k) {
        return super.rem(k);
    }

    @Override
    @Deprecated
    public boolean rem(long k) {
        return this.remove(k);
    }
}

