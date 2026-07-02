/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.antlr4.runtime.atn;

import com.sk89q.worldedit.antlr4.runtime.atn.DecisionState;
import com.sk89q.worldedit.antlr4.runtime.atn.StarLoopbackState;

public final class StarLoopEntryState
extends DecisionState {
    public StarLoopbackState loopBackState;
    public boolean isPrecedenceDecision;

    @Override
    public int getStateType() {
        return 10;
    }
}

