/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldguard.internal.flywaydb.core.internal.dbsupport;

import com.sk89q.worldguard.internal.flywaydb.core.internal.dbsupport.Delimiter;
import com.sk89q.worldguard.internal.flywaydb.core.internal.dbsupport.SqlStatement;
import com.sk89q.worldguard.internal.flywaydb.core.internal.util.StringUtils;
import java.util.ArrayList;
import java.util.List;

public class SqlStatementBuilder {
    private StringBuilder statement = new StringBuilder();
    private int lineNumber;
    private boolean empty = true;
    private boolean terminated;
    private boolean insideQuoteStringLiteral = false;
    private boolean insideAlternateQuoteStringLiteral = false;
    private String alternateQuote;
    private boolean insideMultiLineComment = false;
    private Delimiter delimiter = this.getDefaultDelimiter();

    protected Delimiter getDefaultDelimiter() {
        return new Delimiter(";", false);
    }

    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }

    public void setDelimiter(Delimiter delimiter) {
        this.delimiter = delimiter;
    }

    public boolean isEmpty() {
        return this.empty;
    }

    public boolean isTerminated() {
        return this.terminated;
    }

    public SqlStatement getSqlStatement() {
        return new SqlStatement(this.lineNumber, this.statement.toString());
    }

    public Delimiter extractNewDelimiterFromLine(String line) {
        return null;
    }

    public boolean isCommentDirective(String line) {
        return false;
    }

    public boolean isSingleLineComment(String line) {
        return line.startsWith("--");
    }

    public void addLine(String line) {
        if (this.isEmpty()) {
            this.empty = false;
        } else {
            this.statement.append("\n");
        }
        String lineSimplified = this.simplifyLine(line);
        if (this.endsWithOpenMultilineStringLiteral(lineSimplified)) {
            this.statement.append(line);
            return;
        }
        this.delimiter = this.changeDelimiterIfNecessary(lineSimplified, this.delimiter);
        this.statement.append(line);
        if (this.lineTerminatesStatement(lineSimplified, this.delimiter)) {
            this.statement = new StringBuilder(SqlStatementBuilder.stripDelimiter(this.statement.toString(), this.delimiter));
            this.terminated = true;
        }
    }

    protected String simplifyLine(String line) {
        return line.replaceAll("\\s+", " ").trim().toUpperCase();
    }

    protected Delimiter changeDelimiterIfNecessary(String line, Delimiter delimiter) {
        return delimiter;
    }

    private boolean lineTerminatesStatement(String line, Delimiter delimiter) {
        if (delimiter == null) {
            return false;
        }
        String upperCaseDelimiter = delimiter.getDelimiter().toUpperCase();
        if (delimiter.isAloneOnLine()) {
            return line.equals(upperCaseDelimiter);
        }
        return line.endsWith(upperCaseDelimiter);
    }

    static String stripDelimiter(String sql, Delimiter delimiter) {
        int lowerCaseIndex = sql.lastIndexOf(delimiter.getDelimiter().toLowerCase());
        int upperCaseIndex = sql.lastIndexOf(delimiter.getDelimiter().toUpperCase());
        return sql.substring(0, Math.max(lowerCaseIndex, upperCaseIndex));
    }

    protected String extractAlternateOpenQuote(String token) {
        return null;
    }

    protected String computeAlternateCloseQuote(String openQuote) {
        return openQuote;
    }

    protected boolean endsWithOpenMultilineStringLiteral(String line) {
        String[] tokens = StringUtils.tokenizeToStringArray(line, " @<>;:=|(),+{}");
        List<TokenType> delimitingTokens = this.extractStringLiteralDelimitingTokens(tokens);
        for (TokenType delimitingToken : delimitingTokens) {
            if (!this.insideQuoteStringLiteral && !this.insideAlternateQuoteStringLiteral && TokenType.MULTI_LINE_COMMENT.equals((Object)delimitingToken)) {
                boolean bl = this.insideMultiLineComment = !this.insideMultiLineComment;
            }
            if (!this.insideQuoteStringLiteral && !this.insideAlternateQuoteStringLiteral && !this.insideMultiLineComment && TokenType.SINGLE_LINE_COMMENT.equals((Object)delimitingToken)) {
                return false;
            }
            if (!this.insideMultiLineComment && !this.insideQuoteStringLiteral && TokenType.ALTERNATE_QUOTE.equals((Object)delimitingToken)) {
                boolean bl = this.insideAlternateQuoteStringLiteral = !this.insideAlternateQuoteStringLiteral;
            }
            if (this.insideMultiLineComment || this.insideAlternateQuoteStringLiteral || !TokenType.QUOTE.equals((Object)delimitingToken)) continue;
            this.insideQuoteStringLiteral = !this.insideQuoteStringLiteral;
        }
        return this.insideQuoteStringLiteral || this.insideAlternateQuoteStringLiteral;
    }

    private List<TokenType> extractStringLiteralDelimitingTokens(String[] tokens) {
        ArrayList<TokenType> delimitingTokens = new ArrayList<TokenType>();
        for (String token : tokens) {
            String alternateQuoteFromToken;
            String cleanToken = this.removeCharsetCasting(this.removeEscapedQuotes(token));
            if (this.alternateQuote == null && (alternateQuoteFromToken = this.extractAlternateOpenQuote(cleanToken)) != null) {
                String closeQuote = this.computeAlternateCloseQuote(alternateQuoteFromToken);
                if (cleanToken.length() >= alternateQuoteFromToken.length() + closeQuote.length() && cleanToken.startsWith(alternateQuoteFromToken) && cleanToken.endsWith(closeQuote)) continue;
                this.alternateQuote = closeQuote;
                delimitingTokens.add(TokenType.ALTERNATE_QUOTE);
                if (!cleanToken.startsWith("\"") || !cleanToken.endsWith("'")) continue;
                delimitingTokens.add(TokenType.QUOTE);
                continue;
            }
            if (this.alternateQuote != null && cleanToken.endsWith(this.alternateQuote)) {
                this.alternateQuote = null;
                delimitingTokens.add(TokenType.ALTERNATE_QUOTE);
                continue;
            }
            if (cleanToken.length() >= 2 && cleanToken.startsWith("'") && cleanToken.endsWith("'")) continue;
            if (cleanToken.startsWith("'") || cleanToken.endsWith("'")) {
                delimitingTokens.add(TokenType.QUOTE);
            }
            if (this.isSingleLineComment(cleanToken)) {
                delimitingTokens.add(TokenType.SINGLE_LINE_COMMENT);
            }
            if (cleanToken.length() >= 4 && cleanToken.startsWith("/*") && cleanToken.endsWith("*/") || !cleanToken.startsWith("/*") && !cleanToken.endsWith("*/")) continue;
            delimitingTokens.add(TokenType.MULTI_LINE_COMMENT);
        }
        return delimitingTokens;
    }

    protected String removeEscapedQuotes(String token) {
        return StringUtils.replaceAll(token, "''", "");
    }

    protected String removeCharsetCasting(String token) {
        return token;
    }

    private static enum TokenType {
        QUOTE,
        ALTERNATE_QUOTE,
        SINGLE_LINE_COMMENT,
        MULTI_LINE_COMMENT;

    }
}

