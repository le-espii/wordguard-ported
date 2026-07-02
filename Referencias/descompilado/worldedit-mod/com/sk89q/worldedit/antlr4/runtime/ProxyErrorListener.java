/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.antlr4.runtime;

import com.sk89q.worldedit.antlr4.runtime.ANTLRErrorListener;
import com.sk89q.worldedit.antlr4.runtime.Parser;
import com.sk89q.worldedit.antlr4.runtime.RecognitionException;
import com.sk89q.worldedit.antlr4.runtime.Recognizer;
import com.sk89q.worldedit.antlr4.runtime.atn.ATNConfigSet;
import com.sk89q.worldedit.antlr4.runtime.dfa.DFA;
import java.util.BitSet;
import java.util.Collection;

public class ProxyErrorListener
implements ANTLRErrorListener {
    private final Collection<? extends ANTLRErrorListener> delegates;

    public ProxyErrorListener(Collection<? extends ANTLRErrorListener> delegates) {
        if (delegates == null) {
            throw new NullPointerException("delegates");
        }
        this.delegates = delegates;
    }

    @Override
    public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e) {
        for (ANTLRErrorListener aNTLRErrorListener : this.delegates) {
            aNTLRErrorListener.syntaxError(recognizer, offendingSymbol, line, charPositionInLine, msg, e);
        }
    }

    @Override
    public void reportAmbiguity(Parser recognizer, DFA dfa, int startIndex, int stopIndex, boolean exact, BitSet ambigAlts, ATNConfigSet configs) {
        for (ANTLRErrorListener aNTLRErrorListener : this.delegates) {
            aNTLRErrorListener.reportAmbiguity(recognizer, dfa, startIndex, stopIndex, exact, ambigAlts, configs);
        }
    }

    @Override
    public void reportAttemptingFullContext(Parser recognizer, DFA dfa, int startIndex, int stopIndex, BitSet conflictingAlts, ATNConfigSet configs) {
        for (ANTLRErrorListener aNTLRErrorListener : this.delegates) {
            aNTLRErrorListener.reportAttemptingFullContext(recognizer, dfa, startIndex, stopIndex, conflictingAlts, configs);
        }
    }

    @Override
    public void reportContextSensitivity(Parser recognizer, DFA dfa, int startIndex, int stopIndex, int prediction, ATNConfigSet configs) {
        for (ANTLRErrorListener aNTLRErrorListener : this.delegates) {
            aNTLRErrorListener.reportContextSensitivity(recognizer, dfa, startIndex, stopIndex, prediction, configs);
        }
    }
}

