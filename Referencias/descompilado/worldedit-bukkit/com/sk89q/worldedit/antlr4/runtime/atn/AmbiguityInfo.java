/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.antlr4.runtime.atn;

import com.sk89q.worldedit.antlr4.runtime.TokenStream;
import com.sk89q.worldedit.antlr4.runtime.atn.ATNConfigSet;
import com.sk89q.worldedit.antlr4.runtime.atn.DecisionEventInfo;
import java.util.BitSet;

public class AmbiguityInfo
extends DecisionEventInfo {
    public BitSet ambigAlts;

    public AmbiguityInfo(int decision, ATNConfigSet configs, BitSet ambigAlts, TokenStream input, int startIndex, int stopIndex, boolean fullCtx) {
        super(decision, configs, input, startIndex, stopIndex, fullCtx);
        this.ambigAlts = ambigAlts;
    }
}

