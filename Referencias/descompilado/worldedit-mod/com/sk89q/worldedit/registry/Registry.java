/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  javax.annotation.Nullable
 */
package com.sk89q.worldedit.registry;

import com.google.common.base.Preconditions;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.registry.Keyed;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import javax.annotation.Nullable;

public class Registry<V extends Keyed>
implements Iterable<V> {
    private final Map<String, V> map = new HashMap<String, V>();
    private final String name;
    private final boolean checkInitialized;

    public Registry(String name) {
        this(name, false);
    }

    public Registry(String name, boolean checkInitialized) {
        this.name = name;
        this.checkInitialized = checkInitialized;
    }

    public String getName() {
        return this.name;
    }

    @Nullable
    public V get(String key) {
        Preconditions.checkState((boolean)key.equals(key.toLowerCase(Locale.ROOT)), (String)"key must be lowercase: %s", (Object)key);
        if (this.checkInitialized) {
            Preconditions.checkState((boolean)WorldEdit.getInstance().getPlatformManager().isInitialized(), (Object)"WorldEdit is not initialized yet.");
        }
        return (V)((Keyed)this.map.get(key));
    }

    public V register(String key, V value) {
        Objects.requireNonNull(key, "key");
        Objects.requireNonNull(value, "value");
        Preconditions.checkState((boolean)key.equals(key.toLowerCase(Locale.ROOT)), (String)"key must be lowercase: %s", (Object)key);
        Preconditions.checkState((!this.map.containsKey(key) ? 1 : 0) != 0, (String)"key '%s' already has an associated %s", (Object)key, (Object)this.name);
        this.map.put(key, value);
        return value;
    }

    public void clear() {
        this.map.clear();
    }

    public Set<String> keySet() {
        return Collections.unmodifiableSet(this.map.keySet());
    }

    public Collection<V> values() {
        return Collections.unmodifiableCollection(this.map.values());
    }

    @Override
    public Iterator<V> iterator() {
        return this.map.values().iterator();
    }
}

