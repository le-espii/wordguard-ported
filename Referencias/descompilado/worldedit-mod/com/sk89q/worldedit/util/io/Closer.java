/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.base.Preconditions
 *  com.google.common.base.Throwables
 *  org.apache.logging.log4j.Logger
 */
package com.sk89q.worldedit.util.io;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;
import com.sk89q.worldedit.internal.util.LogManagerCompat;
import java.io.Closeable;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.zip.ZipFile;
import org.apache.logging.log4j.Logger;

public final class Closer
implements Closeable {
    private static final Logger LOGGER = LogManagerCompat.getLogger();
    private static final Suppressor SUPPRESSOR = SuppressingSuppressor.isAvailable() ? SuppressingSuppressor.INSTANCE : LoggingSuppressor.INSTANCE;
    @VisibleForTesting
    final Suppressor suppressor;
    private final Deque<Closeable> stack = new ArrayDeque<Closeable>(4);
    private final Deque<ZipFile> zipStack = new ArrayDeque<ZipFile>(4);
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

    public <Z extends ZipFile> Z register(Z zipFile) {
        this.zipStack.push(zipFile);
        return zipFile;
    }

    public RuntimeException rethrowAndClose(Throwable t) throws IOException {
        try {
            throw this.rethrow(t);
        }
        catch (Throwable throwable) {
            try {
                this.close();
            }
            catch (Throwable closeThrown) {
                t.addSuppressed(closeThrown);
            }
            throw throwable;
        }
    }

    public RuntimeException rethrow(Throwable e) throws IOException {
        this.thrown = e;
        Throwables.propagateIfPossible((Throwable)e, IOException.class);
        Throwables.throwIfUnchecked((Throwable)e);
        throw new RuntimeException(e);
    }

    public <X extends Exception> RuntimeException rethrow(Throwable e, Class<X> declaredType) throws IOException, X {
        this.thrown = e;
        Throwables.propagateIfPossible((Throwable)e, IOException.class);
        Throwables.propagateIfPossible((Throwable)e, declaredType);
        Throwables.throwIfUnchecked((Throwable)e);
        throw new RuntimeException(e);
    }

    public <X1 extends Exception, X2 extends Exception> RuntimeException rethrow(Throwable e, Class<X1> declaredType1, Class<X2> declaredType2) throws IOException, X1, X2 {
        this.thrown = e;
        Throwables.propagateIfPossible((Throwable)e, IOException.class);
        Throwables.propagateIfPossible((Throwable)e, declaredType1, declaredType2);
        Throwables.throwIfUnchecked((Throwable)e);
        throw new RuntimeException(e);
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
        while (!this.zipStack.isEmpty()) {
            ZipFile zipFile = this.zipStack.pop();
            try {
                zipFile.close();
            }
            catch (Throwable e) {
                if (throwable == null) {
                    throwable = e;
                    continue;
                }
                this.suppressor.suppress(zipFile, throwable, e);
            }
        }
        if (this.thrown == null && throwable != null) {
            Throwables.propagateIfPossible((Throwable)throwable, IOException.class);
            throw new AssertionError((Object)throwable);
        }
    }

    @VisibleForTesting
    static interface Suppressor {
        public void suppress(Object var1, Throwable var2, Throwable var3);
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
        public void suppress(Object closeable, Throwable thrown, Throwable suppressed) {
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
        public void suppress(Object closeable, Throwable thrown, Throwable suppressed) {
            LOGGER.warn("Suppressing exception thrown when closing " + String.valueOf(closeable), suppressed);
        }
    }
}

