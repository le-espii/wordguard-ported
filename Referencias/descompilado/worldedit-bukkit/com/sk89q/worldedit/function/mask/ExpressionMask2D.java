/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  javax.annotation.Nullable
 */
package com.sk89q.worldedit.function.mask;

import com.google.common.base.Preconditions;
import com.sk89q.worldedit.function.mask.AbstractMask2D;
import com.sk89q.worldedit.internal.expression.EvaluationException;
import com.sk89q.worldedit.internal.expression.Expression;
import com.sk89q.worldedit.internal.expression.ExpressionException;
import com.sk89q.worldedit.math.BlockVector2;
import java.util.function.IntSupplier;
import javax.annotation.Nullable;

public class ExpressionMask2D
extends AbstractMask2D {
    private final Expression expression;
    private final IntSupplier timeout;

    public ExpressionMask2D(String expression) throws ExpressionException {
        this(Expression.compile((String)Preconditions.checkNotNull((Object)expression), "x", "z"));
    }

    public ExpressionMask2D(Expression expression) {
        this(expression, null);
    }

    public ExpressionMask2D(Expression expression, @Nullable IntSupplier timeout) {
        Preconditions.checkNotNull((Object)expression);
        this.expression = expression;
        this.timeout = timeout;
    }

    @Override
    public boolean test(BlockVector2 vector) {
        try {
            if (this.timeout != null) {
                return this.expression.evaluate(vector.x(), 0.0, vector.z()) > 0.0;
            }
            return this.expression.evaluate(new double[]{vector.x(), 0.0, vector.z()}, this.timeout.getAsInt()) > 0.0;
        }
        catch (EvaluationException e) {
            return false;
        }
    }
}

