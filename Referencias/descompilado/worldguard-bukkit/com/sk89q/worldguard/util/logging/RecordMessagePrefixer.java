/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 */
package com.sk89q.worldguard.util.logging;

import com.google.common.base.Preconditions;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class RecordMessagePrefixer
extends Handler {
    private final Logger parentLogger;
    private final String prefix;

    public RecordMessagePrefixer(Logger parentLogger, String prefix) {
        Preconditions.checkNotNull((Object)parentLogger);
        Preconditions.checkNotNull((Object)prefix);
        this.parentLogger = parentLogger;
        this.prefix = prefix;
    }

    @Override
    public void publish(LogRecord record) {
        record.setMessage(this.prefix + record.getMessage());
        this.parentLogger.log(record);
    }

    @Override
    public void flush() {
    }

    @Override
    public void close() throws SecurityException {
    }

    public static void register(Logger logger, String prefix) {
        Preconditions.checkNotNull((Object)logger);
        String className = RecordMessagePrefixer.class.getCanonicalName();
        logger.setUseParentHandlers(false);
        for (Handler handler : logger.getHandlers()) {
            if (!handler.getClass().getCanonicalName().equals(className)) continue;
            logger.removeHandler(handler);
        }
        logger.addHandler(new RecordMessagePrefixer(logger.getParent(), prefix));
    }
}

