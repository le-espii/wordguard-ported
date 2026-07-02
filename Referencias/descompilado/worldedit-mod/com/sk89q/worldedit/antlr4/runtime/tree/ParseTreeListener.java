/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.antlr4.runtime.tree;

import com.sk89q.worldedit.antlr4.runtime.ParserRuleContext;
import com.sk89q.worldedit.antlr4.runtime.tree.ErrorNode;
import com.sk89q.worldedit.antlr4.runtime.tree.TerminalNode;

public interface ParseTreeListener {
    public void visitTerminal(TerminalNode var1);

    public void visitErrorNode(ErrorNode var1);

    public void enterEveryRule(ParserRuleContext var1);

    public void exitEveryRule(ParserRuleContext var1);
}

