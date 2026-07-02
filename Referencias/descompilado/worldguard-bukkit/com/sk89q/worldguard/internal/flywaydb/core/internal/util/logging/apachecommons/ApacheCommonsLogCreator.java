/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.LogFactory
 */
package com.sk89q.worldguard.internal.flywaydb.core.internal.util.logging.apachecommons;

import com.sk89q.worldguard.internal.flywaydb.core.internal.util.logging.Log;
import com.sk89q.worldguard.internal.flywaydb.core.internal.util.logging.LogCreator;
import com.sk89q.worldguard.internal.flywaydb.core.internal.util.logging.apachecommons.ApacheCommonsLog;
import org.apache.commons.logging.LogFactory;

public class ApacheCommonsLogCreator
implements LogCreator {
    @Override
    public Log createLogger(Class<?> clazz) {
        return new ApacheCommonsLog(LogFactory.getLog(clazz));
    }
}

