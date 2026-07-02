/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldguard.internal.flywaydb.core.internal.util.logging.javautil;

import com.sk89q.worldguard.internal.flywaydb.core.internal.util.logging.Log;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class JavaUtilLog
implements Log {
    private final Logger logger;

    public JavaUtilLog(Logger logger) {
        this.logger = logger;
    }

    @Override
    public void debug(String message) {
        this.log(Level.FINE, message, null);
    }

    @Override
    public void info(String message) {
        this.log(Level.INFO, message, null);
    }

    @Override
    public void warn(String message) {
        this.log(Level.WARNING, message, null);
    }

    @Override
    public void error(String message) {
        this.log(Level.SEVERE, message, null);
    }

    @Override
    public void error(String message, Exception e) {
        this.log(Level.SEVERE, message, e);
    }

    private void log(Level level, String message, Exception e) {
        LogRecord record = new LogRecord(level, message);
        record.setLoggerName(this.logger.getName());
        record.setThrown(e);
        record.setSourceClassName(this.logger.getName());
        record.setSourceMethodName(this.getMethodName());
        this.logger.log(record);
    }

    private String getMethodName() {
        StackTraceElement[] steArray;
        for (StackTraceElement stackTraceElement : steArray = new Throwable().getStackTrace()) {
            if (!this.logger.getName().equals(stackTraceElement.getClassName())) continue;
            return stackTraceElement.getMethodName();
        }
        return null;
    }
}

