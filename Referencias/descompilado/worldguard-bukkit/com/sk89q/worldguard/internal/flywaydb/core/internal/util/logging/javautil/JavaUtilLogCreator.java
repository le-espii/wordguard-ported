/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldguard.internal.flywaydb.core.internal.util.logging.javautil;

import com.sk89q.worldguard.internal.flywaydb.core.internal.util.logging.Log;
import com.sk89q.worldguard.internal.flywaydb.core.internal.util.logging.LogCreator;
import com.sk89q.worldguard.internal.flywaydb.core.internal.util.logging.javautil.JavaUtilLog;
import java.util.logging.Logger;

public class JavaUtilLogCreator
implements LogCreator {
    @Override
    public Log createLogger(Class<?> clazz) {
        return new JavaUtilLog(Logger.getLogger(clazz.getName()));
    }
}

