/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.antlr4.runtime.atn;

import com.sk89q.worldedit.antlr4.runtime.TokenStream;
import com.sk89q.worldedit.antlr4.runtime.atn.ATNConfigSet;
import com.sk89q.worldedit.antlr4.runtime.atn.DecisionEventInfo;

public class ContextSensitivityInfo
extends DecisionEventInfo {
    public ContextSensitivityInfo(int decision, ATNConfigSet configs, TokenStream input, int startIndex, int stopIndex) {
        super(decision, configs, input, startIndex, stopIndex, true);
    }
}

