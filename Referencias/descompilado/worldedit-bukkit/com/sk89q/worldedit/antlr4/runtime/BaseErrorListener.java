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

public class BaseErrorListener
implements ANTLRErrorListener {
    @Override
    public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e) {
    }

    @Override
    public void reportAmbiguity(Parser recognizer, DFA dfa, int startIndex, int stopIndex, boolean exact, BitSet ambigAlts, ATNConfigSet configs) {
    }

    @Override
    public void reportAttemptingFullContext(Parser recognizer, DFA dfa, int startIndex, int stopIndex, BitSet conflictingAlts, ATNConfigSet configs) {
    }

    @Override
    public void reportContextSensitivity(Parser recognizer, DFA dfa, int startIndex, int stopIndex, int prediction, ATNConfigSet configs) {
    }
}

