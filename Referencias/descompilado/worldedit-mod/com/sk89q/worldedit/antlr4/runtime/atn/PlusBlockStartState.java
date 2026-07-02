/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.antlr4.runtime.atn;

import com.sk89q.worldedit.antlr4.runtime.atn.BlockStartState;
import com.sk89q.worldedit.antlr4.runtime.atn.PlusLoopbackState;

public final class PlusBlockStartState
extends BlockStartState {
    public PlusLoopbackState loopBackState;

    @Override
    public int getStateType() {
        return 4;
    }
}

