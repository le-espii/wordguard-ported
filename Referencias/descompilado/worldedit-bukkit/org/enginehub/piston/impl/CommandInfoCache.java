/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.cache.CacheBuilder
 *  com.google.common.cache.CacheLoader
 *  com.google.common.cache.LoadingCache
 */
package org.enginehub.piston.impl;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.enginehub.piston.Command;
import org.enginehub.piston.impl.CommandInfo;

class CommandInfoCache {
    private final LoadingCache<Command, CommandInfo> commandCache = CacheBuilder.newBuilder().softValues().build(CacheLoader.from(CommandInfo::from));

    CommandInfoCache() {
    }

    public CommandInfo getInfo(Command command) {
        return (CommandInfo)this.commandCache.getUnchecked((Object)command);
    }
}

