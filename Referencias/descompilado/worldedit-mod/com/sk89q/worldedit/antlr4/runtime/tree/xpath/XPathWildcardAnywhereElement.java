/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.antlr4.runtime.tree.xpath;

import com.sk89q.worldedit.antlr4.runtime.tree.ParseTree;
import com.sk89q.worldedit.antlr4.runtime.tree.Trees;
import com.sk89q.worldedit.antlr4.runtime.tree.xpath.XPathElement;
import java.util.ArrayList;
import java.util.Collection;

public class XPathWildcardAnywhereElement
extends XPathElement {
    public XPathWildcardAnywhereElement() {
        super("*");
    }

    @Override
    public Collection<ParseTree> evaluate(ParseTree t) {
        if (this.invert) {
            return new ArrayList<ParseTree>();
        }
        return Trees.getDescendants(t);
    }
}

