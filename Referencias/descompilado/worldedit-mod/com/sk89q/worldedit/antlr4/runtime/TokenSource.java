/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.antlr4.runtime;

import com.sk89q.worldedit.antlr4.runtime.CharStream;
import com.sk89q.worldedit.antlr4.runtime.Token;
import com.sk89q.worldedit.antlr4.runtime.TokenFactory;

public interface TokenSource {
    public Token nextToken();

    public int getLine();

    public int getCharPositionInLine();

    public CharStream getInputStream();

    public String getSourceName();

    public void setTokenFactory(TokenFactory<?> var1);

    public TokenFactory<?> getTokenFactory();
}

