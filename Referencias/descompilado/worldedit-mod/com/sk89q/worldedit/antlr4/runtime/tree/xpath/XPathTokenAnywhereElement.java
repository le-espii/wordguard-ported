/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.antlr4.runtime.tree.xpath;

import com.sk89q.worldedit.antlr4.runtime.tree.ParseTree;
import com.sk89q.worldedit.antlr4.runtime.tree.Trees;
import com.sk89q.worldedit.antlr4.runtime.tree.xpath.XPathElement;
import java.util.Collection;

public class XPathTokenAnywhereElement
extends XPathElement {
    protected int tokenType;

    public XPathTokenAnywhereElement(String tokenName, int tokenType) {
        super(tokenName);
        this.tokenType = tokenType;
    }

    @Override
    public Collection<ParseTree> evaluate(ParseTree t) {
        return Trees.findAllTokenNodes(t, this.tokenType);
    }
}

