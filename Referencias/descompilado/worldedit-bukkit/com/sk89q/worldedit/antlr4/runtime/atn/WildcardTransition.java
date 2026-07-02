/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.antlr4.runtime.atn;

import com.sk89q.worldedit.antlr4.runtime.atn.ATNState;
import com.sk89q.worldedit.antlr4.runtime.atn.Transition;

public final class WildcardTransition
extends Transition {
    public WildcardTransition(ATNState target) {
        super(target);
    }

    @Override
    public int getSerializationType() {
        return 9;
    }

    @Override
    public boolean matches(int symbol, int minVocabSymbol, int maxVocabSymbol) {
        return symbol >= minVocabSymbol && symbol <= maxVocabSymbol;
    }

    public String toString() {
        return ".";
    }
}

