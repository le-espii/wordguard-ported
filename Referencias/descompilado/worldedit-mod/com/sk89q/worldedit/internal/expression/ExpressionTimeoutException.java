/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.internal.expression;

import com.sk89q.worldedit.internal.expression.EvaluationException;

public class ExpressionTimeoutException
extends EvaluationException {
    public ExpressionTimeoutException(String message) {
        super(-1, message);
    }
}

