/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.antlr4.runtime.atn;

import com.sk89q.worldedit.antlr4.runtime.atn.ATNState;

public abstract class DecisionState
extends ATNState {
    public int decision = -1;
    public boolean nonGreedy;
}

