/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldguard.internal.flywaydb.core.internal.util.logging;

import com.sk89q.worldguard.internal.flywaydb.core.internal.util.FeatureDetector;
import com.sk89q.worldguard.internal.flywaydb.core.internal.util.logging.Log;
import com.sk89q.worldguard.internal.flywaydb.core.internal.util.logging.LogCreator;
import com.sk89q.worldguard.internal.flywaydb.core.internal.util.logging.android.AndroidLogCreator;
import com.sk89q.worldguard.internal.flywaydb.core.internal.util.logging.apachecommons.ApacheCommonsLogCreator;
import com.sk89q.worldguard.internal.flywaydb.core.internal.util.logging.javautil.JavaUtilLogCreator;

public class LogFactory {
    private static LogCreator logCreator;

    private LogFactory() {
    }

    public static void setLogCreator(LogCreator logCreator) {
        LogFactory.logCreator = logCreator;
    }

    public static Log getLog(Class<?> clazz) {
        if (logCreator == null) {
            FeatureDetector featureDetector = new FeatureDetector(Thread.currentThread().getContextClassLoader());
            logCreator = featureDetector.isAndroidAvailable() ? new AndroidLogCreator() : (featureDetector.isApacheCommonsLoggingAvailable() ? new ApacheCommonsLogCreator() : new JavaUtilLogCreator());
        }
        return logCreator.createLogger(clazz);
    }
}

