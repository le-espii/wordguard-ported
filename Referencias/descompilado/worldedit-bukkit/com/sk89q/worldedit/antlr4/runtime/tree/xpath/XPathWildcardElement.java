/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.antlr4.runtime.tree.xpath;

import com.sk89q.worldedit.antlr4.runtime.tree.ParseTree;
import com.sk89q.worldedit.antlr4.runtime.tree.Tree;
import com.sk89q.worldedit.antlr4.runtime.tree.Trees;
import com.sk89q.worldedit.antlr4.runtime.tree.xpath.XPathElement;
import java.util.ArrayList;
import java.util.Collection;

public class XPathWildcardElement
extends XPathElement {
    public XPathWildcardElement() {
        super("*");
    }

    @Override
    public Collection<ParseTree> evaluate(ParseTree t) {
        if (this.invert) {
            return new ArrayList<ParseTree>();
        }
        ArrayList<ParseTree> kids = new ArrayList<ParseTree>();
        for (Tree c : Trees.getChildren(t)) {
            kids.add((ParseTree)c);
        }
        return kids;
    }
}

