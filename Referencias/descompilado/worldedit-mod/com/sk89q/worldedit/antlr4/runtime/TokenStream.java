/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.antlr4.runtime;

import com.sk89q.worldedit.antlr4.runtime.IntStream;
import com.sk89q.worldedit.antlr4.runtime.RuleContext;
import com.sk89q.worldedit.antlr4.runtime.Token;
import com.sk89q.worldedit.antlr4.runtime.TokenSource;
import com.sk89q.worldedit.antlr4.runtime.misc.Interval;

public interface TokenStream
extends IntStream {
    public Token LT(int var1);

    public Token get(int var1);

    public TokenSource getTokenSource();

    public String getText(Interval var1);

    public String getText();

    public String getText(RuleContext var1);

    public String getText(Token var1, Token var2);
}

