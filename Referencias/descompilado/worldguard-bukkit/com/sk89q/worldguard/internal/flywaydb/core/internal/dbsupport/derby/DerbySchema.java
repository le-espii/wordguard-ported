/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldguard.internal.flywaydb.core.internal.dbsupport.derby;

import com.sk89q.worldguard.internal.flywaydb.core.internal.dbsupport.DbSupport;
import com.sk89q.worldguard.internal.flywaydb.core.internal.dbsupport.JdbcTemplate;
import com.sk89q.worldguard.internal.flywaydb.core.internal.dbsupport.Schema;
import com.sk89q.worldguard.internal.flywaydb.core.internal.dbsupport.Table;
import com.sk89q.worldguard.internal.flywaydb.core.internal.dbsupport.derby.DerbyTable;
import com.sk89q.worldguard.internal.flywaydb.core.internal.util.StringUtils;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DerbySchema
extends Schema {
    public DerbySchema(JdbcTemplate jdbcTemplate, DbSupport dbSupport, String name) {
        super(jdbcTemplate, dbSupport, name);
    }

    @Override
    protected boolean doExists() throws SQLException {
        return this.jdbcTemplate.queryForInt("SELECT COUNT (*) FROM sys.sysschemas WHERE schemaname=?", this.name) > 0;
    }

    @Override
    protected boolean doEmpty() throws SQLException {
        return this.allTables().length == 0;
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
        for (String statement : this.generateDropStatementsForConstraints()) {
            this.jdbcTemplate.execute(statement, new Object[0]);
        }
        List<String> viewNames = this.listObjectNames("TABLE", "TABLETYPE='V'");
        for (String statement : this.generateDropStatements("VIEW", viewNames, "")) {
            this.jdbcTemplate.execute(statement, new Object[0]);
        }
        for (Table table : this.allTables()) {
            table.drop();
        }
        List<String> sequenceNames = this.listObjectNames("SEQUENCE", "");
        for (String statement : this.generateDropStatements("SEQUENCE", sequenceNames, "RESTRICT")) {
            this.jdbcTemplate.execute(statement, new Object[0]);
        }
    }

    private List<String> generateDropStatementsForConstraints() throws SQLException {
        List<Map<String, String>> results = this.jdbcTemplate.queryForList("SELECT c.constraintname, t.tablename FROM sys.sysconstraints c INNER JOIN sys.systables t ON c.tableid = t.tableid INNER JOIN sys.sysschemas s ON c.schemaid = s.schemaid WHERE c.type = 'F' AND s.schemaname = ?", this.name);
        ArrayList<String> statements = new ArrayList<String>();
        for (Map<String, String> result : results) {
            String dropStatement = "ALTER TABLE " + this.dbSupport.quote(this.name, result.get("TABLENAME")) + " DROP CONSTRAINT " + this.dbSupport.quote(result.get("CONSTRAINTNAME"));
            statements.add(dropStatement);
        }
        return statements;
    }

    private List<String> generateDropStatements(String objectType, List<String> objectNames, String dropStatementSuffix) {
        ArrayList<String> statements = new ArrayList<String>();
        for (String objectName : objectNames) {
            String dropStatement = "DROP " + objectType + " " + this.dbSupport.quote(this.name, objectName) + " " + dropStatementSuffix;
            statements.add(dropStatement);
        }
        return statements;
    }

    @Override
    protected Table[] doAllTables() throws SQLException {
        List<String> tableNames = this.listObjectNames("TABLE", "TABLETYPE='T'");
        Table[] tables = new Table[tableNames.size()];
        for (int i = 0; i < tableNames.size(); ++i) {
            tables[i] = new DerbyTable(this.jdbcTemplate, this.dbSupport, this, tableNames.get(i));
        }
        return tables;
    }

    private List<String> listObjectNames(String objectType, String querySuffix) throws SQLException {
        String query = "SELECT " + objectType + "name FROM sys.sys" + objectType + "s WHERE schemaid in (SELECT schemaid FROM sys.sysschemas where schemaname = ?)";
        if (StringUtils.hasLength(querySuffix)) {
            query = query + " AND " + querySuffix;
        }
        return this.jdbcTemplate.queryForStringList(query, this.name);
    }

    @Override
    public Table getTable(String tableName) {
        return new DerbyTable(this.jdbcTemplate, this.dbSupport, this, tableName);
    }
}

