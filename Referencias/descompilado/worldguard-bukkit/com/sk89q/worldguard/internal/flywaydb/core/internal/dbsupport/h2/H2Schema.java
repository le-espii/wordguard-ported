/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldguard.internal.flywaydb.core.internal.dbsupport.h2;

import com.sk89q.worldguard.internal.flywaydb.core.internal.dbsupport.DbSupport;
import com.sk89q.worldguard.internal.flywaydb.core.internal.dbsupport.JdbcTemplate;
import com.sk89q.worldguard.internal.flywaydb.core.internal.dbsupport.Schema;
import com.sk89q.worldguard.internal.flywaydb.core.internal.dbsupport.Table;
import com.sk89q.worldguard.internal.flywaydb.core.internal.dbsupport.h2.H2Table;
import com.sk89q.worldguard.internal.flywaydb.core.internal.util.StringUtils;
import com.sk89q.worldguard.internal.flywaydb.core.internal.util.logging.Log;
import com.sk89q.worldguard.internal.flywaydb.core.internal.util.logging.LogFactory;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class H2Schema
extends Schema {
    private static final Log LOG = LogFactory.getLog(H2Schema.class);

    public H2Schema(JdbcTemplate jdbcTemplate, DbSupport dbSupport, String name) {
        super(jdbcTemplate, dbSupport, name);
    }

    @Override
    protected boolean doExists() throws SQLException {
        return this.jdbcTemplate.queryForInt("SELECT COUNT(*) FROM INFORMATION_SCHEMA.schemata WHERE schema_name=?", this.name) > 0;
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
        this.jdbcTemplate.execute("DROP SCHEMA " + this.dbSupport.quote(this.name), new Object[0]);
    }

    @Override
    protected void doClean() throws SQLException {
        for (Table table : this.allTables()) {
            table.drop();
        }
        List<String> sequenceNames = this.listObjectNames("SEQUENCE", "IS_GENERATED = false");
        for (String statement : this.generateDropStatements("SEQUENCE", sequenceNames, "")) {
            this.jdbcTemplate.execute(statement, new Object[0]);
        }
        List<String> constantNames = this.listObjectNames("CONSTANT", "");
        for (String statement : this.generateDropStatements("CONSTANT", constantNames, "")) {
            this.jdbcTemplate.execute(statement, new Object[0]);
        }
        List<String> domainNames = this.listObjectNames("DOMAIN", "");
        if (!domainNames.isEmpty()) {
            if (this.name.equals(this.dbSupport.getCurrentSchema().getName())) {
                for (String statement : this.generateDropStatementsForCurrentSchema("DOMAIN", domainNames, "")) {
                    this.jdbcTemplate.execute(statement, new Object[0]);
                }
            } else {
                LOG.error("Unable to drop DOMAIN objects in schema " + this.dbSupport.quote(this.name) + " due to H2 bug! (More info: http://code.google.com/p/h2database/issues/detail?id=306)");
            }
        }
    }

    private List<String> generateDropStatements(String objectType, List<String> objectNames, String dropStatementSuffix) {
        ArrayList<String> statements = new ArrayList<String>();
        for (String objectName : objectNames) {
            String dropStatement = "DROP " + objectType + this.dbSupport.quote(this.name, objectName) + " " + dropStatementSuffix;
            statements.add(dropStatement);
        }
        return statements;
    }

    private List<String> generateDropStatementsForCurrentSchema(String objectType, List<String> objectNames, String dropStatementSuffix) {
        ArrayList<String> statements = new ArrayList<String>();
        for (String objectName : objectNames) {
            String dropStatement = "DROP " + objectType + this.dbSupport.quote(objectName) + " " + dropStatementSuffix;
            statements.add(dropStatement);
        }
        return statements;
    }

    @Override
    protected Table[] doAllTables() throws SQLException {
        List<String> tableNames = this.listObjectNames("TABLE", "TABLE_TYPE = 'TABLE'");
        Table[] tables = new Table[tableNames.size()];
        for (int i = 0; i < tableNames.size(); ++i) {
            tables[i] = new H2Table(this.jdbcTemplate, this.dbSupport, this, tableNames.get(i));
        }
        return tables;
    }

    private List<String> listObjectNames(String objectType, String querySuffix) throws SQLException {
        String query = "SELECT " + objectType + "_NAME FROM INFORMATION_SCHEMA." + objectType + "s WHERE " + objectType + "_schema = ?";
        if (StringUtils.hasLength(querySuffix)) {
            query = query + " AND " + querySuffix;
        }
        return this.jdbcTemplate.queryForStringList(query, this.name);
    }

    @Override
    public Table getTable(String tableName) {
        return new H2Table(this.jdbcTemplate, this.dbSupport, this, tableName);
    }
}

