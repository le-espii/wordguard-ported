/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldguard.protection.managers.storage.sql;

import com.sk89q.worldguard.protection.managers.storage.sql.TableCache;
import com.sk89q.worldguard.util.sql.DataSourceConfig;
import java.sql.Connection;

class DomainTableCache {
    private final TableCache.UserNameCache userNameCache;
    private final TableCache.UserUuidCache userUuidCache;
    private final TableCache.GroupNameCache groupNameCache;

    DomainTableCache(DataSourceConfig config, Connection conn) {
        this.userNameCache = new TableCache.UserNameCache(config, conn);
        this.userUuidCache = new TableCache.UserUuidCache(config, conn);
        this.groupNameCache = new TableCache.GroupNameCache(config, conn);
    }

    public TableCache.UserNameCache getUserNameCache() {
        return this.userNameCache;
    }

    public TableCache.UserUuidCache getUserUuidCache() {
        return this.userUuidCache;
    }

    public TableCache.GroupNameCache getGroupNameCache() {
        return this.groupNameCache;
    }
}

