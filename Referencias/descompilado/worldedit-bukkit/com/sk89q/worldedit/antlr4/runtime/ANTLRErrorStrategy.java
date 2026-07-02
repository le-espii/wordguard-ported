/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.antlr4.runtime;

import com.sk89q.worldedit.antlr4.runtime.Parser;
import com.sk89q.worldedit.antlr4.runtime.RecognitionException;
import com.sk89q.worldedit.antlr4.runtime.Token;

public interface ANTLRErrorStrategy {
    public void reset(Parser var1);

    public Token recoverInline(Parser var1) throws RecognitionException;

    public void recover(Parser var1, RecognitionException var2) throws RecognitionException;

    public void sync(Parser var1) throws RecognitionException;

    public boolean inErrorRecoveryMode(Parser var1);

    public void reportMatch(Parser var1);

    public void reportError(Parser var1, RecognitionException var2);
}

