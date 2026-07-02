/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.Lists
 *  javax.annotation.Nullable
 */
package com.sk89q.worldguard.protection.managers.storage.sql;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.sk89q.worldguard.internal.util.sql.StatementUtils;
import com.sk89q.worldguard.util.io.Closer;
import com.sk89q.worldguard.util.sql.DataSourceConfig;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;
import javax.annotation.Nullable;

abstract class TableCache<V> {
    private static final Logger log = Logger.getLogger(TableCache.class.getCanonicalName());
    private static final int MAX_NUMBER_PER_QUERY = 100;
    private static final Object LOCK = new Object();
    private final Map<V, Integer> cache = new HashMap<V, Integer>();
    private final DataSourceConfig config;
    private final Connection conn;
    private final String tableName;
    private final String fieldName;

    protected TableCache(DataSourceConfig config, Connection conn, String tableName, String fieldName) {
        this.config = config;
        this.conn = conn;
        this.tableName = tableName;
        this.fieldName = fieldName;
    }

    protected abstract String fromType(V var1);

    protected abstract V toType(String var1);

    protected abstract V toKey(V var1);

    @Nullable
    public Integer find(V object) {
        return this.cache.get(object);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void fetch(Collection<V> entries) throws SQLException {
        Object object = LOCK;
        synchronized (object) {
            Preconditions.checkNotNull(entries);
            ArrayList<V> fetchList = new ArrayList<V>();
            for (V entry : entries) {
                if (this.cache.containsKey(this.toKey(entry))) continue;
                fetchList.add(entry);
            }
            if (fetchList.isEmpty()) {
                return;
            }
            for (Object partition : Lists.partition(fetchList, (int)100)) {
                Closer closer = Closer.create();
                try {
                    PreparedStatement statement = closer.register(this.conn.prepareStatement(String.format("SELECT id, " + this.fieldName + " FROM `" + this.config.getTablePrefix() + this.tableName + "` WHERE " + this.fieldName + " IN (%s)", StatementUtils.preparePlaceHolders(partition.size()))));
                    String[] values = new String[partition.size()];
                    int i = 0;
                    Iterator iterator = partition.iterator();
                    while (iterator.hasNext()) {
                        Object entry = iterator.next();
                        values[i] = this.fromType(entry);
                        ++i;
                    }
                    StatementUtils.setValues(statement, values);
                    ResultSet results = closer.register(statement.executeQuery());
                    while (results.next()) {
                        this.cache.put((Integer)this.toKey(this.toType(results.getString(this.fieldName))), results.getInt("id"));
                    }
                }
                finally {
                    closer.closeQuietly();
                }
            }
            ArrayList missing = new ArrayList();
            for (Object entry : fetchList) {
                if (this.cache.containsKey(this.toKey(entry))) continue;
                missing.add(entry);
            }
            if (!missing.isEmpty()) {
                Closer closer = Closer.create();
                try {
                    PreparedStatement statement = closer.register(this.conn.prepareStatement("INSERT INTO `" + this.config.getTablePrefix() + this.tableName + "` (id, " + this.fieldName + ") VALUES (null, ?)", 1));
                    for (Object entry : missing) {
                        statement.setString(1, this.fromType(entry));
                        statement.execute();
                        ResultSet generatedKeys = statement.getGeneratedKeys();
                        if (generatedKeys.next()) {
                            this.cache.put((Integer)this.toKey(entry), generatedKeys.getInt(1));
                            continue;
                        }
                        log.warning("Could not get the database ID for entry " + String.valueOf(entry));
                    }
                }
                finally {
                    closer.closeQuietly();
                }
            }
        }
    }

    static class GroupNameCache
    extends TableCache<String> {
        protected GroupNameCache(DataSourceConfig config, Connection conn) {
            super(config, conn, "group", "name");
        }

        @Override
        protected String fromType(String o) {
            return o;
        }

        @Override
        protected String toType(String o) {
            return o;
        }

        @Override
        protected String toKey(String object) {
            return object.toLowerCase();
        }
    }

    static class UserUuidCache
    extends TableCache<UUID> {
        protected UserUuidCache(DataSourceConfig config, Connection conn) {
            super(config, conn, "user", "uuid");
        }

        @Override
        protected String fromType(UUID o) {
            return o.toString();
        }

        @Override
        protected UUID toType(String o) {
            return UUID.fromString(o);
        }

        @Override
        protected UUID toKey(UUID object) {
            return object;
        }
    }

    static class UserNameCache
    extends TableCache<String> {
        protected UserNameCache(DataSourceConfig config, Connection conn) {
            super(config, conn, "user", "name");
        }

        @Override
        protected String fromType(String o) {
            return o;
        }

        @Override
        protected String toType(String o) {
            return o;
        }

        @Override
        protected String toKey(String object) {
            return object.toLowerCase();
        }
    }
}

