/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldguard.blacklist.logger;

import com.sk89q.worldguard.blacklist.event.BlacklistEvent;

public interface LoggerHandler {
    public void logEvent(BlacklistEvent var1, String var2);

    public void close();
}

