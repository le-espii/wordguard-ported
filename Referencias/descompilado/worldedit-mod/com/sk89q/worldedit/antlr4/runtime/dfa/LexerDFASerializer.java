/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.antlr4.runtime.dfa;

import com.sk89q.worldedit.antlr4.runtime.VocabularyImpl;
import com.sk89q.worldedit.antlr4.runtime.dfa.DFA;
import com.sk89q.worldedit.antlr4.runtime.dfa.DFASerializer;

public class LexerDFASerializer
extends DFASerializer {
    public LexerDFASerializer(DFA dfa) {
        super(dfa, VocabularyImpl.EMPTY_VOCABULARY);
    }

    @Override
    protected String getEdgeLabel(int i) {
        return new StringBuilder("'").appendCodePoint(i).append("'").toString();
    }
}

