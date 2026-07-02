/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldguard.internal.flywaydb.core.internal.dbsupport;

import com.sk89q.worldguard.internal.flywaydb.core.api.FlywayException;
import com.sk89q.worldguard.internal.flywaydb.core.internal.dbsupport.DbSupport;
import com.sk89q.worldguard.internal.flywaydb.core.internal.dbsupport.db2.DB2DbSupport;
import com.sk89q.worldguard.internal.flywaydb.core.internal.dbsupport.derby.DerbyDbSupport;
import com.sk89q.worldguard.internal.flywaydb.core.internal.dbsupport.h2.H2DbSupport;
import com.sk89q.worldguard.internal.flywaydb.core.internal.dbsupport.hsql.HsqlDbSupport;
import com.sk89q.worldguard.internal.flywaydb.core.internal.dbsupport.mysql.MySQLDbSupport;
import com.sk89q.worldguard.internal.flywaydb.core.internal.dbsupport.oracle.OracleDbSupport;
import com.sk89q.worldguard.internal.flywaydb.core.internal.dbsupport.postgresql.PostgreSQLDbSupport;
import com.sk89q.worldguard.internal.flywaydb.core.internal.dbsupport.sqlite.SQLiteDbSupport;
import com.sk89q.worldguard.internal.flywaydb.core.internal.dbsupport.sqlserver.SQLServerDbSupport;
import com.sk89q.worldguard.internal.flywaydb.core.internal.util.logging.Log;
import com.sk89q.worldguard.internal.flywaydb.core.internal.util.logging.LogFactory;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

public class DbSupportFactory {
    private static final Log LOG = LogFactory.getLog(DbSupportFactory.class);

    private DbSupportFactory() {
    }

    public static DbSupport createDbSupport(Connection connection, boolean printInfo) {
        String databaseProductName = DbSupportFactory.getDatabaseProductName(connection);
        if (printInfo) {
            LOG.info("Database: " + DbSupportFactory.getJdbcUrl(connection) + " (" + databaseProductName + ")");
        }
        if (databaseProductName.startsWith("Apache Derby")) {
            return new DerbyDbSupport(connection);
        }
        if (databaseProductName.startsWith("SQLite")) {
            return new SQLiteDbSupport(connection);
        }
        if (databaseProductName.startsWith("H2")) {
            return new H2DbSupport(connection);
        }
        if (databaseProductName.contains("HSQL Database Engine")) {
            return new HsqlDbSupport(connection);
        }
        if (databaseProductName.startsWith("Microsoft SQL Server")) {
            return new SQLServerDbSupport(connection);
        }
        if (databaseProductName.contains("MySQL")) {
            return new MySQLDbSupport(connection);
        }
        if (databaseProductName.startsWith("Oracle")) {
            return new OracleDbSupport(connection);
        }
        if (databaseProductName.startsWith("PostgreSQL")) {
            return new PostgreSQLDbSupport(connection);
        }
        if (databaseProductName.startsWith("DB2")) {
            return new DB2DbSupport(connection);
        }
        throw new FlywayException("Unsupported Database: " + databaseProductName);
    }

    private static String getJdbcUrl(Connection connection) {
        try {
            return connection.getMetaData().getURL();
        }
        catch (SQLException e) {
            throw new FlywayException("Unable to retrieve the Jdbc connection Url!", e);
        }
    }

    private static String getDatabaseProductName(Connection connection) {
        try {
            DatabaseMetaData databaseMetaData = connection.getMetaData();
            if (databaseMetaData == null) {
                throw new FlywayException("Unable to read database metadata while it is null!");
            }
            String databaseProductName = databaseMetaData.getDatabaseProductName();
            if (databaseProductName == null) {
                throw new FlywayException("Unable to determine database. Product name is null.");
            }
            int databaseMajorVersion = databaseMetaData.getDatabaseMajorVersion();
            int databaseMinorVersion = databaseMetaData.getDatabaseMinorVersion();
            return databaseProductName + " " + databaseMajorVersion + "." + databaseMinorVersion;
        }
        catch (SQLException e) {
            throw new FlywayException("Error while determining database product name", e);
        }
    }
}

