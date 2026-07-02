/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 */
package com.sk89q.worldguard.internal.flywaydb.core.internal.util.logging.apachecommons;

import com.sk89q.worldguard.internal.flywaydb.core.internal.util.logging.Log;

public class ApacheCommonsLog
implements Log {
    private final org.apache.commons.logging.Log logger;

    public ApacheCommonsLog(org.apache.commons.logging.Log logger) {
        this.logger = logger;
    }

    @Override
    public void debug(String message) {
        this.logger.debug((Object)message);
    }

    @Override
    public void info(String message) {
        this.logger.info((Object)message);
    }

    @Override
    public void warn(String message) {
        this.logger.warn((Object)message);
    }

    @Override
    public void error(String message) {
        this.logger.error((Object)message);
    }

    @Override
    public void error(String message, Exception e) {
        this.logger.error((Object)message, (Throwable)e);
    }
}

