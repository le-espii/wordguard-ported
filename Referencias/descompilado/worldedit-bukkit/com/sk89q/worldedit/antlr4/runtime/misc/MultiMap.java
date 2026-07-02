/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.antlr4.runtime.misc;

import com.sk89q.worldedit.antlr4.runtime.misc.Pair;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class MultiMap<K, V>
extends LinkedHashMap<K, List<V>> {
    public void map(K key, V value) {
        ArrayList<V> elementsForKey = (ArrayList<V>)this.get(key);
        if (elementsForKey == null) {
            elementsForKey = new ArrayList<V>();
            super.put(key, elementsForKey);
        }
        elementsForKey.add(value);
    }

    public List<Pair<K, V>> getPairs() {
        ArrayList<Pair<K, V>> pairs = new ArrayList<Pair<K, V>>();
        for (Object key : this.keySet()) {
            for (Object value : (List)this.get(key)) {
                pairs.add(new Pair(key, value));
            }
        }
        return pairs;
    }
}

