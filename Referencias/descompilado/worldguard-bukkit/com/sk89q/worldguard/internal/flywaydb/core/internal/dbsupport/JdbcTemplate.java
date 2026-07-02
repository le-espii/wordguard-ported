/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldguard.internal.flywaydb.core.internal.dbsupport;

import com.sk89q.worldguard.internal.flywaydb.core.internal.util.jdbc.JdbcUtils;
import com.sk89q.worldguard.internal.flywaydb.core.internal.util.jdbc.RowMapper;
import com.sk89q.worldguard.internal.flywaydb.core.internal.util.logging.Log;
import com.sk89q.worldguard.internal.flywaydb.core.internal.util.logging.LogFactory;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JdbcTemplate {
    private static final Log LOG = LogFactory.getLog(JdbcTemplate.class);
    private final Connection connection;
    private final int nullType;

    public JdbcTemplate(Connection connection, int nullType) {
        this.connection = connection;
        this.nullType = nullType;
    }

    public Connection getConnection() {
        return this.connection;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public List<Map<String, String>> queryForList(String query, String ... params) throws SQLException {
        ArrayList<Map<String, String>> result;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            statement = this.connection.prepareStatement(query);
            for (int i = 0; i < params.length; ++i) {
                statement.setString(i + 1, params[i]);
            }
            resultSet = statement.executeQuery();
            result = new ArrayList<Map<String, String>>();
            while (resultSet.next()) {
                HashMap<String, String> rowMap = new HashMap<String, String>();
                for (int i = 1; i <= resultSet.getMetaData().getColumnCount(); ++i) {
                    rowMap.put(resultSet.getMetaData().getColumnLabel(i), resultSet.getString(i));
                }
                result.add(rowMap);
            }
        }
        catch (Throwable throwable) {
            JdbcUtils.closeResultSet(resultSet);
            JdbcUtils.closeStatement(statement);
            throw throwable;
        }
        JdbcUtils.closeResultSet(resultSet);
        JdbcUtils.closeStatement(statement);
        return result;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public List<String> queryForStringList(String query, String ... params) throws SQLException {
        ArrayList<String> result;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            statement = this.connection.prepareStatement(query);
            for (int i = 0; i < params.length; ++i) {
                statement.setString(i + 1, params[i]);
            }
            resultSet = statement.executeQuery();
            result = new ArrayList<String>();
            while (resultSet.next()) {
                result.add(resultSet.getString(1));
            }
        }
        catch (Throwable throwable) {
            JdbcUtils.closeResultSet(resultSet);
            JdbcUtils.closeStatement(statement);
            throw throwable;
        }
        JdbcUtils.closeResultSet(resultSet);
        JdbcUtils.closeStatement(statement);
        return result;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public int queryForInt(String query, String ... params) throws SQLException {
        int result;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            statement = this.connection.prepareStatement(query);
            for (int i = 0; i < params.length; ++i) {
                statement.setString(i + 1, params[i]);
            }
            resultSet = statement.executeQuery();
            resultSet.next();
            result = resultSet.getInt(1);
        }
        catch (Throwable throwable) {
            JdbcUtils.closeResultSet(resultSet);
            JdbcUtils.closeStatement(statement);
            throw throwable;
        }
        JdbcUtils.closeResultSet(resultSet);
        JdbcUtils.closeStatement(statement);
        return result;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public String queryForString(String query, String ... params) throws SQLException {
        String result;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            statement = this.connection.prepareStatement(query);
            for (int i = 0; i < params.length; ++i) {
                statement.setString(i + 1, params[i]);
            }
            resultSet = statement.executeQuery();
            result = null;
            if (resultSet.next()) {
                result = resultSet.getString(1);
            }
        }
        catch (Throwable throwable) {
            JdbcUtils.closeResultSet(resultSet);
            JdbcUtils.closeStatement(statement);
            throw throwable;
        }
        JdbcUtils.closeResultSet(resultSet);
        JdbcUtils.closeStatement(statement);
        return result;
    }

    public DatabaseMetaData getMetaData() throws SQLException {
        return this.connection.getMetaData();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void execute(String sql, Object ... params) throws SQLException {
        PreparedStatement statement = null;
        try {
            statement = this.prepareStatement(sql, params);
            statement.execute();
        }
        catch (Throwable throwable) {
            JdbcUtils.closeStatement(statement);
            throw throwable;
        }
        JdbcUtils.closeStatement(statement);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void executeStatement(String sql) throws SQLException {
        Statement statement = null;
        try {
            statement = this.connection.createStatement();
            statement.execute(sql);
            for (SQLWarning warning = statement.getWarnings(); warning != null; warning = warning.getNextWarning()) {
                LOG.warn(warning.getMessage() + " (SQL State: " + warning.getSQLState() + " - Error Code: " + warning.getErrorCode() + ")");
            }
        }
        finally {
            JdbcUtils.closeStatement(statement);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void update(String sql, Object ... params) throws SQLException {
        PreparedStatement statement = null;
        try {
            statement = this.prepareStatement(sql, params);
            statement.executeUpdate();
        }
        catch (Throwable throwable) {
            JdbcUtils.closeStatement(statement);
            throw throwable;
        }
        JdbcUtils.closeStatement(statement);
    }

    private PreparedStatement prepareStatement(String sql, Object[] params) throws SQLException {
        PreparedStatement statement = this.connection.prepareStatement(sql);
        for (int i = 0; i < params.length; ++i) {
            if (params[i] == null) {
                statement.setNull(i + 1, this.nullType);
                continue;
            }
            if (params[i] instanceof Integer) {
                statement.setInt(i + 1, (Integer)params[i]);
                continue;
            }
            if (params[i] instanceof Boolean) {
                statement.setBoolean(i + 1, (Boolean)params[i]);
                continue;
            }
            statement.setString(i + 1, (String)params[i]);
        }
        return statement;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public <T> List<T> query(String query, RowMapper<T> rowMapper) throws SQLException {
        ArrayList<T> results;
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            statement = this.connection.createStatement();
            resultSet = statement.executeQuery(query);
            results = new ArrayList<T>();
            while (resultSet.next()) {
                results.add(rowMapper.mapRow(resultSet));
            }
        }
        catch (Throwable throwable) {
            JdbcUtils.closeResultSet(resultSet);
            JdbcUtils.closeStatement(statement);
            throw throwable;
        }
        JdbcUtils.closeResultSet(resultSet);
        JdbcUtils.closeStatement(statement);
        return results;
    }
}

