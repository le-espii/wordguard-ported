/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldguard.internal.flywaydb.core.internal.dbsupport.sqlite;

import com.sk89q.worldguard.internal.flywaydb.core.internal.dbsupport.DbSupport;
import com.sk89q.worldguard.internal.flywaydb.core.internal.dbsupport.JdbcTemplate;
import com.sk89q.worldguard.internal.flywaydb.core.internal.dbsupport.Schema;
import com.sk89q.worldguard.internal.flywaydb.core.internal.dbsupport.Table;
import com.sk89q.worldguard.internal.flywaydb.core.internal.dbsupport.sqlite.SQLiteTable;
import com.sk89q.worldguard.internal.flywaydb.core.internal.util.logging.Log;
import com.sk89q.worldguard.internal.flywaydb.core.internal.util.logging.LogFactory;
import java.sql.SQLException;
import java.util.List;

public class SQLiteSchema
extends Schema {
    private static final Log LOG = LogFactory.getLog(SQLiteSchema.class);

    public SQLiteSchema(JdbcTemplate jdbcTemplate, DbSupport dbSupport, String name) {
        super(jdbcTemplate, dbSupport, name);
    }

    @Override
    protected boolean doExists() throws SQLException {
        try {
            this.doAllTables();
            return true;
        }
        catch (SQLException e) {
            return false;
        }
    }

    @Override
    protected boolean doEmpty() throws SQLException {
        Table[] tables = this.allTables();
        return tables.length == 0 || tables.length == 1 && "android_metadata".equals(tables[0].getName());
    }

    @Override
    protected void doCreate() throws SQLException {
        LOG.info("SQLite does not support creating schemas. Schema not created: " + this.name);
    }

    @Override
    protected void doDrop() throws SQLException {
        LOG.info("SQLite does not support dropping schemas. Schema not dropped: " + this.name);
    }

    @Override
    protected void doClean() throws SQLException {
        List<String> viewNames = this.jdbcTemplate.queryForStringList("SELECT tbl_name FROM " + this.dbSupport.quote(this.name) + ".sqlite_master WHERE type='view'", new String[0]);
        for (String viewName : viewNames) {
            this.jdbcTemplate.executeStatement("DROP VIEW " + this.dbSupport.quote(this.name, viewName));
        }
        for (Table table : this.allTables()) {
            table.drop();
        }
    }

    @Override
    protected Table[] doAllTables() throws SQLException {
        List<String> tableNames = this.jdbcTemplate.queryForStringList("SELECT tbl_name FROM " + this.dbSupport.quote(this.name) + ".sqlite_master WHERE type='table'", new String[0]);
        Table[] tables = new Table[tableNames.size()];
        for (int i = 0; i < tableNames.size(); ++i) {
            tables[i] = new SQLiteTable(this.jdbcTemplate, this.dbSupport, this, tableNames.get(i));
        }
        return tables;
    }

    @Override
    public Table getTable(String tableName) {
        return new SQLiteTable(this.jdbcTemplate, this.dbSupport, this, tableName);
    }
}

