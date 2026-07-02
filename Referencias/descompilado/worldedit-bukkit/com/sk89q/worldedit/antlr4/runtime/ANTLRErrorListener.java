/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.antlr4.runtime;

import com.sk89q.worldedit.antlr4.runtime.Parser;
import com.sk89q.worldedit.antlr4.runtime.RecognitionException;
import com.sk89q.worldedit.antlr4.runtime.Recognizer;
import com.sk89q.worldedit.antlr4.runtime.atn.ATNConfigSet;
import com.sk89q.worldedit.antlr4.runtime.dfa.DFA;
import java.util.BitSet;

public interface ANTLRErrorListener {
    public void syntaxError(Recognizer<?, ?> var1, Object var2, int var3, int var4, String var5, RecognitionException var6);

    public void reportAmbiguity(Parser var1, DFA var2, int var3, int var4, boolean var5, BitSet var6, ATNConfigSet var7);

    public void reportAttemptingFullContext(Parser var1, DFA var2, int var3, int var4, BitSet var5, ATNConfigSet var6);

    public void reportContextSensitivity(Parser var1, DFA var2, int var3, int var4, int var5, ATNConfigSet var6);
}

