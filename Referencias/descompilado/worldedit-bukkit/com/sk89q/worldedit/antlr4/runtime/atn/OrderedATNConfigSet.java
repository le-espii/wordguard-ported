/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.antlr4.runtime.atn;

import com.sk89q.worldedit.antlr4.runtime.atn.ATNConfigSet;
import com.sk89q.worldedit.antlr4.runtime.misc.ObjectEqualityComparator;

public class OrderedATNConfigSet
extends ATNConfigSet {
    public OrderedATNConfigSet() {
        this.configLookup = new LexerConfigHashSet();
    }

    public static class LexerConfigHashSet
    extends ATNConfigSet.AbstractConfigHashSet {
        public LexerConfigHashSet() {
            super(ObjectEqualityComparator.INSTANCE);
        }
    }
}

