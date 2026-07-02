/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.antlr4.runtime;

import com.sk89q.worldedit.antlr4.runtime.ParserRuleContext;

public class InterpreterRuleContext
extends ParserRuleContext {
    protected int ruleIndex = -1;

    public InterpreterRuleContext() {
    }

    public InterpreterRuleContext(ParserRuleContext parent, int invokingStateNumber, int ruleIndex) {
        super(parent, invokingStateNumber);
        this.ruleIndex = ruleIndex;
    }

    @Override
    public int getRuleIndex() {
        return this.ruleIndex;
    }
}

