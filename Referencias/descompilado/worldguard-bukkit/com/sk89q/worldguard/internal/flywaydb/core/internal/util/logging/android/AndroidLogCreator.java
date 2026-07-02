/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldguard.internal.flywaydb.core.internal.util.logging.android;

import com.sk89q.worldguard.internal.flywaydb.core.internal.util.logging.Log;
import com.sk89q.worldguard.internal.flywaydb.core.internal.util.logging.LogCreator;
import com.sk89q.worldguard.internal.flywaydb.core.internal.util.logging.android.AndroidLog;

public class AndroidLogCreator
implements LogCreator {
    @Override
    public Log createLogger(Class<?> clazz) {
        return new AndroidLog();
    }
}

