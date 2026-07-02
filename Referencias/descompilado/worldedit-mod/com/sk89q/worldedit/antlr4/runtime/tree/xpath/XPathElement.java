/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.antlr4.runtime.tree.xpath;

import com.sk89q.worldedit.antlr4.runtime.tree.ParseTree;
import java.util.Collection;

public abstract class XPathElement {
    protected String nodeName;
    protected boolean invert;

    public XPathElement(String nodeName) {
        this.nodeName = nodeName;
    }

    public abstract Collection<ParseTree> evaluate(ParseTree var1);

    public String toString() {
        String inv = this.invert ? "!" : "";
        return this.getClass().getSimpleName() + "[" + inv + this.nodeName + "]";
    }
}

