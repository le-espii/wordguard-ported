/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.antlr4.runtime.atn;

import com.sk89q.worldedit.antlr4.runtime.atn.ATNState;
import com.sk89q.worldedit.antlr4.runtime.atn.RuleStopState;

public final class RuleStartState
extends ATNState {
    public RuleStopState stopState;
    public boolean isLeftRecursiveRule;

    @Override
    public int getStateType() {
        return 2;
    }
}

