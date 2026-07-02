/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.antlr4.runtime.atn;

import com.sk89q.worldedit.antlr4.runtime.Lexer;
import com.sk89q.worldedit.antlr4.runtime.atn.LexerActionType;

public interface LexerAction {
    public LexerActionType getActionType();

    public boolean isPositionDependent();

    public void execute(Lexer var1);
}

