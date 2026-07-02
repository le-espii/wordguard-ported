/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  android.util.Log
 */
package com.sk89q.worldguard.internal.flywaydb.core.internal.util.logging.android;

import com.sk89q.worldguard.internal.flywaydb.core.internal.util.logging.Log;

public class AndroidLog
implements Log {
    private static final String TAG = "Flyway";

    @Override
    public void debug(String message) {
        android.util.Log.d((String)TAG, (String)message);
    }

    @Override
    public void info(String message) {
        android.util.Log.i((String)TAG, (String)message);
    }

    @Override
    public void warn(String message) {
        android.util.Log.w((String)TAG, (String)message);
    }

    @Override
    public void error(String message) {
        android.util.Log.e((String)TAG, (String)message);
    }

    @Override
    public void error(String message, Exception e) {
        android.util.Log.e((String)TAG, (String)message, (Throwable)e);
    }
}

