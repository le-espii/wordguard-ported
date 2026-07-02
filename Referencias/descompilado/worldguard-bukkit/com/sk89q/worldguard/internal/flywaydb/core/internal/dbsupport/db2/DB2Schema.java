/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldguard.internal.flywaydb.core.internal.dbsupport.db2;

import com.sk89q.worldguard.internal.flywaydb.core.internal.dbsupport.DbSupport;
import com.sk89q.worldguard.internal.flywaydb.core.internal.dbsupport.Function;
import com.sk89q.worldguard.internal.flywaydb.core.internal.dbsupport.JdbcTemplate;
import com.sk89q.worldguard.internal.flywaydb.core.internal.dbsupport.Schema;
import com.sk89q.worldguard.internal.flywaydb.core.internal.dbsupport.SchemaObject;
import com.sk89q.worldguard.internal.flywaydb.core.internal.dbsupport.Table;
import com.sk89q.worldguard.internal.flywaydb.core.internal.dbsupport.Type;
import com.sk89q.worldguard.internal.flywaydb.core.internal.dbsupport.db2.DB2Function;
import com.sk89q.worldguard.internal.flywaydb.core.internal.dbsupport.db2.DB2Table;
import com.sk89q.worldguard.internal.flywaydb.core.internal.dbsupport.db2.DB2Type;
import com.sk89q.worldguard.internal.flywaydb.core.internal.util.StringUtils;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DB2Schema
extends Schema {
    public DB2Schema(JdbcTemplate jdbcTemplate, DbSupport dbSupport, String name) {
        super(jdbcTemplate, dbSupport, name);
    }

    @Override
    protected boolean doExists() throws SQLException {
        return this.jdbcTemplate.queryForInt("SELECT COUNT(*) FROM syscat.schemata WHERE schemaname=?", this.name) > 0;
    }

    @Override
    protected boolean doEmpty() throws SQLException {
        int objectCount = this.jdbcTemplate.queryForInt("select count(*) from syscat.tables where tabschema = ?", this.name);
        objectCount += this.jdbcTemplate.queryForInt("select count(*) from syscat.views where viewschema = ?", this.name);
        objectCount += this.jdbcTemplate.queryForInt("select count(*) from syscat.sequences where seqschema = ?", this.name);
        objectCount += this.jdbcTemplate.queryForInt("select count(*) from syscat.indexes where indschema = ?", this.name);
        objectCount += this.jdbcTemplate.queryForInt("select count(*) from syscat.procedures where procschema = ?", this.name);
        return (objectCount += this.jdbcTemplate.queryForInt("select count(*) from syscat.functions where funcschema = ?", this.name)) == 0;
    }

    @Override
    protected void doCreate() throws SQLException {
        this.jdbcTemplate.execute("CREATE SCHEMA " + this.dbSupport.quote(this.name), new Object[0]);
    }

    @Override
    protected void doDrop() throws SQLException {
        this.clean();
        this.jdbcTemplate.execute("DROP SCHEMA " + this.dbSupport.quote(this.name) + " RESTRICT", new Object[0]);
    }

    @Override
    protected void doClean() throws SQLException {
        for (String dropVersioningStatement : this.generateDropVersioningStatement()) {
            this.jdbcTemplate.execute(dropVersioningStatement, new Object[0]);
        }
        for (String dropStatement : this.generateDropStatements(this.name, "V", "VIEW")) {
            this.jdbcTemplate.execute(dropStatement, new Object[0]);
        }
        for (String dropStatement : this.generateDropStatements(this.name, "A", "ALIAS")) {
            this.jdbcTemplate.execute(dropStatement, new Object[0]);
        }
        for (Table table : this.allTables()) {
            table.drop();
        }
        for (String dropStatement : this.generateDropStatementsForSequences(this.name)) {
            this.jdbcTemplate.execute(dropStatement, new Object[0]);
        }
        for (String dropStatement : this.generateDropStatementsForProcedures(this.name)) {
            this.jdbcTemplate.execute(dropStatement, new Object[0]);
        }
        for (SchemaObject schemaObject : this.allFunctions()) {
            schemaObject.drop();
        }
        for (SchemaObject schemaObject : this.allTypes()) {
            schemaObject.drop();
        }
    }

    private List<String> generateDropStatementsForProcedures(String schema) throws SQLException {
        String dropProcGenQuery = "select rtrim(PROCNAME) from SYSCAT.PROCEDURES where PROCSCHEMA = '" + schema + "'";
        return this.buildDropStatements("DROP PROCEDURE", dropProcGenQuery, schema);
    }

    private List<String> generateDropStatementsForSequences(String schema) throws SQLException {
        String dropSeqGenQuery = "select rtrim(SEQNAME) from SYSCAT.SEQUENCES where SEQSCHEMA = '" + schema + "' and SEQTYPE='S'";
        return this.buildDropStatements("DROP SEQUENCE", dropSeqGenQuery, schema);
    }

    private List<String> generateDropStatements(String schema, String tableType, String objectType) throws SQLException {
        String dropTablesGenQuery = "select rtrim(TABNAME) from SYSCAT.TABLES where TYPE='" + tableType + "' and TABSCHEMA = '" + schema + "'";
        return this.buildDropStatements("DROP " + objectType, dropTablesGenQuery, schema);
    }

    private List<String> buildDropStatements(String dropPrefix, String query, String schema) throws SQLException {
        ArrayList<String> dropStatements = new ArrayList<String>();
        List<String> dbObjects = this.jdbcTemplate.queryForStringList(query, new String[0]);
        for (String dbObject : dbObjects) {
            dropStatements.add(dropPrefix + " " + this.dbSupport.quote(schema, dbObject));
        }
        return dropStatements;
    }

    private List<String> generateDropVersioningStatement() throws SQLException {
        Table[] versioningTables;
        ArrayList<String> dropVersioningStatements = new ArrayList<String>();
        for (Table table : versioningTables = this.findTables("select rtrim(TABNAME) from SYSCAT.TABLES where TEMPORALTYPE <> 'N' and TABSCHEMA = ?", this.name)) {
            dropVersioningStatements.add("ALTER TABLE " + table.toString() + " DROP VERSIONING");
        }
        return dropVersioningStatements;
    }

    private Table[] findTables(String sqlQuery, String ... params) throws SQLException {
        List<String> tableNames = this.jdbcTemplate.queryForStringList(sqlQuery, params);
        Table[] tables = new Table[tableNames.size()];
        for (int i = 0; i < tableNames.size(); ++i) {
            tables[i] = new DB2Table(this.jdbcTemplate, this.dbSupport, this, tableNames.get(i));
        }
        return tables;
    }

    @Override
    protected Table[] doAllTables() throws SQLException {
        return this.findTables("select rtrim(TABNAME) from SYSCAT.TABLES where TYPE='T' and TABSCHEMA = ?", this.name);
    }

    @Override
    protected Function[] doAllFunctions() throws SQLException {
        List<Map<String, String>> rows = this.jdbcTemplate.queryForList("select p.SPECIFICNAME, p.FUNCNAME, substr( xmlserialize( xmlagg( xmltext( concat( ', ', TYPENAME ) ) ) as varchar( 1024 ) ), 3 ) as PARAMS from SYSCAT.FUNCTIONS f inner join SYSCAT.FUNCPARMS p on f.SPECIFICNAME = p.SPECIFICNAME where f.ORIGIN = 'Q' and p.FUNCSCHEMA = ? and p.ROWTYPE = 'P' group by p.SPECIFICNAME, p.FUNCNAME order by p.SPECIFICNAME", this.name);
        ArrayList<Function> functions = new ArrayList<Function>();
        for (Map<String, String> row : rows) {
            functions.add(this.getFunction(row.get("FUNCNAME"), StringUtils.tokenizeToStringArray(row.get("PARAMS"), ",")));
        }
        return functions.toArray(new Function[functions.size()]);
    }

    @Override
    public Table getTable(String tableName) {
        return new DB2Table(this.jdbcTemplate, this.dbSupport, this, tableName);
    }

    @Override
    protected Type getType(String typeName) {
        return new DB2Type(this.jdbcTemplate, this.dbSupport, this, typeName);
    }

    @Override
    public Function getFunction(String functionName, String ... args) {
        return new DB2Function(this.jdbcTemplate, this.dbSupport, this, functionName, args);
    }
}

