/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.HashMultimap
 *  com.google.common.collect.Multimap
 *  com.google.common.collect.SetMultimap
 *  org.apache.logging.log4j.Logger
 */
package com.sk89q.worldedit.util.eventbus;

import com.google.common.base.Preconditions;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.SetMultimap;
import com.sk89q.worldedit.internal.util.LogManagerCompat;
import com.sk89q.worldedit.util.eventbus.AnnotatedSubscriberFinder;
import com.sk89q.worldedit.util.eventbus.EventHandler;
import com.sk89q.worldedit.util.eventbus.HierarchyCache;
import com.sk89q.worldedit.util.eventbus.SubscriberFindingStrategy;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.apache.logging.log4j.Logger;

public final class EventBus {
    private static final Logger LOGGER = LogManagerCompat.getLogger();
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private final SetMultimap<Class<?>, EventHandler> handlersByType = HashMultimap.create();
    private final SubscriberFindingStrategy finder = new AnnotatedSubscriberFinder();
    private final HierarchyCache flattenHierarchyCache = new HierarchyCache();

    public void subscribe(Class<?> clazz, EventHandler handler) {
        Preconditions.checkNotNull(clazz);
        Preconditions.checkNotNull((Object)handler);
        this.lock.writeLock().lock();
        try {
            this.handlersByType.put(clazz, (Object)handler);
        }
        finally {
            this.lock.writeLock().unlock();
        }
    }

    public void subscribeAll(Multimap<Class<?>, EventHandler> handlers) {
        Preconditions.checkNotNull(handlers);
        this.lock.writeLock().lock();
        try {
            this.handlersByType.putAll(handlers);
        }
        finally {
            this.lock.writeLock().unlock();
        }
    }

    public void unsubscribe(Class<?> clazz, EventHandler handler) {
        Preconditions.checkNotNull(clazz);
        Preconditions.checkNotNull((Object)handler);
        this.lock.writeLock().lock();
        try {
            this.handlersByType.remove(clazz, (Object)handler);
        }
        finally {
            this.lock.writeLock().unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void unsubscribeAll(Multimap<Class<?>, EventHandler> handlers) {
        Preconditions.checkNotNull(handlers);
        this.lock.writeLock().lock();
        try {
            for (Map.Entry entry : handlers.asMap().entrySet()) {
                this.handlersByType.get((Object)((Class)entry.getKey())).removeAll((Collection)entry.getValue());
            }
        }
        finally {
            this.lock.writeLock().unlock();
        }
    }

    public void register(Object object) {
        this.subscribeAll(this.finder.findAllSubscribers(object));
    }

    public void unregister(Object object) {
        this.unsubscribeAll(this.finder.findAllSubscribers(object));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void post(Object event) {
        ArrayList dispatching = new ArrayList();
        Set<Class<?>> dispatchTypes = this.flattenHierarchyCache.get(event.getClass());
        this.lock.readLock().lock();
        try {
            for (Class<?> eventType : dispatchTypes) {
                Set wrappers = this.handlersByType.get(eventType);
                if (wrappers == null || wrappers.isEmpty()) continue;
                dispatching.addAll(wrappers);
            }
        }
        finally {
            this.lock.readLock().unlock();
        }
        Collections.sort(dispatching);
        for (EventHandler handler : dispatching) {
            this.dispatch(event, handler);
        }
    }

    private void dispatch(Object event, EventHandler handler) {
        try {
            handler.handleEvent(event);
        }
        catch (InvocationTargetException e) {
            LOGGER.error("Could not dispatch event: " + String.valueOf(event) + " to handler " + String.valueOf(handler), (Throwable)e);
        }
    }
}

