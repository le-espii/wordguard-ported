/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldguard.internal.flywaydb.core.internal.util.jdbc;

import com.sk89q.worldguard.internal.flywaydb.core.api.FlywayException;
import com.sk89q.worldguard.internal.flywaydb.core.internal.util.logging.Log;
import com.sk89q.worldguard.internal.flywaydb.core.internal.util.logging.LogFactory;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.sql.DataSource;

public class JdbcUtils {
    private static final Log LOG = LogFactory.getLog(JdbcUtils.class);

    private JdbcUtils() {
    }

    public static Connection openConnection(DataSource dataSource) throws FlywayException {
        try {
            Connection connection = dataSource.getConnection();
            if (connection == null) {
                throw new FlywayException("Unable to obtain Jdbc connection from DataSource");
            }
            return connection;
        }
        catch (SQLException e) {
            throw new FlywayException("Unable to obtain Jdbc connection from DataSource", e);
        }
    }

    public static void closeConnection(Connection connection) {
        if (connection == null) {
            return;
        }
        try {
            connection.close();
        }
        catch (SQLException e) {
            LOG.error("Error while closing Jdbc connection", e);
        }
    }

    public static void closeStatement(Statement statement) {
        if (statement == null) {
            return;
        }
        try {
            statement.close();
        }
        catch (SQLException e) {
            LOG.error("Error while closing Jdbc statement", e);
        }
    }

    public static void closeResultSet(ResultSet resultSet) {
        if (resultSet == null) {
            return;
        }
        try {
            resultSet.close();
        }
        catch (SQLException e) {
            LOG.error("Error while closing Jdbc resultSet", e);
        }
    }
}

