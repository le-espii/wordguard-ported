/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.antlr4.runtime.atn;

import com.sk89q.worldedit.antlr4.runtime.atn.EmptyPredictionContext;
import com.sk89q.worldedit.antlr4.runtime.atn.PredictionContext;
import java.util.HashMap;
import java.util.Map;

public class PredictionContextCache {
    protected final Map<PredictionContext, PredictionContext> cache = new HashMap<PredictionContext, PredictionContext>();

    public PredictionContext add(PredictionContext ctx) {
        if (ctx == EmptyPredictionContext.Instance) {
            return EmptyPredictionContext.Instance;
        }
        PredictionContext existing = this.cache.get(ctx);
        if (existing != null) {
            return existing;
        }
        this.cache.put(ctx, ctx);
        return ctx;
    }

    public PredictionContext get(PredictionContext ctx) {
        return this.cache.get(ctx);
    }

    public int size() {
        return this.cache.size();
    }
}

