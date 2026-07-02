/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.antlr4.runtime;

import com.sk89q.worldedit.antlr4.runtime.BaseErrorListener;
import com.sk89q.worldedit.antlr4.runtime.RecognitionException;
import com.sk89q.worldedit.antlr4.runtime.Recognizer;

public class ConsoleErrorListener
extends BaseErrorListener {
    public static final ConsoleErrorListener INSTANCE = new ConsoleErrorListener();

    @Override
    public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e) {
        System.err.println("line " + line + ":" + charPositionInLine + " " + msg);
    }
}

