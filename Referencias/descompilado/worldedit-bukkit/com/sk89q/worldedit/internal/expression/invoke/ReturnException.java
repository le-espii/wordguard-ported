/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.internal.expression.invoke;

public class ReturnException
extends RuntimeException {
    private final Double result;

    public ReturnException(Double result) {
        super("return " + result, null, true, false);
        this.result = result;
    }

    public Double getResult() {
        return this.result;
    }
}

