/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.antlr4.runtime.tree;

import com.sk89q.worldedit.antlr4.runtime.tree.ErrorNode;
import com.sk89q.worldedit.antlr4.runtime.tree.ParseTree;
import com.sk89q.worldedit.antlr4.runtime.tree.RuleNode;
import com.sk89q.worldedit.antlr4.runtime.tree.TerminalNode;

public interface ParseTreeVisitor<T> {
    public T visit(ParseTree var1);

    public T visitChildren(RuleNode var1);

    public T visitTerminal(TerminalNode var1);

    public T visitErrorNode(ErrorNode var1);
}

