/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.antlr4.runtime.atn;

import com.sk89q.worldedit.antlr4.runtime.TokenStream;
import com.sk89q.worldedit.antlr4.runtime.atn.ATNConfigSet;
import com.sk89q.worldedit.antlr4.runtime.atn.DecisionEventInfo;

public class LookaheadEventInfo
extends DecisionEventInfo {
    public int predictedAlt;

    public LookaheadEventInfo(int decision, ATNConfigSet configs, int predictedAlt, TokenStream input, int startIndex, int stopIndex, boolean fullCtx) {
        super(decision, configs, input, startIndex, stopIndex, fullCtx);
        this.predictedAlt = predictedAlt;
    }
}

