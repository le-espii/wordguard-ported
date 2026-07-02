/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.antlr4.runtime.atn;

import com.sk89q.worldedit.antlr4.runtime.Lexer;
import com.sk89q.worldedit.antlr4.runtime.atn.LexerAction;
import com.sk89q.worldedit.antlr4.runtime.atn.LexerActionType;
import com.sk89q.worldedit.antlr4.runtime.misc.MurmurHash;

public final class LexerPopModeAction
implements LexerAction {
    public static final LexerPopModeAction INSTANCE = new LexerPopModeAction();

    private LexerPopModeAction() {
    }

    @Override
    public LexerActionType getActionType() {
        return LexerActionType.POP_MODE;
    }

    @Override
    public boolean isPositionDependent() {
        return false;
    }

    @Override
    public void execute(Lexer lexer) {
        lexer.popMode();
    }

    public int hashCode() {
        int hash = MurmurHash.initialize();
        hash = MurmurHash.update(hash, this.getActionType().ordinal());
        return MurmurHash.finish(hash, 1);
    }

    public boolean equals(Object obj) {
        return obj == this;
    }

    public String toString() {
        return "popMode";
    }
}

