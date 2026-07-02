/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.antlr4.runtime;

import com.sk89q.worldedit.antlr4.runtime.IntStream;
import com.sk89q.worldedit.antlr4.runtime.ParserRuleContext;
import com.sk89q.worldedit.antlr4.runtime.Recognizer;
import com.sk89q.worldedit.antlr4.runtime.RuleContext;
import com.sk89q.worldedit.antlr4.runtime.Token;
import com.sk89q.worldedit.antlr4.runtime.misc.IntervalSet;

public class RecognitionException
extends RuntimeException {
    private final Recognizer<?, ?> recognizer;
    private final RuleContext ctx;
    private final IntStream input;
    private Token offendingToken;
    private int offendingState = -1;

    public RecognitionException(Recognizer<?, ?> recognizer, IntStream input, ParserRuleContext ctx) {
        this.recognizer = recognizer;
        this.input = input;
        this.ctx = ctx;
        if (recognizer != null) {
            this.offendingState = recognizer.getState();
        }
    }

    public RecognitionException(String message, Recognizer<?, ?> recognizer, IntStream input, ParserRuleContext ctx) {
        super(message);
        this.recognizer = recognizer;
        this.input = input;
        this.ctx = ctx;
        if (recognizer != null) {
            this.offendingState = recognizer.getState();
        }
    }

    public int getOffendingState() {
        return this.offendingState;
    }

    protected final void setOffendingState(int offendingState) {
        this.offendingState = offendingState;
    }

    public IntervalSet getExpectedTokens() {
        if (this.recognizer != null) {
            return this.recognizer.getATN().getExpectedTokens(this.offendingState, this.ctx);
        }
        return null;
    }

    public RuleContext getCtx() {
        return this.ctx;
    }

    public IntStream getInputStream() {
        return this.input;
    }

    public Token getOffendingToken() {
        return this.offendingToken;
    }

    protected final void setOffendingToken(Token offendingToken) {
        this.offendingToken = offendingToken;
    }

    public Recognizer<?, ?> getRecognizer() {
        return this.recognizer;
    }
}

