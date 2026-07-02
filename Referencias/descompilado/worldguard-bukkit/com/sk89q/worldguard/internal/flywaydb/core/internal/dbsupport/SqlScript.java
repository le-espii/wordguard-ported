/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldguard.internal.flywaydb.core.internal.dbsupport;

import com.sk89q.worldguard.internal.flywaydb.core.api.FlywayException;
import com.sk89q.worldguard.internal.flywaydb.core.internal.dbsupport.DbSupport;
import com.sk89q.worldguard.internal.flywaydb.core.internal.dbsupport.Delimiter;
import com.sk89q.worldguard.internal.flywaydb.core.internal.dbsupport.FlywaySqlScriptException;
import com.sk89q.worldguard.internal.flywaydb.core.internal.dbsupport.JdbcTemplate;
import com.sk89q.worldguard.internal.flywaydb.core.internal.dbsupport.SqlStatement;
import com.sk89q.worldguard.internal.flywaydb.core.internal.dbsupport.SqlStatementBuilder;
import com.sk89q.worldguard.internal.flywaydb.core.internal.util.StringUtils;
import com.sk89q.worldguard.internal.flywaydb.core.internal.util.logging.Log;
import com.sk89q.worldguard.internal.flywaydb.core.internal.util.logging.LogFactory;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SqlScript {
    private static final Log LOG = LogFactory.getLog(SqlScript.class);
    private final DbSupport dbSupport;
    private final List<SqlStatement> sqlStatements;

    public SqlScript(String sqlScriptSource, DbSupport dbSupport) {
        this.dbSupport = dbSupport;
        this.sqlStatements = this.parse(sqlScriptSource);
    }

    SqlScript(DbSupport dbSupport) {
        this.dbSupport = dbSupport;
        this.sqlStatements = null;
    }

    public List<SqlStatement> getSqlStatements() {
        return this.sqlStatements;
    }

    public void execute(JdbcTemplate jdbcTemplate) {
        for (SqlStatement sqlStatement : this.sqlStatements) {
            String sql = sqlStatement.getSql();
            LOG.debug("Executing SQL: " + sql);
            try {
                jdbcTemplate.executeStatement(sql);
            }
            catch (SQLException e) {
                throw new FlywaySqlScriptException(sqlStatement.getLineNumber(), sql, e);
            }
        }
    }

    List<SqlStatement> parse(String sqlScriptSource) {
        return this.linesToStatements(this.readLines(new StringReader(sqlScriptSource)));
    }

    List<SqlStatement> linesToStatements(List<String> lines) {
        ArrayList<SqlStatement> statements = new ArrayList<SqlStatement>();
        boolean inMultilineComment = false;
        Delimiter nonStandardDelimiter = null;
        SqlStatementBuilder sqlStatementBuilder = this.dbSupport.createSqlStatementBuilder();
        for (int lineNumber = 1; lineNumber <= lines.size(); ++lineNumber) {
            String line = lines.get(lineNumber - 1);
            if (sqlStatementBuilder.isEmpty()) {
                Delimiter newDelimiter;
                if (!StringUtils.hasText(line)) continue;
                String trimmedLine = line.trim();
                if (!sqlStatementBuilder.isCommentDirective(trimmedLine)) {
                    if (trimmedLine.startsWith("/*")) {
                        inMultilineComment = true;
                    }
                    if (inMultilineComment) {
                        if (!trimmedLine.endsWith("*/")) continue;
                        inMultilineComment = false;
                        continue;
                    }
                    if (sqlStatementBuilder.isSingleLineComment(trimmedLine)) continue;
                }
                if ((newDelimiter = sqlStatementBuilder.extractNewDelimiterFromLine(line)) != null) {
                    nonStandardDelimiter = newDelimiter;
                    continue;
                }
                sqlStatementBuilder.setLineNumber(lineNumber);
                if (nonStandardDelimiter != null) {
                    sqlStatementBuilder.setDelimiter(nonStandardDelimiter);
                }
            }
            sqlStatementBuilder.addLine(line);
            if (!sqlStatementBuilder.isTerminated()) continue;
            SqlStatement sqlStatement = sqlStatementBuilder.getSqlStatement();
            statements.add(sqlStatement);
            LOG.debug("Found statement at line " + sqlStatement.getLineNumber() + ": " + sqlStatement.getSql());
            sqlStatementBuilder = this.dbSupport.createSqlStatementBuilder();
        }
        if (!sqlStatementBuilder.isEmpty()) {
            statements.add(sqlStatementBuilder.getSqlStatement());
        }
        return statements;
    }

    private List<String> readLines(Reader reader) {
        ArrayList<String> lines = new ArrayList<String>();
        BufferedReader bufferedReader = new BufferedReader(reader);
        try {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                lines.add(line);
            }
        }
        catch (IOException e) {
            throw new FlywayException("Cannot parse lines", e);
        }
        return lines;
    }
}

