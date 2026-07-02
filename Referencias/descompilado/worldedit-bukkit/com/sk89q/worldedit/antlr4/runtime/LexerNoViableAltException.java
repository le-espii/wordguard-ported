/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.antlr4.runtime;

import com.sk89q.worldedit.antlr4.runtime.CharStream;
import com.sk89q.worldedit.antlr4.runtime.Lexer;
import com.sk89q.worldedit.antlr4.runtime.RecognitionException;
import com.sk89q.worldedit.antlr4.runtime.atn.ATNConfigSet;
import com.sk89q.worldedit.antlr4.runtime.misc.Interval;
import com.sk89q.worldedit.antlr4.runtime.misc.Utils;
import java.util.Locale;

public class LexerNoViableAltException
extends RecognitionException {
    private final int startIndex;
    private final ATNConfigSet deadEndConfigs;

    public LexerNoViableAltException(Lexer lexer, CharStream input, int startIndex, ATNConfigSet deadEndConfigs) {
        super(lexer, input, null);
        this.startIndex = startIndex;
        this.deadEndConfigs = deadEndConfigs;
    }

    public int getStartIndex() {
        return this.startIndex;
    }

    public ATNConfigSet getDeadEndConfigs() {
        return this.deadEndConfigs;
    }

    @Override
    public CharStream getInputStream() {
        return (CharStream)super.getInputStream();
    }

    @Override
    public String toString() {
        String symbol = "";
        if (this.startIndex >= 0 && this.startIndex < this.getInputStream().size()) {
            symbol = this.getInputStream().getText(Interval.of(this.startIndex, this.startIndex));
            symbol = Utils.escapeWhitespace(symbol, false);
        }
        return String.format(Locale.getDefault(), "%s('%s')", LexerNoViableAltException.class.getSimpleName(), symbol);
    }
}

