/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.base.Preconditions
 *  com.google.common.base.Throwables
 */
package com.sk89q.worldguard.util.io;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;
import java.io.Closeable;
import java.io.IOException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class Closer
implements Closeable {
    private static final Logger logger = Logger.getLogger(Closer.class.getCanonicalName());
    private static final Suppressor SUPPRESSOR = SuppressingSuppressor.isAvailable() ? SuppressingSuppressor.INSTANCE : LoggingSuppressor.INSTANCE;
    @VisibleForTesting
    final Suppressor suppressor;
    private final Deque<Closeable> stack = new ArrayDeque<Closeable>(4);
    private Throwable thrown;

    public static Closer create() {
        return new Closer(SUPPRESSOR);
    }

    @VisibleForTesting
    Closer(Suppressor suppressor) {
        this.suppressor = (Suppressor)Preconditions.checkNotNull((Object)suppressor);
    }

    public <C extends Closeable> C register(C closeable) {
        this.stack.push(closeable);
        return closeable;
    }

    public <C extends Connection> C register(final C connection) {
        this.register(new Closeable(){

            @Override
            public void close() throws IOException {
                try {
                    connection.close();
                }
                catch (SQLException e) {
                    throw new IOException("Failed to close", e);
                }
            }
        });
        return connection;
    }

    public <C extends Statement> C register(final C statement) {
        this.register(new Closeable(){

            @Override
            public void close() throws IOException {
                try {
                    statement.close();
                }
                catch (SQLException e) {
                    throw new IOException("Failed to close", e);
                }
            }
        });
        return statement;
    }

    public <C extends ResultSet> C register(final C resultSet) {
        this.register(new Closeable(){

            @Override
            public void close() throws IOException {
                try {
                    resultSet.close();
                }
                catch (SQLException e) {
                    throw new IOException("Failed to close", e);
                }
            }
        });
        return resultSet;
    }

    public RuntimeException rethrow(Throwable e) throws IOException {
        this.thrown = e;
        Throwables.propagateIfPossible((Throwable)e, IOException.class);
        throw Throwables.propagate((Throwable)e);
    }

    public <X extends Exception> RuntimeException rethrow(Throwable e, Class<X> declaredType) throws IOException, X {
        this.thrown = e;
        Throwables.propagateIfPossible((Throwable)e, IOException.class);
        Throwables.propagateIfPossible((Throwable)e, declaredType);
        throw Throwables.propagate((Throwable)e);
    }

    public <X1 extends Exception, X2 extends Exception> RuntimeException rethrow(Throwable e, Class<X1> declaredType1, Class<X2> declaredType2) throws IOException, X1, X2 {
        this.thrown = e;
        Throwables.propagateIfPossible((Throwable)e, IOException.class);
        Throwables.propagateIfPossible((Throwable)e, declaredType1, declaredType2);
        throw Throwables.propagate((Throwable)e);
    }

    @Override
    public void close() throws IOException {
        Throwable throwable = this.thrown;
        while (!this.stack.isEmpty()) {
            Closeable closeable = this.stack.pop();
            try {
                closeable.close();
            }
            catch (Throwable e) {
                if (throwable == null) {
                    throwable = e;
                    continue;
                }
                this.suppressor.suppress(closeable, throwable, e);
            }
        }
        if (this.thrown == null && throwable != null) {
            Throwables.propagateIfPossible((Throwable)throwable, IOException.class);
            throw new AssertionError((Object)throwable);
        }
    }

    public void closeQuietly() {
        try {
            this.close();
        }
        catch (IOException iOException) {
            // empty catch block
        }
    }

    @VisibleForTesting
    static interface Suppressor {
        public void suppress(Closeable var1, Throwable var2, Throwable var3);
    }

    @VisibleForTesting
    static final class SuppressingSuppressor
    implements Suppressor {
        static final SuppressingSuppressor INSTANCE = new SuppressingSuppressor();
        static final Method addSuppressed = SuppressingSuppressor.getAddSuppressed();

        SuppressingSuppressor() {
        }

        static boolean isAvailable() {
            return addSuppressed != null;
        }

        private static Method getAddSuppressed() {
            try {
                return Throwable.class.getMethod("addSuppressed", Throwable.class);
            }
            catch (Throwable e) {
                return null;
            }
        }

        @Override
        public void suppress(Closeable closeable, Throwable thrown, Throwable suppressed) {
            if (thrown == suppressed) {
                return;
            }
            try {
                addSuppressed.invoke(thrown, suppressed);
            }
            catch (Throwable e) {
                LoggingSuppressor.INSTANCE.suppress(closeable, thrown, suppressed);
            }
        }
    }

    @VisibleForTesting
    static final class LoggingSuppressor
    implements Suppressor {
        static final LoggingSuppressor INSTANCE = new LoggingSuppressor();

        LoggingSuppressor() {
        }

        @Override
        public void suppress(Closeable closeable, Throwable thrown, Throwable suppressed) {
            logger.log(Level.WARNING, "Suppressing exception thrown when closing " + String.valueOf(closeable), suppressed);
        }
    }
}

