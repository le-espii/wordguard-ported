/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldguard.internal.flywaydb.core.internal.dbsupport.sqlserver;

import com.sk89q.worldguard.internal.flywaydb.core.internal.dbsupport.DbSupport;
import com.sk89q.worldguard.internal.flywaydb.core.internal.dbsupport.JdbcTemplate;
import com.sk89q.worldguard.internal.flywaydb.core.internal.dbsupport.Schema;
import com.sk89q.worldguard.internal.flywaydb.core.internal.dbsupport.SqlStatementBuilder;
import com.sk89q.worldguard.internal.flywaydb.core.internal.dbsupport.sqlserver.SQLServerSchema;
import com.sk89q.worldguard.internal.flywaydb.core.internal.dbsupport.sqlserver.SQLServerSqlStatementBuilder;
import com.sk89q.worldguard.internal.flywaydb.core.internal.util.StringUtils;
import com.sk89q.worldguard.internal.flywaydb.core.internal.util.logging.Log;
import com.sk89q.worldguard.internal.flywaydb.core.internal.util.logging.LogFactory;
import java.sql.Connection;
import java.sql.SQLException;

public class SQLServerDbSupport
extends DbSupport {
    private static final Log LOG = LogFactory.getLog(SQLServerDbSupport.class);

    public SQLServerDbSupport(Connection connection) {
        super(new JdbcTemplate(connection, 12));
    }

    @Override
    public String getDbName() {
        return "sqlserver";
    }

    @Override
    public String getCurrentUserFunction() {
        return "SUSER_SNAME()";
    }

    @Override
    protected String doGetCurrentSchema() throws SQLException {
        return this.jdbcTemplate.queryForString("SELECT SCHEMA_NAME()", new String[0]);
    }

    @Override
    protected void doSetCurrentSchema(Schema schema) throws SQLException {
        LOG.info("SQLServer does not support setting the schema for the current session. Default schema NOT changed to " + schema);
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
        return new SQLServerSqlStatementBuilder();
    }

    private String escapeIdentifier(String identifier) {
        return StringUtils.replaceAll(identifier, "]", "]]");
    }

    @Override
    public String doQuote(String identifier) {
        return "[" + this.escapeIdentifier(identifier) + "]";
    }

    @Override
    public Schema getSchema(String name) {
        return new SQLServerSchema(this.jdbcTemplate, this, name);
    }

    @Override
    public boolean catalogIsSchema() {
        return false;
    }
}

