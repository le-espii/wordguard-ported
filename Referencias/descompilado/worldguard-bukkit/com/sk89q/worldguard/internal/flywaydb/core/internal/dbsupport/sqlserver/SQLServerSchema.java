/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldguard.internal.flywaydb.core.internal.dbsupport.sqlserver;

import com.sk89q.worldguard.internal.flywaydb.core.internal.dbsupport.DbSupport;
import com.sk89q.worldguard.internal.flywaydb.core.internal.dbsupport.JdbcTemplate;
import com.sk89q.worldguard.internal.flywaydb.core.internal.dbsupport.Schema;
import com.sk89q.worldguard.internal.flywaydb.core.internal.dbsupport.Table;
import com.sk89q.worldguard.internal.flywaydb.core.internal.dbsupport.sqlserver.SQLServerTable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SQLServerSchema
extends Schema {
    public SQLServerSchema(JdbcTemplate jdbcTemplate, DbSupport dbSupport, String name) {
        super(jdbcTemplate, dbSupport, name);
    }

    @Override
    protected boolean doExists() throws SQLException {
        return this.jdbcTemplate.queryForInt("SELECT COUNT(*) FROM INFORMATION_SCHEMA.SCHEMATA WHERE SCHEMA_NAME=?", this.name) > 0;
    }

    @Override
    protected boolean doEmpty() throws SQLException {
        int objectCount = this.jdbcTemplate.queryForInt("Select count(*) FROM ( Select TABLE_NAME as OBJECT_NAME, TABLE_SCHEMA as OBJECT_SCHEMA from INFORMATION_SCHEMA.TABLES Union Select TABLE_NAME as OBJECT_NAME, TABLE_SCHEMA as OBJECT_SCHEMA from INFORMATION_SCHEMA.VIEWS Union Select CONSTRAINT_NAME as OBJECT_NAME, TABLE_SCHEMA as OBJECT_SCHEMA from INFORMATION_SCHEMA.TABLE_CONSTRAINTS Union Select ROUTINE_NAME as OBJECT_NAME, ROUTINE_SCHEMA as OBJECT_SCHEMA from INFORMATION_SCHEMA.ROUTINES ) R where OBJECT_SCHEMA = ?", this.name);
        return objectCount == 0;
    }

    @Override
    protected void doCreate() throws SQLException {
        this.jdbcTemplate.execute("CREATE SCHEMA " + this.dbSupport.quote(this.name), new Object[0]);
    }

    @Override
    protected void doDrop() throws SQLException {
        this.clean();
        this.jdbcTemplate.execute("DROP SCHEMA " + this.dbSupport.quote(this.name), new Object[0]);
    }

    @Override
    protected void doClean() throws SQLException {
        for (String statement : this.cleanForeignKeys()) {
            this.jdbcTemplate.execute(statement, new Object[0]);
        }
        for (String statement : this.cleanDefaultConstraints()) {
            this.jdbcTemplate.execute(statement, new Object[0]);
        }
        for (String statement : this.cleanRoutines()) {
            this.jdbcTemplate.execute(statement, new Object[0]);
        }
        for (String statement : this.cleanViews()) {
            this.jdbcTemplate.execute(statement, new Object[0]);
        }
        for (Table table : this.allTables()) {
            table.drop();
        }
        for (String statement : this.cleanTypes()) {
            this.jdbcTemplate.execute(statement, new Object[0]);
        }
        for (String statement : this.cleanSynonyms()) {
            this.jdbcTemplate.execute(statement, new Object[0]);
        }
    }

    private List<String> cleanForeignKeys() throws SQLException {
        List<Map<String, String>> constraintNames = this.jdbcTemplate.queryForList("SELECT table_name, constraint_name FROM INFORMATION_SCHEMA.TABLE_CONSTRAINTS WHERE constraint_type in ('FOREIGN KEY','CHECK') and table_schema=?", this.name);
        ArrayList<String> statements = new ArrayList<String>();
        for (Map<String, String> row : constraintNames) {
            String tableName = row.get("table_name");
            String constraintName = row.get("constraint_name");
            statements.add("ALTER TABLE " + this.dbSupport.quote(this.name, tableName) + " DROP CONSTRAINT " + this.dbSupport.quote(constraintName));
        }
        return statements;
    }

    private List<String> cleanDefaultConstraints() throws SQLException {
        List<Map<String, String>> constraintNames = this.jdbcTemplate.queryForList("select t.name as table_name, d.name as constraint_name from sys.tables t inner join sys.default_constraints d on d.parent_object_id = t.object_id\n inner join sys.schemas s on s.schema_id = t.schema_id\n where s.name = ?", this.name);
        ArrayList<String> statements = new ArrayList<String>();
        for (Map<String, String> row : constraintNames) {
            String tableName = row.get("table_name");
            String constraintName = row.get("constraint_name");
            statements.add("ALTER TABLE " + this.dbSupport.quote(this.name, tableName) + " DROP CONSTRAINT " + this.dbSupport.quote(constraintName));
        }
        return statements;
    }

    private List<String> cleanRoutines() throws SQLException {
        List<Map<String, String>> routineNames = this.jdbcTemplate.queryForList("SELECT routine_name, routine_type FROM INFORMATION_SCHEMA.ROUTINES WHERE routine_schema=?", this.name);
        ArrayList<String> statements = new ArrayList<String>();
        for (Map<String, String> row : routineNames) {
            String routineName = row.get("routine_name");
            String routineType = row.get("routine_type");
            statements.add("DROP " + routineType + " " + this.dbSupport.quote(this.name, routineName));
        }
        return statements;
    }

    private List<String> cleanViews() throws SQLException {
        List<String> viewNames = this.jdbcTemplate.queryForStringList("SELECT table_name FROM INFORMATION_SCHEMA.VIEWS WHERE table_schema=?", this.name);
        ArrayList<String> statements = new ArrayList<String>();
        for (String viewName : viewNames) {
            statements.add("DROP VIEW " + this.dbSupport.quote(this.name, viewName));
        }
        return statements;
    }

    private List<String> cleanTypes() throws SQLException {
        List<String> typeNames = this.jdbcTemplate.queryForStringList("SELECT t.name FROM sys.types t INNER JOIN sys.schemas s ON t.schema_id = s.schema_id WHERE t.is_user_defined = 1 AND s.name = ?", this.name);
        ArrayList<String> statements = new ArrayList<String>();
        for (String typeName : typeNames) {
            statements.add("DROP TYPE " + this.dbSupport.quote(this.name, typeName));
        }
        return statements;
    }

    private List<String> cleanSynonyms() throws SQLException {
        List<String> synonymNames = this.jdbcTemplate.queryForStringList("SELECT sn.name FROM sys.synonyms sn INNER JOIN sys.schemas s ON sn.schema_id = s.schema_id WHERE s.name = ?", this.name);
        ArrayList<String> statements = new ArrayList<String>();
        for (String synonymName : synonymNames) {
            statements.add("DROP SYNONYM " + this.dbSupport.quote(this.name, synonymName));
        }
        return statements;
    }

    @Override
    protected Table[] doAllTables() throws SQLException {
        List<String> tableNames = this.jdbcTemplate.queryForStringList("SELECT table_name FROM INFORMATION_SCHEMA.TABLES WHERE table_type='BASE TABLE' and table_schema=?", this.name);
        Table[] tables = new Table[tableNames.size()];
        for (int i = 0; i < tableNames.size(); ++i) {
            tables[i] = new SQLServerTable(this.jdbcTemplate, this.dbSupport, this, tableNames.get(i));
        }
        return tables;
    }

    @Override
    public Table getTable(String tableName) {
        return new SQLServerTable(this.jdbcTemplate, this.dbSupport, this, tableName);
    }
}

