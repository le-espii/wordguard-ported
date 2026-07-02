/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.antlr4.runtime.tree;

import com.sk89q.worldedit.antlr4.runtime.misc.Interval;
import com.sk89q.worldedit.antlr4.runtime.tree.Tree;

public interface SyntaxTree
extends Tree {
    public Interval getSourceInterval();
}

