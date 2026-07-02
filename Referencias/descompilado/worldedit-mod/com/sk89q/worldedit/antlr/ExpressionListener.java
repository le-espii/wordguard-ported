/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.antlr;

import com.sk89q.worldedit.antlr.ExpressionParser;
import com.sk89q.worldedit.antlr4.runtime.tree.ParseTreeListener;

public interface ExpressionListener
extends ParseTreeListener {
    public void enterAllStatements(ExpressionParser.AllStatementsContext var1);

    public void exitAllStatements(ExpressionParser.AllStatementsContext var1);

    public void enterStatements(ExpressionParser.StatementsContext var1);

    public void exitStatements(ExpressionParser.StatementsContext var1);

    public void enterStatement(ExpressionParser.StatementContext var1);

    public void exitStatement(ExpressionParser.StatementContext var1);

    public void enterBlock(ExpressionParser.BlockContext var1);

    public void exitBlock(ExpressionParser.BlockContext var1);

    public void enterIfStatement(ExpressionParser.IfStatementContext var1);

    public void exitIfStatement(ExpressionParser.IfStatementContext var1);

    public void enterWhileStatement(ExpressionParser.WhileStatementContext var1);

    public void exitWhileStatement(ExpressionParser.WhileStatementContext var1);

    public void enterDoStatement(ExpressionParser.DoStatementContext var1);

    public void exitDoStatement(ExpressionParser.DoStatementContext var1);

    public void enterForStatement(ExpressionParser.ForStatementContext var1);

    public void exitForStatement(ExpressionParser.ForStatementContext var1);

    public void enterSimpleForStatement(ExpressionParser.SimpleForStatementContext var1);

    public void exitSimpleForStatement(ExpressionParser.SimpleForStatementContext var1);

    public void enterBreakStatement(ExpressionParser.BreakStatementContext var1);

    public void exitBreakStatement(ExpressionParser.BreakStatementContext var1);

    public void enterContinueStatement(ExpressionParser.ContinueStatementContext var1);

    public void exitContinueStatement(ExpressionParser.ContinueStatementContext var1);

    public void enterReturnStatement(ExpressionParser.ReturnStatementContext var1);

    public void exitReturnStatement(ExpressionParser.ReturnStatementContext var1);

    public void enterSwitchStatement(ExpressionParser.SwitchStatementContext var1);

    public void exitSwitchStatement(ExpressionParser.SwitchStatementContext var1);

    public void enterCase(ExpressionParser.CaseContext var1);

    public void exitCase(ExpressionParser.CaseContext var1);

    public void enterDefault(ExpressionParser.DefaultContext var1);

    public void exitDefault(ExpressionParser.DefaultContext var1);

    public void enterExpressionStatement(ExpressionParser.ExpressionStatementContext var1);

    public void exitExpressionStatement(ExpressionParser.ExpressionStatementContext var1);

    public void enterEmptyStatement(ExpressionParser.EmptyStatementContext var1);

    public void exitEmptyStatement(ExpressionParser.EmptyStatementContext var1);

    public void enterExpression(ExpressionParser.ExpressionContext var1);

    public void exitExpression(ExpressionParser.ExpressionContext var1);

    public void enterAssignmentExpression(ExpressionParser.AssignmentExpressionContext var1);

    public void exitAssignmentExpression(ExpressionParser.AssignmentExpressionContext var1);

    public void enterAssignment(ExpressionParser.AssignmentContext var1);

    public void exitAssignment(ExpressionParser.AssignmentContext var1);

    public void enterAssignmentOperator(ExpressionParser.AssignmentOperatorContext var1);

    public void exitAssignmentOperator(ExpressionParser.AssignmentOperatorContext var1);

    public void enterCEFallthrough(ExpressionParser.CEFallthroughContext var1);

    public void exitCEFallthrough(ExpressionParser.CEFallthroughContext var1);

    public void enterTernaryExpr(ExpressionParser.TernaryExprContext var1);

    public void exitTernaryExpr(ExpressionParser.TernaryExprContext var1);

    public void enterCOFallthrough(ExpressionParser.COFallthroughContext var1);

    public void exitCOFallthrough(ExpressionParser.COFallthroughContext var1);

    public void enterConditionalOrExpr(ExpressionParser.ConditionalOrExprContext var1);

    public void exitConditionalOrExpr(ExpressionParser.ConditionalOrExprContext var1);

    public void enterCAFallthrough(ExpressionParser.CAFallthroughContext var1);

    public void exitCAFallthrough(ExpressionParser.CAFallthroughContext var1);

    public void enterConditionalAndExpr(ExpressionParser.ConditionalAndExprContext var1);

    public void exitConditionalAndExpr(ExpressionParser.ConditionalAndExprContext var1);

    public void enterEqualityExpr(ExpressionParser.EqualityExprContext var1);

    public void exitEqualityExpr(ExpressionParser.EqualityExprContext var1);

    public void enterEqFallthrough(ExpressionParser.EqFallthroughContext var1);

    public void exitEqFallthrough(ExpressionParser.EqFallthroughContext var1);

    public void enterReFallthrough(ExpressionParser.ReFallthroughContext var1);

    public void exitReFallthrough(ExpressionParser.ReFallthroughContext var1);

    public void enterRelationalExpr(ExpressionParser.RelationalExprContext var1);

    public void exitRelationalExpr(ExpressionParser.RelationalExprContext var1);

    public void enterShFallthrough(ExpressionParser.ShFallthroughContext var1);

    public void exitShFallthrough(ExpressionParser.ShFallthroughContext var1);

    public void enterShiftExpr(ExpressionParser.ShiftExprContext var1);

    public void exitShiftExpr(ExpressionParser.ShiftExprContext var1);

    public void enterAddExpr(ExpressionParser.AddExprContext var1);

    public void exitAddExpr(ExpressionParser.AddExprContext var1);

    public void enterAdFallthrough(ExpressionParser.AdFallthroughContext var1);

    public void exitAdFallthrough(ExpressionParser.AdFallthroughContext var1);

    public void enterMultiplicativeExpr(ExpressionParser.MultiplicativeExprContext var1);

    public void exitMultiplicativeExpr(ExpressionParser.MultiplicativeExprContext var1);

    public void enterMuFallthrough(ExpressionParser.MuFallthroughContext var1);

    public void exitMuFallthrough(ExpressionParser.MuFallthroughContext var1);

    public void enterPowerExpr(ExpressionParser.PowerExprContext var1);

    public void exitPowerExpr(ExpressionParser.PowerExprContext var1);

    public void enterPwFallthrough(ExpressionParser.PwFallthroughContext var1);

    public void exitPwFallthrough(ExpressionParser.PwFallthroughContext var1);

    public void enterPreCrementExpr(ExpressionParser.PreCrementExprContext var1);

    public void exitPreCrementExpr(ExpressionParser.PreCrementExprContext var1);

    public void enterPlusMinusExpr(ExpressionParser.PlusMinusExprContext var1);

    public void exitPlusMinusExpr(ExpressionParser.PlusMinusExprContext var1);

    public void enterUaFallthrough(ExpressionParser.UaFallthroughContext var1);

    public void exitUaFallthrough(ExpressionParser.UaFallthroughContext var1);

    public void enterComplementExpr(ExpressionParser.ComplementExprContext var1);

    public void exitComplementExpr(ExpressionParser.ComplementExprContext var1);

    public void enterNotExpr(ExpressionParser.NotExprContext var1);

    public void exitNotExpr(ExpressionParser.NotExprContext var1);

    public void enterPostfixExpr(ExpressionParser.PostfixExprContext var1);

    public void exitPostfixExpr(ExpressionParser.PostfixExprContext var1);

    public void enterPoFallthrough(ExpressionParser.PoFallthroughContext var1);

    public void exitPoFallthrough(ExpressionParser.PoFallthroughContext var1);

    public void enterPostCrementExpr(ExpressionParser.PostCrementExprContext var1);

    public void exitPostCrementExpr(ExpressionParser.PostCrementExprContext var1);

    public void enterFunctionCallExpr(ExpressionParser.FunctionCallExprContext var1);

    public void exitFunctionCallExpr(ExpressionParser.FunctionCallExprContext var1);

    public void enterConstantExpr(ExpressionParser.ConstantExprContext var1);

    public void exitConstantExpr(ExpressionParser.ConstantExprContext var1);

    public void enterIdExpr(ExpressionParser.IdExprContext var1);

    public void exitIdExpr(ExpressionParser.IdExprContext var1);

    public void enterWrappedExpr(ExpressionParser.WrappedExprContext var1);

    public void exitWrappedExpr(ExpressionParser.WrappedExprContext var1);

    public void enterConstantExpression(ExpressionParser.ConstantExpressionContext var1);

    public void exitConstantExpression(ExpressionParser.ConstantExpressionContext var1);

    public void enterFunctionCall(ExpressionParser.FunctionCallContext var1);

    public void exitFunctionCall(ExpressionParser.FunctionCallContext var1);
}

