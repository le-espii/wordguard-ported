/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.antlr4.runtime;

import com.sk89q.worldedit.antlr4.runtime.CharStream;
import com.sk89q.worldedit.antlr4.runtime.Token;
import com.sk89q.worldedit.antlr4.runtime.TokenSource;
import com.sk89q.worldedit.antlr4.runtime.misc.Pair;

public interface TokenFactory<Symbol extends Token> {
    public Symbol create(Pair<TokenSource, CharStream> var1, int var2, String var3, int var4, int var5, int var6, int var7, int var8);

    public Symbol create(int var1, String var2);
}

