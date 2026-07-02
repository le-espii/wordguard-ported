/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.antlr4.runtime.tree;

import com.sk89q.worldedit.antlr4.runtime.RuleContext;
import com.sk89q.worldedit.antlr4.runtime.tree.ParseTree;

public interface RuleNode
extends ParseTree {
    public RuleContext getRuleContext();
}

