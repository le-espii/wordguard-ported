/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.antlr4.runtime.tree;

public interface Tree {
    public Tree getParent();

    public Object getPayload();

    public Tree getChild(int var1);

    public int getChildCount();

    public String toStringTree();
}

