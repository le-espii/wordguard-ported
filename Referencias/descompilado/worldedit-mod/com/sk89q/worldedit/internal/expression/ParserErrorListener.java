/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.internal.expression;

import com.sk89q.worldedit.antlr4.runtime.BaseErrorListener;
import com.sk89q.worldedit.antlr4.runtime.RecognitionException;
import com.sk89q.worldedit.antlr4.runtime.Recognizer;
import com.sk89q.worldedit.internal.expression.ParserException;

class ParserErrorListener
extends BaseErrorListener {
    ParserErrorListener() {
    }

    @Override
    public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e) {
        throw new ParserException(charPositionInLine, msg);
    }
}

