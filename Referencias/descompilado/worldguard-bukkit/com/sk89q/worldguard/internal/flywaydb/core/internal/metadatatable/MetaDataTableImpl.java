/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldguard.internal.flywaydb.core.internal.metadatatable;

import com.sk89q.worldguard.internal.flywaydb.core.api.FlywayException;
import com.sk89q.worldguard.internal.flywaydb.core.api.MigrationType;
import com.sk89q.worldguard.internal.flywaydb.core.api.MigrationVersion;
import com.sk89q.worldguard.internal.flywaydb.core.internal.dbsupport.DbSupport;
import com.sk89q.worldguard.internal.flywaydb.core.internal.dbsupport.JdbcTemplate;
import com.sk89q.worldguard.internal.flywaydb.core.internal.dbsupport.Schema;
import com.sk89q.worldguard.internal.flywaydb.core.internal.dbsupport.SqlScript;
import com.sk89q.worldguard.internal.flywaydb.core.internal.dbsupport.Table;
import com.sk89q.worldguard.internal.flywaydb.core.internal.metadatatable.AppliedMigration;
import com.sk89q.worldguard.internal.flywaydb.core.internal.metadatatable.MetaDataTable;
import com.sk89q.worldguard.internal.flywaydb.core.internal.util.PlaceholderReplacer;
import com.sk89q.worldguard.internal.flywaydb.core.internal.util.StopWatch;
import com.sk89q.worldguard.internal.flywaydb.core.internal.util.StringUtils;
import com.sk89q.worldguard.internal.flywaydb.core.internal.util.TimeFormat;
import com.sk89q.worldguard.internal.flywaydb.core.internal.util.jdbc.RowMapper;
import com.sk89q.worldguard.internal.flywaydb.core.internal.util.logging.Log;
import com.sk89q.worldguard.internal.flywaydb.core.internal.util.logging.LogFactory;
import com.sk89q.worldguard.internal.flywaydb.core.internal.util.scanner.classpath.ClassPathResource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class MetaDataTableImpl
implements MetaDataTable {
    private static final Log LOG = LogFactory.getLog(MetaDataTableImpl.class);
    private final DbSupport dbSupport;
    private final Table table;
    private final JdbcTemplate jdbcTemplate;
    private ClassLoader classLoader;

    public MetaDataTableImpl(DbSupport dbSupport, Table table, ClassLoader classLoader) {
        this.jdbcTemplate = dbSupport.getJdbcTemplate();
        this.dbSupport = dbSupport;
        this.table = table;
        this.classLoader = classLoader;
    }

    private void createIfNotExists() {
        if (this.table.exists()) {
            return;
        }
        LOG.info("Creating Metadata table: " + this.table);
        String resourceName = "com/sk89q/worldguard/internal/flywaydb/core/internal/dbsupport/" + this.dbSupport.getDbName() + "/createMetaDataTable.sql";
        String source = new ClassPathResource(resourceName, this.classLoader).loadAsString("UTF-8");
        HashMap<String, String> placeholders = new HashMap<String, String>();
        placeholders.put("schema", this.table.getSchema().getName());
        placeholders.put("table", this.table.getName());
        String sourceNoPlaceholders = new PlaceholderReplacer(placeholders, "${", "}").replacePlaceholders(source);
        SqlScript sqlScript = new SqlScript(sourceNoPlaceholders, this.dbSupport);
        sqlScript.execute(this.jdbcTemplate);
        LOG.debug("Metadata table " + this.table + " created.");
    }

    @Override
    public void lock() {
        this.createIfNotExists();
        this.table.lock();
    }

    @Override
    public void addAppliedMigration(AppliedMigration appliedMigration) {
        this.createIfNotExists();
        MigrationVersion version = appliedMigration.getVersion();
        try {
            int versionRank = this.calculateVersionRank(version);
            this.jdbcTemplate.update("UPDATE " + this.table + " SET " + this.dbSupport.quote("version_rank") + " = " + this.dbSupport.quote("version_rank") + " + 1 WHERE " + this.dbSupport.quote("version_rank") + " >= ?", versionRank);
            this.jdbcTemplate.update("INSERT INTO " + this.table + " (" + this.dbSupport.quote("version_rank") + "," + this.dbSupport.quote("installed_rank") + "," + this.dbSupport.quote("version") + "," + this.dbSupport.quote("description") + "," + this.dbSupport.quote("type") + "," + this.dbSupport.quote("script") + "," + this.dbSupport.quote("checksum") + "," + this.dbSupport.quote("installed_by") + "," + this.dbSupport.quote("execution_time") + "," + this.dbSupport.quote("success") + ")" + " VALUES (?, ?, ?, ?, ?, ?, ?, " + this.dbSupport.getCurrentUserFunction() + ", ?, ?)", versionRank, this.calculateInstalledRank(), version.toString(), appliedMigration.getDescription(), appliedMigration.getType().name(), appliedMigration.getScript(), appliedMigration.getChecksum(), appliedMigration.getExecutionTime(), appliedMigration.isSuccess());
            LOG.debug("MetaData table " + this.table + " successfully updated to reflect changes");
        }
        catch (SQLException e) {
            throw new FlywayException("Unable to insert row for version '" + version + "' in metadata table " + this.table, e);
        }
    }

    private int calculateInstalledRank() throws SQLException {
        int currentMax = this.jdbcTemplate.queryForInt("SELECT MAX(" + this.dbSupport.quote("installed_rank") + ")" + " FROM " + this.table, new String[0]);
        return currentMax + 1;
    }

    private int calculateVersionRank(MigrationVersion version) throws SQLException {
        List<String> versions = this.jdbcTemplate.queryForStringList("select " + this.dbSupport.quote("version") + " from " + this.table, new String[0]);
        ArrayList<MigrationVersion> migrationVersions = new ArrayList<MigrationVersion>();
        for (String versionStr : versions) {
            migrationVersions.add(MigrationVersion.fromVersion(versionStr));
        }
        Collections.sort(migrationVersions);
        for (int i = 0; i < migrationVersions.size(); ++i) {
            if (version.compareTo((MigrationVersion)migrationVersions.get(i)) >= 0) continue;
            return i + 1;
        }
        return migrationVersions.size() + 1;
    }

    @Override
    public List<AppliedMigration> allAppliedMigrations() {
        return this.findAppliedMigrations(new MigrationType[0]);
    }

    private List<AppliedMigration> findAppliedMigrations(MigrationType ... migrationTypes) {
        if (!this.table.exists()) {
            return new ArrayList<AppliedMigration>();
        }
        this.createIfNotExists();
        String query = "SELECT " + this.dbSupport.quote("version_rank") + "," + this.dbSupport.quote("installed_rank") + "," + this.dbSupport.quote("version") + "," + this.dbSupport.quote("description") + "," + this.dbSupport.quote("type") + "," + this.dbSupport.quote("script") + "," + this.dbSupport.quote("checksum") + "," + this.dbSupport.quote("installed_on") + "," + this.dbSupport.quote("installed_by") + "," + this.dbSupport.quote("execution_time") + "," + this.dbSupport.quote("success") + " FROM " + this.table;
        if (migrationTypes.length > 0) {
            query = query + " WHERE " + this.dbSupport.quote("type") + " IN (";
            for (int i = 0; i < migrationTypes.length; ++i) {
                if (i > 0) {
                    query = query + ",";
                }
                query = query + "'" + (Object)((Object)migrationTypes[i]) + "'";
            }
            query = query + ")";
        }
        query = query + " ORDER BY " + this.dbSupport.quote("version_rank");
        try {
            return this.jdbcTemplate.query(query, new RowMapper<AppliedMigration>(){

                @Override
                public AppliedMigration mapRow(ResultSet rs) throws SQLException {
                    Integer checksum = rs.getInt("checksum");
                    if (rs.wasNull()) {
                        checksum = null;
                    }
                    return new AppliedMigration(rs.getInt("version_rank"), rs.getInt("installed_rank"), MigrationVersion.fromVersion(rs.getString("version")), rs.getString("description"), MigrationType.valueOf(rs.getString("type")), rs.getString("script"), checksum, rs.getTimestamp("installed_on"), rs.getString("installed_by"), rs.getInt("execution_time"), rs.getBoolean("success"));
                }
            });
        }
        catch (SQLException e) {
            throw new FlywayException("Error while retrieving the list of applied migrations from metadata table " + this.table, e);
        }
    }

    @Override
    public void addInitMarker(MigrationVersion initVersion, String initDescription) {
        this.addAppliedMigration(new AppliedMigration(initVersion, initDescription, MigrationType.INIT, initDescription, null, 0, true));
    }

    @Override
    public void removeFailedMigrations() {
        if (!this.table.exists()) {
            LOG.info("Repair of metadata table " + this.table + " not necessary. No failed migration detected.");
            return;
        }
        this.createIfNotExists();
        try {
            int failedCount = this.jdbcTemplate.queryForInt("SELECT COUNT(*) FROM " + this.table + " WHERE " + this.dbSupport.quote("success") + "=" + this.dbSupport.getBooleanFalse(), new String[0]);
            if (failedCount == 0) {
                LOG.info("Repair of metadata table " + this.table + " not necessary. No failed migration detected.");
                return;
            }
        }
        catch (SQLException e) {
            throw new FlywayException("Unable to check the metadata table " + this.table + " for failed migrations", e);
        }
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        try {
            this.jdbcTemplate.execute("DELETE FROM " + this.table + " WHERE " + this.dbSupport.quote("success") + " = " + this.dbSupport.getBooleanFalse(), new Object[0]);
        }
        catch (SQLException e) {
            throw new FlywayException("Unable to repair metadata table " + this.table, e);
        }
        stopWatch.stop();
        LOG.info("Metadata table " + this.table + " successfully repaired (execution time " + TimeFormat.format(stopWatch.getTotalTimeMillis()) + ").");
        LOG.info("Manual cleanup of the remaining effects the failed migration may still be required.");
    }

    @Override
    public void addSchemasMarker(Schema[] schemas) {
        this.createIfNotExists();
        this.addAppliedMigration(new AppliedMigration(MigrationVersion.fromVersion("0"), "<< Flyway Schema Creation >>", MigrationType.SCHEMA, StringUtils.arrayToCommaDelimitedString(schemas), null, 0, true));
    }

    @Override
    public boolean hasSchemasMarker() {
        if (!this.table.exists()) {
            return false;
        }
        this.createIfNotExists();
        try {
            int count = this.jdbcTemplate.queryForInt("SELECT COUNT(*) FROM " + this.table + " WHERE " + this.dbSupport.quote("type") + "='SCHEMA'", new String[0]);
            return count > 0;
        }
        catch (SQLException e) {
            throw new FlywayException("Unable to check whether the metadata table " + this.table + " has a schema marker migration", e);
        }
    }

    @Override
    public boolean hasInitMarker() {
        if (!this.table.exists()) {
            return false;
        }
        this.createIfNotExists();
        try {
            int count = this.jdbcTemplate.queryForInt("SELECT COUNT(*) FROM " + this.table + " WHERE " + this.dbSupport.quote("type") + "='INIT'", new String[0]);
            return count > 0;
        }
        catch (SQLException e) {
            throw new FlywayException("Unable to check whether the metadata table " + this.table + " has an init marker migration", e);
        }
    }

    @Override
    public AppliedMigration getInitMarker() {
        List<AppliedMigration> appliedMigrations = this.findAppliedMigrations(MigrationType.INIT);
        return appliedMigrations.isEmpty() ? null : appliedMigrations.get(0);
    }

    @Override
    public boolean hasAppliedMigrations() {
        if (!this.table.exists()) {
            return false;
        }
        this.createIfNotExists();
        try {
            int count = this.jdbcTemplate.queryForInt("SELECT COUNT(*) FROM " + this.table + " WHERE " + this.dbSupport.quote("type") + " NOT IN ('SCHEMA', 'INIT')", new String[0]);
            return count > 0;
        }
        catch (SQLException e) {
            throw new FlywayException("Unable to check whether the metadata table " + this.table + " has applied migrations", e);
        }
    }

    @Override
    public void updateChecksum(MigrationVersion version, Integer checksum) {
        try {
            this.jdbcTemplate.update("UPDATE " + this.table + " SET " + this.dbSupport.quote("checksum") + "=" + checksum + " WHERE " + this.dbSupport.quote("version") + "='" + version + "'", new Object[0]);
        }
        catch (SQLException e) {
            throw new FlywayException("Unable to update checksum in metadata table " + this.table + " for version " + version + " to " + checksum, e);
        }
    }

    public String toString() {
        return this.table.toString();
    }
}

