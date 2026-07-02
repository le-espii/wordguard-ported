/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.antlr;

import com.sk89q.worldedit.antlr.ExpressionParser;
import com.sk89q.worldedit.antlr4.runtime.tree.ParseTreeVisitor;

public interface ExpressionVisitor<T>
extends ParseTreeVisitor<T> {
    public T visitAllStatements(ExpressionParser.AllStatementsContext var1);

    public T visitStatements(ExpressionParser.StatementsContext var1);

    public T visitStatement(ExpressionParser.StatementContext var1);

    public T visitBlock(ExpressionParser.BlockContext var1);

    public T visitIfStatement(ExpressionParser.IfStatementContext var1);

    public T visitWhileStatement(ExpressionParser.WhileStatementContext var1);

    public T visitDoStatement(ExpressionParser.DoStatementContext var1);

    public T visitForStatement(ExpressionParser.ForStatementContext var1);

    public T visitSimpleForStatement(ExpressionParser.SimpleForStatementContext var1);

    public T visitBreakStatement(ExpressionParser.BreakStatementContext var1);

    public T visitContinueStatement(ExpressionParser.ContinueStatementContext var1);

    public T visitReturnStatement(ExpressionParser.ReturnStatementContext var1);

    public T visitSwitchStatement(ExpressionParser.SwitchStatementContext var1);

    public T visitCase(ExpressionParser.CaseContext var1);

    public T visitDefault(ExpressionParser.DefaultContext var1);

    public T visitExpressionStatement(ExpressionParser.ExpressionStatementContext var1);

    public T visitEmptyStatement(ExpressionParser.EmptyStatementContext var1);

    public T visitExpression(ExpressionParser.ExpressionContext var1);

    public T visitAssignmentExpression(ExpressionParser.AssignmentExpressionContext var1);

    public T visitAssignment(ExpressionParser.AssignmentContext var1);

    public T visitAssignmentOperator(ExpressionParser.AssignmentOperatorContext var1);

    public T visitCEFallthrough(ExpressionParser.CEFallthroughContext var1);

    public T visitTernaryExpr(ExpressionParser.TernaryExprContext var1);

    public T visitCOFallthrough(ExpressionParser.COFallthroughContext var1);

    public T visitConditionalOrExpr(ExpressionParser.ConditionalOrExprContext var1);

    public T visitCAFallthrough(ExpressionParser.CAFallthroughContext var1);

    public T visitConditionalAndExpr(ExpressionParser.ConditionalAndExprContext var1);

    public T visitEqualityExpr(ExpressionParser.EqualityExprContext var1);

    public T visitEqFallthrough(ExpressionParser.EqFallthroughContext var1);

    public T visitReFallthrough(ExpressionParser.ReFallthroughContext var1);

    public T visitRelationalExpr(ExpressionParser.RelationalExprContext var1);

    public T visitShFallthrough(ExpressionParser.ShFallthroughContext var1);

    public T visitShiftExpr(ExpressionParser.ShiftExprContext var1);

    public T visitAddExpr(ExpressionParser.AddExprContext var1);

    public T visitAdFallthrough(ExpressionParser.AdFallthroughContext var1);

    public T visitMultiplicativeExpr(ExpressionParser.MultiplicativeExprContext var1);

    public T visitMuFallthrough(ExpressionParser.MuFallthroughContext var1);

    public T visitPowerExpr(ExpressionParser.PowerExprContext var1);

    public T visitPwFallthrough(ExpressionParser.PwFallthroughContext var1);

    public T visitPreCrementExpr(ExpressionParser.PreCrementExprContext var1);

    public T visitPlusMinusExpr(ExpressionParser.PlusMinusExprContext var1);

    public T visitUaFallthrough(ExpressionParser.UaFallthroughContext var1);

    public T visitComplementExpr(ExpressionParser.ComplementExprContext var1);

    public T visitNotExpr(ExpressionParser.NotExprContext var1);

    public T visitPostfixExpr(ExpressionParser.PostfixExprContext var1);

    public T visitPoFallthrough(ExpressionParser.PoFallthroughContext var1);

    public T visitPostCrementExpr(ExpressionParser.PostCrementExprContext var1);

    public T visitFunctionCallExpr(ExpressionParser.FunctionCallExprContext var1);

    public T visitConstantExpr(ExpressionParser.ConstantExprContext var1);

    public T visitIdExpr(ExpressionParser.IdExprContext var1);

    public T visitWrappedExpr(ExpressionParser.WrappedExprContext var1);

    public T visitConstantExpression(ExpressionParser.ConstantExpressionContext var1);

    public T visitFunctionCall(ExpressionParser.FunctionCallContext var1);
}

