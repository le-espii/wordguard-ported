/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.antlr4.runtime.tree;

import com.sk89q.worldedit.antlr4.runtime.Token;
import com.sk89q.worldedit.antlr4.runtime.tree.ErrorNode;
import com.sk89q.worldedit.antlr4.runtime.tree.ParseTreeVisitor;
import com.sk89q.worldedit.antlr4.runtime.tree.TerminalNodeImpl;

public class ErrorNodeImpl
extends TerminalNodeImpl
implements ErrorNode {
    public ErrorNodeImpl(Token token) {
        super(token);
    }

    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
        return visitor.visitErrorNode(this);
    }
}

