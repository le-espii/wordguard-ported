/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.antlr4.runtime.atn;

import com.sk89q.worldedit.antlr4.runtime.atn.ATNState;
import com.sk89q.worldedit.antlr4.runtime.atn.StarLoopEntryState;

public final class StarLoopbackState
extends ATNState {
    public final StarLoopEntryState getLoopEntryState() {
        return (StarLoopEntryState)this.transition((int)0).target;
    }

    @Override
    public int getStateType() {
        return 9;
    }
}

