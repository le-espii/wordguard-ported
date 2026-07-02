/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldguard.blacklist;

import com.sk89q.worldguard.blacklist.event.BlacklistEvent;
import com.sk89q.worldguard.blacklist.logger.LoggerHandler;
import java.util.HashSet;
import java.util.Set;

public class BlacklistLoggerHandler
implements LoggerHandler {
    private Set<LoggerHandler> handlers = new HashSet<LoggerHandler>();

    public void addHandler(LoggerHandler handler) {
        this.handlers.add(handler);
    }

    public void removeHandler(LoggerHandler handler) {
        this.handlers.remove(handler);
    }

    public void clearHandlers() {
        this.handlers.clear();
    }

    @Override
    public void logEvent(BlacklistEvent event, String comment) {
        for (LoggerHandler handler : this.handlers) {
            handler.logEvent(event, comment);
        }
    }

    @Override
    public void close() {
        for (LoggerHandler handler : this.handlers) {
            handler.close();
        }
    }
}

