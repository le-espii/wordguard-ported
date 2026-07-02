/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.Iterators
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  javax.annotation.Nullable
 */
package com.sk89q.worldguard.protection.flags.registry;

import com.google.common.base.Preconditions;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.registry.FlagConflictException;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import com.sk89q.worldguard.protection.flags.registry.UnknownFlag;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Nullable;

public class SimpleFlagRegistry
implements FlagRegistry {
    private static final Logger log = Logger.getLogger(SimpleFlagRegistry.class.getCanonicalName());
    private final Object lock = new Object();
    private final ConcurrentMap<String, Flag<?>> flags = Maps.newConcurrentMap();
    private boolean initialized = false;

    public boolean isInitialized() {
        return this.initialized;
    }

    public void setInitialized(boolean initialized) {
        this.initialized = initialized;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void register(Flag<?> flag) throws FlagConflictException {
        Object object = this.lock;
        synchronized (object) {
            if (this.initialized) {
                throw new IllegalStateException("New flags cannot be registered at this time");
            }
            this.forceRegister(flag);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void registerAll(Collection<Flag<?>> flags) {
        Object object = this.lock;
        synchronized (object) {
            for (Flag<?> flag : flags) {
                try {
                    this.register(flag);
                }
                catch (FlagConflictException e) {
                    log.log(Level.WARNING, e.getMessage());
                }
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private Flag<?> forceRegister(Flag<?> flag) throws FlagConflictException {
        Preconditions.checkNotNull(flag, (Object)"flag");
        Preconditions.checkNotNull((Object)flag.getName(), (Object)"flag.getName()");
        Object object = this.lock;
        synchronized (object) {
            String name = flag.getName().toLowerCase();
            if (this.flags.containsKey(name)) {
                throw new FlagConflictException("A flag already exists by the name " + name);
            }
            this.flags.put(name, flag);
        }
        return flag;
    }

    @Override
    @Nullable
    public Flag<?> get(String name) {
        Preconditions.checkNotNull((Object)name, (Object)"name");
        return (Flag)this.flags.get(name.toLowerCase());
    }

    @Override
    public List<Flag<?>> getAll() {
        return Lists.newArrayList(this.flags.values());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private Flag<?> getOrCreate(String name) {
        Flag<?> flag = this.get(name);
        if (flag != null) {
            return flag;
        }
        Object object = this.lock;
        synchronized (object) {
            flag = this.get(name);
            return flag != null ? flag : this.forceRegister(new UnknownFlag(name));
        }
    }

    @Override
    public Map<Flag<?>, Object> unmarshal(Map<String, Object> rawValues, boolean createUnknown) {
        Preconditions.checkNotNull(rawValues, (Object)"rawValues");
        Flags.registerAll();
        ConcurrentMap values = Maps.newConcurrentMap();
        ConcurrentMap regionFlags = Maps.newConcurrentMap();
        for (Map.Entry<String, Object> entry : rawValues.entrySet()) {
            if (entry.getKey().endsWith("-group")) {
                regionFlags.put(entry.getKey(), entry.getValue());
                continue;
            }
            Flag<?> flag = createUnknown ? this.getOrCreate(entry.getKey()) : this.get(entry.getKey());
            if (flag == null) continue;
            try {
                Object unmarshalled = flag.unmarshal(entry.getValue());
                if (unmarshalled != null) {
                    values.put(flag, unmarshalled);
                    continue;
                }
                log.warning("Failed to parse flag '" + flag.getName() + "' with value '" + String.valueOf(entry.getValue()) + "'");
            }
            catch (Throwable e) {
                log.log(Level.WARNING, "Failed to unmarshal flag value for " + String.valueOf(flag), e);
            }
        }
        for (Map.Entry<String, Object> entry : regionFlags.entrySet()) {
            String parentName = entry.getKey().replaceAll("-group", "");
            Flag<?> parent = this.get(parentName);
            if (parent == null || parent instanceof UnknownFlag) {
                Flag<?> unk;
                if (createUnknown && this.get(entry.getKey()) == null) {
                    UnknownFlag unknownFlag = new UnknownFlag(entry.getKey());
                    this.forceRegister(unknownFlag);
                }
                if ((unk = this.get(entry.getKey())) == null) continue;
                values.put(unk, entry.getValue());
                continue;
            }
            values.put(parent.getRegionGroupFlag(), parent.getRegionGroupFlag().unmarshal(entry.getValue()));
        }
        return values;
    }

    @Override
    public int size() {
        return this.flags.size();
    }

    @Override
    public Iterator<Flag<?>> iterator() {
        return Iterators.unmodifiableIterator(this.flags.values().iterator());
    }
}

