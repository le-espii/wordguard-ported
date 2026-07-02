/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  java.lang.StackWalker
 *  java.lang.StackWalker$Option
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package org.enginehub.piston.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LogManagerCompat {
    private static final StackWalker WALKER = StackWalker.getInstance((StackWalker.Option)StackWalker.Option.RETAIN_CLASS_REFERENCE);

    public static Logger getLogger() {
        return LogManager.getLogger((Class)WALKER.getCallerClass());
    }

    private LogManagerCompat() {
    }
}

