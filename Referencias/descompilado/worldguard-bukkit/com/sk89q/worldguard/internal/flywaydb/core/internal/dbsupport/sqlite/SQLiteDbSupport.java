/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldguard.internal.flywaydb.core.internal.dbsupport.sqlite;

import com.sk89q.worldguard.internal.flywaydb.core.internal.dbsupport.DbSupport;
import com.sk89q.worldguard.internal.flywaydb.core.internal.dbsupport.JdbcTemplate;
import com.sk89q.worldguard.internal.flywaydb.core.internal.dbsupport.Schema;
import com.sk89q.worldguard.internal.flywaydb.core.internal.dbsupport.SqlStatementBuilder;
import com.sk89q.worldguard.internal.flywaydb.core.internal.dbsupport.sqlite.SQLiteSchema;
import com.sk89q.worldguard.internal.flywaydb.core.internal.dbsupport.sqlite.SQLiteSqlStatementBuilder;
import com.sk89q.worldguard.internal.flywaydb.core.internal.util.logging.Log;
import com.sk89q.worldguard.internal.flywaydb.core.internal.util.logging.LogFactory;
import java.sql.Connection;
import java.sql.SQLException;

public class SQLiteDbSupport
extends DbSupport {
    private static final Log LOG = LogFactory.getLog(SQLiteDbSupport.class);

    public SQLiteDbSupport(Connection connection) {
        super(new JdbcTemplate(connection, 12));
    }

    @Override
    public String getDbName() {
        return "sqlite";
    }

    @Override
    public String getCurrentUserFunction() {
        return "''";
    }

    @Override
    protected String doGetCurrentSchema() throws SQLException {
        return "main";
    }

    @Override
    protected void doSetCurrentSchema(Schema schema) throws SQLException {
        LOG.info("SQLite does not support setting the schema. Default schema NOT changed to " + schema);
    }

    @Override
    public boolean supportsDdlTransactions() {
        return true;
    }

    @Override
    public String getBooleanTrue() {
        return "1";
    }

    @Override
    public String getBooleanFalse() {
        return "0";
    }

    @Override
    public SqlStatementBuilder createSqlStatementBuilder() {
        return new SQLiteSqlStatementBuilder();
    }

    @Override
    public String doQuote(String identifier) {
        return "\"" + identifier + "\"";
    }

    @Override
    public Schema getSchema(String name) {
        return new SQLiteSchema(this.jdbcTemplate, this, name);
    }

    @Override
    public boolean catalogIsSchema() {
        return true;
    }
}

