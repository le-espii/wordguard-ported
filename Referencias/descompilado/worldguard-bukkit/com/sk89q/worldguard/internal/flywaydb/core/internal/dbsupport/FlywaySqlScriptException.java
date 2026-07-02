/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldguard.internal.flywaydb.core.internal.dbsupport;

import com.sk89q.worldguard.internal.flywaydb.core.api.FlywayException;
import java.sql.SQLException;

public class FlywaySqlScriptException
extends FlywayException {
    private final int lineNumber;
    private final String statement;

    public FlywaySqlScriptException(int lineNumber, String statement, SQLException sqlException) {
        super("Error executing statement at line " + lineNumber + ": " + statement, sqlException);
        this.lineNumber = lineNumber;
        this.statement = statement;
    }

    public int getLineNumber() {
        return this.lineNumber;
    }

    public String getStatement() {
        return this.statement;
    }
}

