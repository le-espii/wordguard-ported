/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.antlr4.runtime;

import com.sk89q.worldedit.antlr4.runtime.DefaultErrorStrategy;
import com.sk89q.worldedit.antlr4.runtime.InputMismatchException;
import com.sk89q.worldedit.antlr4.runtime.Parser;
import com.sk89q.worldedit.antlr4.runtime.ParserRuleContext;
import com.sk89q.worldedit.antlr4.runtime.RecognitionException;
import com.sk89q.worldedit.antlr4.runtime.Token;
import com.sk89q.worldedit.antlr4.runtime.misc.ParseCancellationException;

public class BailErrorStrategy
extends DefaultErrorStrategy {
    @Override
    public void recover(Parser recognizer, RecognitionException e) {
        for (ParserRuleContext context = recognizer.getContext(); context != null; context = context.getParent()) {
            context.exception = e;
        }
        throw new ParseCancellationException(e);
    }

    @Override
    public Token recoverInline(Parser recognizer) throws RecognitionException {
        InputMismatchException e = new InputMismatchException(recognizer);
        for (ParserRuleContext context = recognizer.getContext(); context != null; context = context.getParent()) {
            context.exception = e;
        }
        throw new ParseCancellationException(e);
    }

    @Override
    public void sync(Parser recognizer) {
    }
}

