/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  java.lang.StackWalker
 *  java.lang.StackWalker$StackFrame
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package com.sk89q.worldedit.internal.util;

import java.util.Optional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LogManagerCompat {
    public static Logger getLogger() {
        return LogManager.getLogger((String)LogManagerCompat.getCallerCallerClassName());
    }

    private static String getCallerCallerClassName() {
        StackWalker.StackFrame caller = (StackWalker.StackFrame)((Optional)StackWalker.getInstance().walk(s -> s.skip(2L).findFirst())).orElseThrow(() -> new AssertionError((Object)"No caller found"));
        return caller.getClassName();
    }

    private LogManagerCompat() {
    }
}

