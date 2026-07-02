/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.cache.CacheBuilder
 *  com.google.common.cache.CacheLoader
 *  com.google.common.cache.LoadingCache
 *  javax.annotation.Nullable
 *  org.bukkit.event.Cancellable
 *  org.bukkit.event.Event
 */
package com.sk89q.worldguard.bukkit.listener.debounce;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.sk89q.worldguard.bukkit.util.Events;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nullable;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;

public class EventDebounce<K> {
    private final LoadingCache<K, Entry> cache;

    public EventDebounce(int debounceTime) {
        this.cache = CacheBuilder.newBuilder().maximumSize(1000L).expireAfterWrite((long)debounceTime, TimeUnit.MILLISECONDS).concurrencyLevel(2).build(new CacheLoader<K, Entry>(this){

            public Entry load(K key) throws Exception {
                return new Entry();
            }
        });
    }

    public <T extends Event> void fireToCancel(Cancellable originalEvent, T firedEvent, K key) {
        Entry entry = (Entry)this.cache.getUnchecked(key);
        if (entry.cancelled != null) {
            if (entry.cancelled.booleanValue()) {
                originalEvent.setCancelled(true);
            }
        } else {
            boolean cancelled = Events.fireAndTestCancel(firedEvent);
            if (cancelled) {
                originalEvent.setCancelled(true);
            }
            entry.cancelled = cancelled;
        }
    }

    @Nullable
    public <T extends Event> Entry getIfNotPresent(K key, Cancellable originalEvent) {
        Entry entry = (Entry)this.cache.getUnchecked(key);
        if (entry.cancelled != null) {
            if (entry.cancelled.booleanValue()) {
                originalEvent.setCancelled(true);
            }
            return null;
        }
        return entry;
    }

    public static <K> EventDebounce<K> create(int debounceTime) {
        return new EventDebounce<K>(debounceTime);
    }

    public static class Entry {
        private Boolean cancelled;

        public void setCancelled(boolean cancelled) {
            this.cancelled = cancelled;
        }
    }
}

