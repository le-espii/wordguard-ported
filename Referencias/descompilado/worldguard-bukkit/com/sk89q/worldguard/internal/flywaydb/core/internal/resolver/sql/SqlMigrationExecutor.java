/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldguard.internal.flywaydb.core.internal.resolver.sql;

import com.sk89q.worldguard.internal.flywaydb.core.api.resolver.MigrationExecutor;
import com.sk89q.worldguard.internal.flywaydb.core.internal.dbsupport.DbSupport;
import com.sk89q.worldguard.internal.flywaydb.core.internal.dbsupport.JdbcTemplate;
import com.sk89q.worldguard.internal.flywaydb.core.internal.dbsupport.SqlScript;
import com.sk89q.worldguard.internal.flywaydb.core.internal.util.PlaceholderReplacer;
import com.sk89q.worldguard.internal.flywaydb.core.internal.util.scanner.Resource;
import java.sql.Connection;

public class SqlMigrationExecutor
implements MigrationExecutor {
    private final DbSupport dbSupport;
    private final PlaceholderReplacer placeholderReplacer;
    private final Resource sqlScriptResource;
    private final String encoding;

    public SqlMigrationExecutor(DbSupport dbSupport, Resource sqlScriptResource, PlaceholderReplacer placeholderReplacer, String encoding) {
        this.dbSupport = dbSupport;
        this.sqlScriptResource = sqlScriptResource;
        this.encoding = encoding;
        this.placeholderReplacer = placeholderReplacer;
    }

    @Override
    public void execute(Connection connection) {
        String sqlScriptSource = this.sqlScriptResource.loadAsString(this.encoding);
        String sqlScriptSourceNoPlaceholders = this.placeholderReplacer.replacePlaceholders(sqlScriptSource);
        SqlScript sqlScript = new SqlScript(sqlScriptSourceNoPlaceholders, this.dbSupport);
        sqlScript.execute(new JdbcTemplate(connection, 0));
    }

    @Override
    public boolean executeInTransaction() {
        return true;
    }
}

