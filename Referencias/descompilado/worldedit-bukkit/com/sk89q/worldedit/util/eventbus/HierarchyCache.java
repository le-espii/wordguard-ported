/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.cache.CacheBuilder
 *  com.google.common.cache.CacheLoader
 *  com.google.common.cache.LoadingCache
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Sets
 */
package com.sk89q.worldedit.util.eventbus;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

class HierarchyCache {
    private final LoadingCache<Class<?>, Set<Class<?>>> cache = CacheBuilder.newBuilder().weakKeys().build(CacheLoader.from(this::build));

    HierarchyCache() {
    }

    public Set<Class<?>> get(Class<?> concreteClass) {
        return (Set)this.cache.getUnchecked(concreteClass);
    }

    protected Set<Class<?>> build(Class<?> concreteClass) {
        LinkedList parents = Lists.newLinkedList();
        HashSet classes = Sets.newHashSet();
        parents.add(concreteClass);
        while (!parents.isEmpty()) {
            Class clazz = (Class)parents.remove(0);
            classes.add(clazz);
            Class parent = clazz.getSuperclass();
            if (parent != null) {
                parents.add(parent);
            }
            Collections.addAll(parents, clazz.getInterfaces());
        }
        return classes;
    }
}

