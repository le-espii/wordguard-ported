/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldguard.blacklist.logger;

import com.sk89q.worldguard.blacklist.event.BlacklistEvent;
import com.sk89q.worldguard.blacklist.logger.LoggerHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConsoleHandler
implements LoggerHandler {
    private String worldName;
    private final Logger logger;

    public ConsoleHandler(String worldName, Logger logger) {
        this.worldName = worldName;
        this.logger = logger;
    }

    @Override
    public void logEvent(BlacklistEvent event, String comment) {
        this.logger.log(Level.INFO, "[" + this.worldName + "] " + event.getLoggerMessage() + (String)(comment != null ? " (" + comment + ")" : ""));
    }

    @Override
    public void close() {
    }
}

