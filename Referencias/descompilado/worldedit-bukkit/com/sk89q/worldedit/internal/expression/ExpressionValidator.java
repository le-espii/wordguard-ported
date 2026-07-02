/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.internal.expression;

import com.sk89q.worldedit.antlr.ExpressionBaseListener;
import com.sk89q.worldedit.antlr.ExpressionParser;
import com.sk89q.worldedit.internal.expression.ExpressionHelper;
import com.sk89q.worldedit.internal.expression.Functions;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

class ExpressionValidator
extends ExpressionBaseListener {
    private final Set<String> variableNames = new HashSet<String>();
    private final Functions functions;

    ExpressionValidator(Collection<String> variableNames, Functions functions) {
        this.variableNames.addAll(variableNames);
        this.functions = functions;
    }

    private void bindVariable(String name) {
        this.variableNames.add(name);
    }

    @Override
    public void enterAssignment(ExpressionParser.AssignmentContext ctx) {
        this.bindVariable(ctx.target.getText());
    }

    @Override
    public void enterSimpleForStatement(ExpressionParser.SimpleForStatementContext ctx) {
        this.bindVariable(ctx.counter.getText());
    }

    @Override
    public void enterIdExpr(ExpressionParser.IdExprContext ctx) {
        String text = ctx.source.getText();
        ExpressionHelper.check(this.variableNames.contains(text), ctx, "Variable '" + text + "' is not bound");
    }

    @Override
    public void enterFunctionCall(ExpressionParser.FunctionCallContext ctx) {
        ExpressionHelper.resolveFunction(this.functions, ctx);
    }
}

