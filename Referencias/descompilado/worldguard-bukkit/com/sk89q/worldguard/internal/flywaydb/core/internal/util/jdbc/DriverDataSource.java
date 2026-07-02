/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldguard.internal.flywaydb.core.internal.util.jdbc;

import com.sk89q.worldguard.internal.flywaydb.core.api.FlywayException;
import com.sk89q.worldguard.internal.flywaydb.core.internal.util.ClassUtils;
import com.sk89q.worldguard.internal.flywaydb.core.internal.util.FeatureDetector;
import com.sk89q.worldguard.internal.flywaydb.core.internal.util.StringUtils;
import com.sk89q.worldguard.internal.flywaydb.core.internal.util.jdbc.JdbcUtils;
import java.io.PrintWriter;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.logging.Logger;
import javax.sql.DataSource;

public class DriverDataSource
implements DataSource {
    private final Driver driver;
    private final String url;
    private final String user;
    private final String password;
    private final String[] initSqls;
    private final ClassLoader classLoader;
    private boolean singleConnectionMode;
    private Connection singleConnection;

    public DriverDataSource(ClassLoader classLoader, String driverClass, String url, String user, String password, String ... initSqls) throws FlywayException {
        if (!StringUtils.hasText(url)) {
            throw new FlywayException("Missing required JDBC URL. Unable to create DataSource!");
        }
        if (!url.toLowerCase().startsWith("jdbc:")) {
            throw new FlywayException("Invalid JDBC URL (should start with jdbc:) : " + url);
        }
        this.classLoader = classLoader;
        this.url = url;
        if (!StringUtils.hasLength(driverClass) && !StringUtils.hasLength(driverClass = this.detectDriverForUrl(url))) {
            throw new FlywayException("Unable to autodetect Jdbc driver for url: " + url);
        }
        try {
            this.driver = (Driver)ClassUtils.instantiate(driverClass, classLoader);
        }
        catch (Exception e) {
            throw new FlywayException("Unable to instantiate jdbc driver: " + driverClass, e);
        }
        this.user = user;
        this.password = password;
        this.initSqls = initSqls;
    }

    private String detectDriverForUrl(String url) {
        if (url.startsWith("jdbc:db2:")) {
            return "com.ibm.db2.jcc.DB2Driver";
        }
        if (url.startsWith("jdbc:derby:")) {
            return "org.apache.derby.jdbc.EmbeddedDriver";
        }
        if (url.startsWith("jdbc:h2:")) {
            return "org.h2.Driver";
        }
        if (url.startsWith("jdbc:hsqldb:")) {
            return "org.hsqldb.jdbcDriver";
        }
        if (url.startsWith("jdbc:sqlite:")) {
            this.singleConnectionMode = true;
            if (new FeatureDetector(this.classLoader).isAndroidAvailable()) {
                return "org.sqldroid.SQLDroidDriver";
            }
            return "org.sqlite.JDBC";
        }
        if (url.startsWith("jdbc:sqldroid:")) {
            return "org.sqldroid.SQLDroidDriver";
        }
        if (url.startsWith("jdbc:mysql:")) {
            return "com.mysql.jdbc.Driver";
        }
        if (url.startsWith("jdbc:mariadb:")) {
            return "org.mariadb.jdbc.Driver";
        }
        if (url.startsWith("jdbc:google:")) {
            return "com.google.appengine.api.rdbms.AppEngineDriver";
        }
        if (url.startsWith("jdbc:oracle:")) {
            return "oracle.jdbc.OracleDriver";
        }
        if (url.startsWith("jdbc:postgresql:")) {
            return "org.postgresql.Driver";
        }
        if (url.startsWith("jdbc:jtds:")) {
            return "net.sourceforge.jtds.jdbc.Driver";
        }
        if (url.startsWith("jdbc:sqlserver:")) {
            return "com.microsoft.sqlserver.jdbc.SQLServerDriver";
        }
        return null;
    }

    public Driver getDriver() {
        return this.driver;
    }

    public String getUrl() {
        return this.url;
    }

    public String getUser() {
        return this.user;
    }

    public String getPassword() {
        return this.password;
    }

    public String[] getInitSqls() {
        return this.initSqls;
    }

    @Override
    public Connection getConnection() throws SQLException {
        return this.getConnectionFromDriver(this.getUser(), this.getPassword());
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        return this.getConnectionFromDriver(username, password);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected Connection getConnectionFromDriver(String username, String password) throws SQLException {
        Connection connection;
        if (this.singleConnectionMode && this.singleConnection != null) {
            return this.singleConnection;
        }
        Properties props = new Properties();
        if (username != null) {
            props.setProperty("user", username);
        }
        if (password != null) {
            props.setProperty("password", password);
        }
        try {
            connection = this.driver.connect(this.url, props);
        }
        catch (SQLException e) {
            throw new FlywayException("Unable to obtain Jdbc connection from DataSource (" + this.url + ") for user '" + this.user + "'", e);
        }
        for (String initSql : this.initSqls) {
            Statement statement = null;
            try {
                statement = connection.createStatement();
                statement.execute(initSql);
            }
            finally {
                JdbcUtils.closeStatement(statement);
            }
        }
        if (this.singleConnectionMode) {
            SuppressCloseHandler suppressCloseHandler = new SuppressCloseHandler(connection);
            this.singleConnection = (Connection)Proxy.newProxyInstance(this.classLoader, new Class[]{Connection.class}, suppressCloseHandler);
        }
        return connection;
    }

    @Override
    public int getLoginTimeout() throws SQLException {
        return 0;
    }

    @Override
    public void setLoginTimeout(int timeout) throws SQLException {
        throw new UnsupportedOperationException("setLoginTimeout");
    }

    @Override
    public PrintWriter getLogWriter() {
        throw new UnsupportedOperationException("getLogWriter");
    }

    @Override
    public void setLogWriter(PrintWriter pw) throws SQLException {
        throw new UnsupportedOperationException("setLogWriter");
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        throw new UnsupportedOperationException("unwrap");
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return DataSource.class.equals(iface);
    }

    @Override
    public Logger getParentLogger() {
        throw new UnsupportedOperationException("getParentLogger");
    }

    public void close() {
        JdbcUtils.closeConnection(this.singleConnection);
        this.singleConnection = null;
    }

    private static class SuppressCloseHandler
    implements InvocationHandler {
        private final Connection connection;

        public SuppressCloseHandler(Connection connection) {
            this.connection = connection;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if (!"close".equals(method.getName())) {
                return method.invoke(this.connection, args);
            }
            return null;
        }
    }
}

