/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.antlr4.runtime.tree.pattern;

import com.sk89q.worldedit.antlr4.runtime.CommonToken;

public class TokenTagToken
extends CommonToken {
    private final String tokenName;
    private final String label;

    public TokenTagToken(String tokenName, int type) {
        this(tokenName, type, null);
    }

    public TokenTagToken(String tokenName, int type, String label) {
        super(type);
        this.tokenName = tokenName;
        this.label = label;
    }

    public final String getTokenName() {
        return this.tokenName;
    }

    public final String getLabel() {
        return this.label;
    }

    @Override
    public String getText() {
        if (this.label != null) {
            return "<" + this.label + ":" + this.tokenName + ">";
        }
        return "<" + this.tokenName + ">";
    }

    @Override
    public String toString() {
        return this.tokenName + ":" + this.type;
    }
}

