/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.antlr4.runtime;

import com.sk89q.worldedit.antlr4.runtime.Parser;
import com.sk89q.worldedit.antlr4.runtime.ParserRuleContext;
import com.sk89q.worldedit.antlr4.runtime.RecognitionException;

public class InputMismatchException
extends RecognitionException {
    public InputMismatchException(Parser recognizer) {
        super(recognizer, recognizer.getInputStream(), recognizer._ctx);
        this.setOffendingToken(recognizer.getCurrentToken());
    }

    public InputMismatchException(Parser recognizer, int state, ParserRuleContext ctx) {
        super(recognizer, recognizer.getInputStream(), ctx);
        this.setOffendingState(state);
        this.setOffendingToken(recognizer.getCurrentToken());
    }
}

