/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.internal.expression;

import com.sk89q.worldedit.internal.expression.ExpressionTimeoutException;
import com.sk89q.worldedit.internal.expression.Functions;
import com.sk89q.worldedit.internal.expression.SlotTable;
import java.time.Instant;
import java.util.Objects;

public record ExecutionData(SlotTable slots, Functions functions, Instant deadline) {
    private final SlotTable slots;
    private final Functions functions;
    public static final ExecutionData CONSTANT_EVALUATOR = new ExecutionData(null, null, Instant.MAX);

    public SlotTable slots() {
        return Objects.requireNonNull(this.slots, "Cannot use variables in a constant");
    }

    public Functions functions() {
        return Objects.requireNonNull(this.functions, "Cannot use functions in a constant");
    }

    public void checkDeadline() {
        if (Instant.now().isAfter(this.deadline)) {
            throw new ExpressionTimeoutException("Calculations exceeded time limit.");
        }
    }
}

