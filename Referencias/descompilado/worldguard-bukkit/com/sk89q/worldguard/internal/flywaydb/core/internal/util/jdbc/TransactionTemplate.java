/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldguard.internal.flywaydb.core.internal.util.jdbc;

import com.sk89q.worldguard.internal.flywaydb.core.api.FlywayException;
import com.sk89q.worldguard.internal.flywaydb.core.internal.util.jdbc.TransactionCallback;
import com.sk89q.worldguard.internal.flywaydb.core.internal.util.logging.Log;
import com.sk89q.worldguard.internal.flywaydb.core.internal.util.logging.LogFactory;
import java.sql.Connection;
import java.sql.SQLException;

public class TransactionTemplate {
    private static final Log LOG = LogFactory.getLog(TransactionTemplate.class);
    private final Connection connection;
    private final boolean rollbackOnException;

    public TransactionTemplate(Connection connection) {
        this(connection, true);
    }

    public TransactionTemplate(Connection connection, boolean rollbackOnException) {
        this.connection = connection;
        this.rollbackOnException = rollbackOnException;
    }

    public <T> T execute(TransactionCallback<T> transactionCallback) {
        boolean oldAutocommit = true;
        try {
            oldAutocommit = this.connection.getAutoCommit();
            this.connection.setAutoCommit(false);
            T result = transactionCallback.doInTransaction();
            this.connection.commit();
            T t = result;
            return t;
        }
        catch (SQLException e) {
            throw new FlywayException("Unable to commit transaction", e);
        }
        catch (RuntimeException e) {
            if (this.rollbackOnException) {
                try {
                    LOG.debug("Rolling back transaction...");
                    this.connection.rollback();
                    LOG.debug("Transaction rolled back");
                }
                catch (SQLException se) {
                    LOG.error("Unable to rollback transaction", se);
                }
            } else {
                try {
                    this.connection.commit();
                }
                catch (SQLException se) {
                    LOG.error("Unable to commit transaction", se);
                }
            }
            throw e;
        }
        finally {
            try {
                this.connection.setAutoCommit(oldAutocommit);
            }
            catch (SQLException e) {
                LOG.error("Unable to restore autocommit to original value for connection", e);
            }
        }
    }
}

