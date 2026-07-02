/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.antlr4.runtime.tree;

import com.sk89q.worldedit.antlr4.runtime.Token;
import com.sk89q.worldedit.antlr4.runtime.tree.ParseTree;

public interface TerminalNode
extends ParseTree {
    public Token getSymbol();
}

