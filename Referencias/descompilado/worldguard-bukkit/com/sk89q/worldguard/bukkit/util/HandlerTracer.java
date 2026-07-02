/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  javax.annotation.Nullable
 *  org.bukkit.event.Event
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.plugin.RegisteredListener
 */
package com.sk89q.worldguard.bukkit.util;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import org.bukkit.event.Event;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredListener;

public class HandlerTracer {
    private final List<Handler> handlers;

    public HandlerTracer(Event event) {
        this.handlers = HandlerTracer.getHandlers(event);
    }

    @Nullable
    public Plugin detectPlugin(StackTraceElement[] elements) {
        for (int i = elements.length - 1; i >= 0; --i) {
            StackTraceElement element = elements[i];
            for (Handler handler : this.handlers) {
                if (!element.getClassName().equals(handler.className)) continue;
                return handler.plugin;
            }
        }
        return null;
    }

    private static List<Handler> getHandlers(Event event) {
        ArrayList handlers = Lists.newArrayList();
        for (RegisteredListener listener : event.getHandlers().getRegisteredListeners()) {
            handlers.add(new Handler(listener.getListener().getClass().getName(), listener.getPlugin()));
        }
        return handlers;
    }

    private static class Handler {
        private final String className;
        private final Plugin plugin;

        private Handler(String className, Plugin plugin) {
            this.className = className;
            this.plugin = plugin;
        }
    }
}

