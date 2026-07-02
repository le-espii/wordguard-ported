/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldguard.internal.flywaydb.core.internal.dbsupport.postgresql;

import com.sk89q.worldguard.internal.flywaydb.core.internal.dbsupport.DbSupport;
import com.sk89q.worldguard.internal.flywaydb.core.internal.dbsupport.JdbcTemplate;
import com.sk89q.worldguard.internal.flywaydb.core.internal.dbsupport.Schema;
import com.sk89q.worldguard.internal.flywaydb.core.internal.dbsupport.SchemaObject;
import com.sk89q.worldguard.internal.flywaydb.core.internal.dbsupport.Table;
import com.sk89q.worldguard.internal.flywaydb.core.internal.dbsupport.Type;
import com.sk89q.worldguard.internal.flywaydb.core.internal.dbsupport.postgresql.PostgreSQLTable;
import com.sk89q.worldguard.internal.flywaydb.core.internal.dbsupport.postgresql.PostgreSQLType;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PostgreSQLSchema
extends Schema {
    public PostgreSQLSchema(JdbcTemplate jdbcTemplate, DbSupport dbSupport, String name) {
        super(jdbcTemplate, dbSupport, name);
    }

    @Override
    protected boolean doExists() throws SQLException {
        return this.jdbcTemplate.queryForInt("SELECT COUNT(*) FROM pg_namespace WHERE nspname=?", this.name) > 0;
    }

    @Override
    protected boolean doEmpty() throws SQLException {
        int objectCount = this.jdbcTemplate.queryForInt("SELECT count(*) FROM information_schema.tables WHERE table_schema=? AND table_type='BASE TABLE'", this.name);
        return objectCount == 0;
    }

    @Override
    protected void doCreate() throws SQLException {
        this.jdbcTemplate.execute("CREATE SCHEMA " + this.dbSupport.quote(this.name), new Object[0]);
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
        for (String statement : this.generateDropStatementsForBaseTypes(true)) {
            this.jdbcTemplate.execute(statement, new Object[0]);
        }
        for (String statement : this.generateDropStatementsForAggregates()) {
            this.jdbcTemplate.execute(statement, new Object[0]);
        }
        for (String statement : this.generateDropStatementsForRoutines()) {
            this.jdbcTemplate.execute(statement, new Object[0]);
        }
        for (String statement : this.generateDropStatementsForEnums()) {
            this.jdbcTemplate.execute(statement, new Object[0]);
        }
        for (String statement : this.generateDropStatementsForDomains()) {
            this.jdbcTemplate.execute(statement, new Object[0]);
        }
        for (String statement : this.generateDropStatementsForBaseTypes(false)) {
            this.jdbcTemplate.execute(statement, new Object[0]);
        }
        for (SchemaObject schemaObject : this.allTypes()) {
            schemaObject.drop();
        }
    }

    private List<String> generateDropStatementsForSequences() throws SQLException {
        List<String> sequenceNames = this.jdbcTemplate.queryForStringList("SELECT sequence_name FROM information_schema.sequences WHERE sequence_schema=?", this.name);
        ArrayList<String> statements = new ArrayList<String>();
        for (String sequenceName : sequenceNames) {
            statements.add("DROP SEQUENCE IF EXISTS " + this.dbSupport.quote(this.name, sequenceName));
        }
        return statements;
    }

    private List<String> generateDropStatementsForBaseTypes(boolean recreate) throws SQLException {
        List<String> typeNames = this.jdbcTemplate.queryForStringList("select typname from pg_catalog.pg_type where typcategory in ('P', 'U') and typnamespace in (select oid from pg_catalog.pg_namespace where nspname = ?)", this.name);
        ArrayList<String> statements = new ArrayList<String>();
        for (String typeName : typeNames) {
            statements.add("DROP TYPE IF EXISTS " + this.dbSupport.quote(this.name, typeName) + " CASCADE");
        }
        if (recreate) {
            for (String typeName : typeNames) {
                statements.add("CREATE TYPE " + this.dbSupport.quote(this.name, typeName));
            }
        }
        return statements;
    }

    private List<String> generateDropStatementsForAggregates() throws SQLException {
        List<Map<String, String>> rows = this.jdbcTemplate.queryForList("SELECT proname, oidvectortypes(proargtypes) AS args FROM pg_proc INNER JOIN pg_namespace ns ON (pg_proc.pronamespace = ns.oid) WHERE pg_proc.proisagg = true AND ns.nspname = ?", this.name);
        ArrayList<String> statements = new ArrayList<String>();
        for (Map<String, String> row : rows) {
            statements.add("DROP AGGREGATE IF EXISTS " + this.dbSupport.quote(this.name, row.get("proname")) + "(" + row.get("args") + ") CASCADE");
        }
        return statements;
    }

    private List<String> generateDropStatementsForRoutines() throws SQLException {
        List<Map<String, String>> rows = this.jdbcTemplate.queryForList("SELECT proname, oidvectortypes(proargtypes) AS args FROM pg_proc INNER JOIN pg_namespace ns ON (pg_proc.pronamespace = ns.oid) WHERE pg_proc.proisagg = false AND ns.nspname = ?", this.name);
        ArrayList<String> statements = new ArrayList<String>();
        for (Map<String, String> row : rows) {
            statements.add("DROP FUNCTION IF EXISTS " + this.dbSupport.quote(this.name, row.get("proname")) + "(" + row.get("args") + ") CASCADE");
        }
        return statements;
    }

    private List<String> generateDropStatementsForEnums() throws SQLException {
        List<String> enumNames = this.jdbcTemplate.queryForStringList("SELECT t.typname FROM pg_catalog.pg_type t INNER JOIN pg_catalog.pg_namespace n ON n.oid = t.typnamespace WHERE n.nspname = ? and t.typtype = 'e'", this.name);
        ArrayList<String> statements = new ArrayList<String>();
        for (String enumName : enumNames) {
            statements.add("DROP TYPE " + this.dbSupport.quote(this.name, enumName));
        }
        return statements;
    }

    private List<String> generateDropStatementsForDomains() throws SQLException {
        List<String> domainNames = this.jdbcTemplate.queryForStringList("SELECT domain_name FROM information_schema.domains WHERE domain_schema=?", this.name);
        ArrayList<String> statements = new ArrayList<String>();
        for (String domainName : domainNames) {
            statements.add("DROP DOMAIN " + this.dbSupport.quote(this.name, domainName));
        }
        return statements;
    }

    @Override
    protected Table[] doAllTables() throws SQLException {
        List<String> tableNames = this.jdbcTemplate.queryForStringList("SELECT t.table_name FROM information_schema.tables t WHERE table_schema=? AND table_type='BASE TABLE' AND NOT (SELECT EXISTS (SELECT inhrelid FROM pg_catalog.pg_inherits WHERE inhrelid = ('\"'||t.table_schema||'\".\"'||t.table_name||'\"')::regclass::oid))", this.name);
        Table[] tables = new Table[tableNames.size()];
        for (int i = 0; i < tableNames.size(); ++i) {
            tables[i] = new PostgreSQLTable(this.jdbcTemplate, this.dbSupport, this, tableNames.get(i));
        }
        return tables;
    }

    @Override
    public Table getTable(String tableName) {
        return new PostgreSQLTable(this.jdbcTemplate, this.dbSupport, this, tableName);
    }

    @Override
    protected Type getType(String typeName) {
        return new PostgreSQLType(this.jdbcTemplate, this.dbSupport, this, typeName);
    }
}

