/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.antlr4.runtime.atn;

import com.sk89q.worldedit.antlr4.runtime.atn.ATN;
import com.sk89q.worldedit.antlr4.runtime.atn.ATNConfigSet;
import com.sk89q.worldedit.antlr4.runtime.atn.PredictionContext;
import com.sk89q.worldedit.antlr4.runtime.atn.PredictionContextCache;
import com.sk89q.worldedit.antlr4.runtime.dfa.DFAState;
import java.util.IdentityHashMap;

public abstract class ATNSimulator {
    public static final DFAState ERROR = new DFAState(new ATNConfigSet());
    public final ATN atn;
    protected final PredictionContextCache sharedContextCache;

    public ATNSimulator(ATN atn, PredictionContextCache sharedContextCache) {
        this.atn = atn;
        this.sharedContextCache = sharedContextCache;
    }

    public abstract void reset();

    public void clearDFA() {
        throw new UnsupportedOperationException("This ATN simulator does not support clearing the DFA.");
    }

    public PredictionContextCache getSharedContextCache() {
        return this.sharedContextCache;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public PredictionContext getCachedContext(PredictionContext context) {
        if (this.sharedContextCache == null) {
            return context;
        }
        PredictionContextCache predictionContextCache = this.sharedContextCache;
        synchronized (predictionContextCache) {
            IdentityHashMap<PredictionContext, PredictionContext> visited = new IdentityHashMap<PredictionContext, PredictionContext>();
            return PredictionContext.getCachedContext(context, this.sharedContextCache, visited);
        }
    }

    static {
        ATNSimulator.ERROR.stateNumber = Integer.MAX_VALUE;
    }
}

