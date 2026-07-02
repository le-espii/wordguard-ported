/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldguard.internal.flywaydb.core.internal.dbsupport.oracle;

import com.sk89q.worldguard.internal.flywaydb.core.api.FlywayException;
import com.sk89q.worldguard.internal.flywaydb.core.internal.dbsupport.DbSupport;
import com.sk89q.worldguard.internal.flywaydb.core.internal.dbsupport.JdbcTemplate;
import com.sk89q.worldguard.internal.flywaydb.core.internal.dbsupport.Schema;
import com.sk89q.worldguard.internal.flywaydb.core.internal.dbsupport.Table;
import com.sk89q.worldguard.internal.flywaydb.core.internal.dbsupport.oracle.OracleTable;
import com.sk89q.worldguard.internal.flywaydb.core.internal.util.logging.Log;
import com.sk89q.worldguard.internal.flywaydb.core.internal.util.logging.LogFactory;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class OracleSchema
extends Schema {
    private static final Log LOG = LogFactory.getLog(OracleSchema.class);

    public OracleSchema(JdbcTemplate jdbcTemplate, DbSupport dbSupport, String name) {
        super(jdbcTemplate, dbSupport, name);
    }

    @Override
    protected boolean doExists() throws SQLException {
        return this.jdbcTemplate.queryForInt("SELECT COUNT(*) FROM all_users WHERE username=?", this.name) > 0;
    }

    @Override
    protected boolean doEmpty() throws SQLException {
        return this.jdbcTemplate.queryForInt("SELECT count(*) FROM all_objects WHERE owner = ?", this.name) == 0;
    }

    @Override
    protected void doCreate() throws SQLException {
        this.jdbcTemplate.execute("CREATE USER " + this.dbSupport.quote(this.name) + " IDENTIFIED BY flyway", new Object[0]);
        this.jdbcTemplate.execute("GRANT RESOURCE TO " + this.dbSupport.quote(this.name), new Object[0]);
    }

    @Override
    protected void doDrop() throws SQLException {
        this.jdbcTemplate.execute("DROP USER " + this.dbSupport.quote(this.name) + " CASCADE", new Object[0]);
    }

    @Override
    protected void doClean() throws SQLException {
        if ("SYSTEM".equals(this.name.toUpperCase())) {
            throw new FlywayException("Clean not supported on Oracle for user 'SYSTEM'! You should NEVER add your own objects to the SYSTEM schema!");
        }
        this.jdbcTemplate.execute("PURGE RECYCLEBIN", new Object[0]);
        for (String statement : this.generateDropStatementsForSpatialExtensions()) {
            this.jdbcTemplate.execute(statement, new Object[0]);
        }
        for (String statement : this.generateDropStatementsForQueueTables()) {
            this.jdbcTemplate.execute(statement, new Object[0]);
        }
        if (this.flashbackAvailable()) {
            this.executeAlterStatementsForFlashbackTables();
        }
        for (String statement : this.generateDropStatementsForObjectType("TRIGGER", "")) {
            this.jdbcTemplate.execute(statement, new Object[0]);
        }
        for (String statement : this.generateDropStatementsForObjectType("SEQUENCE", "")) {
            this.jdbcTemplate.execute(statement, new Object[0]);
        }
        for (String statement : this.generateDropStatementsForObjectType("FUNCTION", "")) {
            this.jdbcTemplate.execute(statement, new Object[0]);
        }
        for (String statement : this.generateDropStatementsForObjectType("MATERIALIZED VIEW", "PRESERVE TABLE")) {
            this.jdbcTemplate.execute(statement, new Object[0]);
        }
        for (String statement : this.generateDropStatementsForObjectType("PACKAGE", "")) {
            this.jdbcTemplate.execute(statement, new Object[0]);
        }
        for (String statement : this.generateDropStatementsForObjectType("PROCEDURE", "")) {
            this.jdbcTemplate.execute(statement, new Object[0]);
        }
        for (String statement : this.generateDropStatementsForObjectType("SYNONYM", "")) {
            this.jdbcTemplate.execute(statement, new Object[0]);
        }
        for (String statement : this.generateDropStatementsForObjectType("VIEW", "CASCADE CONSTRAINTS")) {
            this.jdbcTemplate.execute(statement, new Object[0]);
        }
        for (Table table : this.allTables()) {
            table.drop();
        }
        for (String statement : this.generateDropStatementsForXmlTables()) {
            this.jdbcTemplate.execute(statement, new Object[0]);
        }
        for (String statement : this.generateDropStatementsForObjectType("CLUSTER", "")) {
            this.jdbcTemplate.execute(statement, new Object[0]);
        }
        for (String statement : this.generateDropStatementsForObjectType("TYPE", "FORCE")) {
            this.jdbcTemplate.execute(statement, new Object[0]);
        }
        for (String statement : this.generateDropStatementsForObjectType("JAVA SOURCE", "")) {
            this.jdbcTemplate.execute(statement, new Object[0]);
        }
    }

    private void executeAlterStatementsForFlashbackTables() throws SQLException {
        List<String> tableNames = this.jdbcTemplate.queryForStringList("SELECT table_name FROM DBA_FLASHBACK_ARCHIVE_TABLES WHERE owner_name = ?", this.name);
        for (String tableName : tableNames) {
            this.jdbcTemplate.execute("ALTER TABLE " + this.dbSupport.quote(this.name, tableName) + " NO FLASHBACK ARCHIVE", new Object[0]);
            String queryForOracleTechnicalTables = "SELECT count(archive_table_name) FROM user_flashback_archive_tables WHERE table_name = ?";
            while (this.jdbcTemplate.queryForInt(queryForOracleTechnicalTables, tableName) > 0) {
                try {
                    LOG.debug("Actively waiting for Flashback cleanup on table: " + tableName);
                    Thread.sleep(1000L);
                }
                catch (InterruptedException e) {
                    throw new FlywayException("Waiting for Flashback cleanup interrupted", e);
                }
            }
        }
    }

    private boolean flashbackAvailable() throws SQLException {
        return this.jdbcTemplate.queryForInt("select count(*) from all_objects where object_name like 'DBA_FLASHBACK_ARCHIVE_TABLES'", new String[0]) > 0;
    }

    private List<String> generateDropStatementsForXmlTables() throws SQLException {
        ArrayList<String> dropStatements = new ArrayList<String>();
        if (!this.xmlDBExtensionsAvailable()) {
            LOG.debug("Oracle XML DB Extensions are not available. No cleaning of XML tables.");
            return dropStatements;
        }
        List<String> objectNames = this.jdbcTemplate.queryForStringList("SELECT table_name FROM all_xml_tables WHERE owner = ?", this.name);
        for (String objectName : objectNames) {
            dropStatements.add("DROP TABLE " + this.dbSupport.quote(this.name, objectName) + " PURGE");
        }
        return dropStatements;
    }

    private boolean xmlDBExtensionsAvailable() throws SQLException {
        return this.jdbcTemplate.queryForInt("SELECT COUNT(*) FROM all_users WHERE username = 'XDB'", new String[0]) > 0 && this.jdbcTemplate.queryForInt("SELECT COUNT(*) FROM all_views WHERE view_name = 'RESOURCE_VIEW'", new String[0]) > 0;
    }

    private List<String> generateDropStatementsForObjectType(String objectType, String extraArguments) throws SQLException {
        String query = "SELECT object_name FROM all_objects WHERE object_type = ? AND owner = ? AND object_name NOT LIKE 'MDRS_%$'";
        List<String> objectNames = this.jdbcTemplate.queryForStringList(query, objectType, this.name);
        ArrayList<String> dropStatements = new ArrayList<String>();
        for (String objectName : objectNames) {
            dropStatements.add("DROP " + objectType + " " + this.dbSupport.quote(this.name, objectName) + " " + extraArguments);
        }
        return dropStatements;
    }

    private List<String> generateDropStatementsForSpatialExtensions() throws SQLException {
        ArrayList<String> statements = new ArrayList<String>();
        if (!this.spatialExtensionsAvailable()) {
            LOG.debug("Oracle Spatial Extensions are not available. No cleaning of MDSYS tables and views.");
            return statements;
        }
        if (!this.dbSupport.getCurrentSchema().getName().equalsIgnoreCase(this.name)) {
            int count = this.jdbcTemplate.queryForInt("SELECT COUNT (*) FROM all_sdo_geom_metadata WHERE owner=?", this.name);
            if ((count += this.jdbcTemplate.queryForInt("SELECT COUNT (*) FROM all_sdo_index_info WHERE sdo_index_owner=?", this.name)) > 0) {
                LOG.warn("Unable to clean Oracle Spatial objects for schema '" + this.name + "' as they do not belong to the default schema for this connection!");
            }
            return statements;
        }
        statements.add("DELETE FROM mdsys.user_sdo_geom_metadata");
        List<String> indexNames = this.jdbcTemplate.queryForStringList("select INDEX_NAME from USER_SDO_INDEX_INFO", new String[0]);
        for (String indexName : indexNames) {
            statements.add("DROP INDEX \"" + indexName + "\"");
        }
        return statements;
    }

    private List<String> generateDropStatementsForQueueTables() throws SQLException {
        ArrayList<String> statements = new ArrayList<String>();
        List<String> queueTblNames = this.jdbcTemplate.queryForStringList("select QUEUE_TABLE from USER_QUEUE_TABLES", new String[0]);
        for (String queueTblName : queueTblNames) {
            statements.add("begin DBMS_AQADM.drop_queue_table (queue_table=> '" + queueTblName + "', FORCE => TRUE); end;");
        }
        return statements;
    }

    private boolean spatialExtensionsAvailable() throws SQLException {
        return this.jdbcTemplate.queryForInt("SELECT COUNT(*) FROM all_views WHERE owner = 'MDSYS' AND view_name = 'USER_SDO_GEOM_METADATA'", new String[0]) > 0;
    }

    @Override
    protected Table[] doAllTables() throws SQLException {
        List<String> tableNames = this.jdbcTemplate.queryForStringList("SELECT table_name FROM all_tables WHERE owner = ? AND table_name NOT LIKE 'BIN$%' AND table_name NOT LIKE 'MDRT_%$' AND table_name NOT LIKE 'MLOG$%' AND table_name NOT LIKE 'RUPD$%' AND table_name NOT LIKE 'DR$%' AND table_name NOT LIKE 'SYS_IOT_OVER_%' AND nested != 'YES' AND secondary != 'Y'", this.name);
        Table[] tables = new Table[tableNames.size()];
        for (int i = 0; i < tableNames.size(); ++i) {
            tables[i] = new OracleTable(this.jdbcTemplate, this.dbSupport, this, tableNames.get(i));
        }
        return tables;
    }

    @Override
    public Table getTable(String tableName) {
        return new OracleTable(this.jdbcTemplate, this.dbSupport, this, tableName);
    }
}

