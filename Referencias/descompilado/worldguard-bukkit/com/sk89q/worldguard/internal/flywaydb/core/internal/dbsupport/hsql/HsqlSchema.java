/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldguard.internal.flywaydb.core.internal.dbsupport.hsql;

import com.sk89q.worldguard.internal.flywaydb.core.internal.dbsupport.DbSupport;
import com.sk89q.worldguard.internal.flywaydb.core.internal.dbsupport.JdbcTemplate;
import com.sk89q.worldguard.internal.flywaydb.core.internal.dbsupport.Schema;
import com.sk89q.worldguard.internal.flywaydb.core.internal.dbsupport.Table;
import com.sk89q.worldguard.internal.flywaydb.core.internal.dbsupport.hsql.HsqlTable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class HsqlSchema
extends Schema {
    public HsqlSchema(JdbcTemplate jdbcTemplate, DbSupport dbSupport, String name) {
        super(jdbcTemplate, dbSupport, name);
    }

    @Override
    protected boolean doExists() throws SQLException {
        return this.jdbcTemplate.queryForInt("SELECT COUNT (*) FROM information_schema.system_schemas WHERE table_schem=?", this.name) > 0;
    }

    @Override
    protected boolean doEmpty() throws SQLException {
        return this.allTables().length == 0;
    }

    @Override
    protected void doCreate() throws SQLException {
        String user = this.jdbcTemplate.queryForString("SELECT USER() FROM (VALUES(0))", new String[0]);
        this.jdbcTemplate.execute("CREATE SCHEMA " + this.dbSupport.quote(this.name) + " AUTHORIZATION " + user, new Object[0]);
    }

    @Override
    protected void doDrop() throws SQLException {
        this.jdbcTemplate.execute("DROP SCHEMA " + this.dbSupport.quote(this.name) + " CASCADE", new Object[0]);
    }

    @Override
    protected void doClean() throws SQLException {
        for (Table table : this.allTables()) {
            table.drop();
        }
        for (String statement : this.generateDropStatementsForSequences()) {
            this.jdbcTemplate.execute(statement, new Object[0]);
        }
    }

    private List<String> generateDropStatementsForSequences() throws SQLException {
        List<String> sequenceNames = this.jdbcTemplate.queryForStringList("SELECT SEQUENCE_NAME FROM INFORMATION_SCHEMA.SYSTEM_SEQUENCES where SEQUENCE_SCHEMA = ?", this.name);
        ArrayList<String> statements = new ArrayList<String>();
        for (String seqName : sequenceNames) {
            statements.add("DROP SEQUENCE " + this.dbSupport.quote(this.name, seqName));
        }
        return statements;
    }

    @Override
    protected Table[] doAllTables() throws SQLException {
        List<String> tableNames = this.jdbcTemplate.queryForStringList("SELECT TABLE_NAME FROM INFORMATION_SCHEMA.SYSTEM_TABLES where TABLE_SCHEM = ? AND TABLE_TYPE = 'TABLE'", this.name);
        Table[] tables = new Table[tableNames.size()];
        for (int i = 0; i < tableNames.size(); ++i) {
            tables[i] = new HsqlTable(this.jdbcTemplate, this.dbSupport, this, tableNames.get(i));
        }
        return tables;
    }

    @Override
    public Table getTable(String tableName) {
        return new HsqlTable(this.jdbcTemplate, this.dbSupport, this, tableName);
    }
}

