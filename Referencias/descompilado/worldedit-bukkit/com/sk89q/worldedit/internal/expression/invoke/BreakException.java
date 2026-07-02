/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.internal.expression.invoke;

class BreakException
extends RuntimeException {
    public static final BreakException BREAK = new BreakException(false);
    public static final BreakException CONTINUE = new BreakException(true);
    public final boolean doContinue;

    private BreakException(boolean doContinue) {
        super(doContinue ? "'continue' encountered outside a loop" : "'break' encountered outside a loop", null, true, false);
        this.doContinue = doContinue;
    }
}

