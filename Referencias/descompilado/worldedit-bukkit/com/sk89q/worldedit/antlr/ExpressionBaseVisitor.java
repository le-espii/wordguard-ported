/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.antlr;

import com.sk89q.worldedit.antlr.ExpressionParser;
import com.sk89q.worldedit.antlr.ExpressionVisitor;
import com.sk89q.worldedit.antlr4.runtime.tree.AbstractParseTreeVisitor;

public class ExpressionBaseVisitor<T>
extends AbstractParseTreeVisitor<T>
implements ExpressionVisitor<T> {
    @Override
    public T visitAllStatements(ExpressionParser.AllStatementsContext ctx) {
        return this.visitChildren(ctx);
    }

    @Override
    public T visitStatements(ExpressionParser.StatementsContext ctx) {
        return this.visitChildren(ctx);
    }

    @Override
    public T visitStatement(ExpressionParser.StatementContext ctx) {
        return this.visitChildren(ctx);
    }

    @Override
    public T visitBlock(ExpressionParser.BlockContext ctx) {
        return this.visitChildren(ctx);
    }

    @Override
    public T visitIfStatement(ExpressionParser.IfStatementContext ctx) {
        return this.visitChildren(ctx);
    }

    @Override
    public T visitWhileStatement(ExpressionParser.WhileStatementContext ctx) {
        return this.visitChildren(ctx);
    }

    @Override
    public T visitDoStatement(ExpressionParser.DoStatementContext ctx) {
        return this.visitChildren(ctx);
    }

    @Override
    public T visitForStatement(ExpressionParser.ForStatementContext ctx) {
        return this.visitChildren(ctx);
    }

    @Override
    public T visitSimpleForStatement(ExpressionParser.SimpleForStatementContext ctx) {
        return this.visitChildren(ctx);
    }

    @Override
    public T visitBreakStatement(ExpressionParser.BreakStatementContext ctx) {
        return this.visitChildren(ctx);
    }

    @Override
    public T visitContinueStatement(ExpressionParser.ContinueStatementContext ctx) {
        return this.visitChildren(ctx);
    }

    @Override
    public T visitReturnStatement(ExpressionParser.ReturnStatementContext ctx) {
        return this.visitChildren(ctx);
    }

    @Override
    public T visitSwitchStatement(ExpressionParser.SwitchStatementContext ctx) {
        return this.visitChildren(ctx);
    }

    @Override
    public T visitCase(ExpressionParser.CaseContext ctx) {
        return this.visitChildren(ctx);
    }

    @Override
    public T visitDefault(ExpressionParser.DefaultContext ctx) {
        return this.visitChildren(ctx);
    }

    @Override
    public T visitExpressionStatement(ExpressionParser.ExpressionStatementContext ctx) {
        return this.visitChildren(ctx);
    }

    @Override
    public T visitEmptyStatement(ExpressionParser.EmptyStatementContext ctx) {
        return this.visitChildren(ctx);
    }

    @Override
    public T visitExpression(ExpressionParser.ExpressionContext ctx) {
        return this.visitChildren(ctx);
    }

    @Override
    public T visitAssignmentExpression(ExpressionParser.AssignmentExpressionContext ctx) {
        return this.visitChildren(ctx);
    }

    @Override
    public T visitAssignment(ExpressionParser.AssignmentContext ctx) {
        return this.visitChildren(ctx);
    }

    @Override
    public T visitAssignmentOperator(ExpressionParser.AssignmentOperatorContext ctx) {
        return this.visitChildren(ctx);
    }

    @Override
    public T visitCEFallthrough(ExpressionParser.CEFallthroughContext ctx) {
        return this.visitChildren(ctx);
    }

    @Override
    public T visitTernaryExpr(ExpressionParser.TernaryExprContext ctx) {
        return this.visitChildren(ctx);
    }

    @Override
    public T visitCOFallthrough(ExpressionParser.COFallthroughContext ctx) {
        return this.visitChildren(ctx);
    }

    @Override
    public T visitConditionalOrExpr(ExpressionParser.ConditionalOrExprContext ctx) {
        return this.visitChildren(ctx);
    }

    @Override
    public T visitCAFallthrough(ExpressionParser.CAFallthroughContext ctx) {
        return this.visitChildren(ctx);
    }

    @Override
    public T visitConditionalAndExpr(ExpressionParser.ConditionalAndExprContext ctx) {
        return this.visitChildren(ctx);
    }

    @Override
    public T visitEqualityExpr(ExpressionParser.EqualityExprContext ctx) {
        return this.visitChildren(ctx);
    }

    @Override
    public T visitEqFallthrough(ExpressionParser.EqFallthroughContext ctx) {
        return this.visitChildren(ctx);
    }

    @Override
    public T visitReFallthrough(ExpressionParser.ReFallthroughContext ctx) {
        return this.visitChildren(ctx);
    }

    @Override
    public T visitRelationalExpr(ExpressionParser.RelationalExprContext ctx) {
        return this.visitChildren(ctx);
    }

    @Override
    public T visitShFallthrough(ExpressionParser.ShFallthroughContext ctx) {
        return this.visitChildren(ctx);
    }

    @Override
    public T visitShiftExpr(ExpressionParser.ShiftExprContext ctx) {
        return this.visitChildren(ctx);
    }

    @Override
    public T visitAddExpr(ExpressionParser.AddExprContext ctx) {
        return this.visitChildren(ctx);
    }

    @Override
    public T visitAdFallthrough(ExpressionParser.AdFallthroughContext ctx) {
        return this.visitChildren(ctx);
    }

    @Override
    public T visitMultiplicativeExpr(ExpressionParser.MultiplicativeExprContext ctx) {
        return this.visitChildren(ctx);
    }

    @Override
    public T visitMuFallthrough(ExpressionParser.MuFallthroughContext ctx) {
        return this.visitChildren(ctx);
    }

    @Override
    public T visitPowerExpr(ExpressionParser.PowerExprContext ctx) {
        return this.visitChildren(ctx);
    }

    @Override
    public T visitPwFallthrough(ExpressionParser.PwFallthroughContext ctx) {
        return this.visitChildren(ctx);
    }

    @Override
    public T visitPreCrementExpr(ExpressionParser.PreCrementExprContext ctx) {
        return this.visitChildren(ctx);
    }

    @Override
    public T visitPlusMinusExpr(ExpressionParser.PlusMinusExprContext ctx) {
        return this.visitChildren(ctx);
    }

    @Override
    public T visitUaFallthrough(ExpressionParser.UaFallthroughContext ctx) {
        return this.visitChildren(ctx);
    }

    @Override
    public T visitComplementExpr(ExpressionParser.ComplementExprContext ctx) {
        return this.visitChildren(ctx);
    }

    @Override
    public T visitNotExpr(ExpressionParser.NotExprContext ctx) {
        return this.visitChildren(ctx);
    }

    @Override
    public T visitPostfixExpr(ExpressionParser.PostfixExprContext ctx) {
        return this.visitChildren(ctx);
    }

    @Override
    public T visitPoFallthrough(ExpressionParser.PoFallthroughContext ctx) {
        return this.visitChildren(ctx);
    }

    @Override
    public T visitPostCrementExpr(ExpressionParser.PostCrementExprContext ctx) {
        return this.visitChildren(ctx);
    }

    @Override
    public T visitFunctionCallExpr(ExpressionParser.FunctionCallExprContext ctx) {
        return this.visitChildren(ctx);
    }

    @Override
    public T visitConstantExpr(ExpressionParser.ConstantExprContext ctx) {
        return this.visitChildren(ctx);
    }

    @Override
    public T visitIdExpr(ExpressionParser.IdExprContext ctx) {
        return this.visitChildren(ctx);
    }

    @Override
    public T visitWrappedExpr(ExpressionParser.WrappedExprContext ctx) {
        return this.visitChildren(ctx);
    }

    @Override
    public T visitConstantExpression(ExpressionParser.ConstantExpressionContext ctx) {
        return this.visitChildren(ctx);
    }

    @Override
    public T visitFunctionCall(ExpressionParser.FunctionCallContext ctx) {
        return this.visitChildren(ctx);
    }
}

