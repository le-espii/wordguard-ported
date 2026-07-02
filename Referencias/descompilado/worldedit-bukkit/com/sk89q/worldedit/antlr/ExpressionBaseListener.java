/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.antlr;

import com.sk89q.worldedit.antlr.ExpressionListener;
import com.sk89q.worldedit.antlr.ExpressionParser;
import com.sk89q.worldedit.antlr4.runtime.ParserRuleContext;
import com.sk89q.worldedit.antlr4.runtime.tree.ErrorNode;
import com.sk89q.worldedit.antlr4.runtime.tree.TerminalNode;

public class ExpressionBaseListener
implements ExpressionListener {
    @Override
    public void enterAllStatements(ExpressionParser.AllStatementsContext ctx) {
    }

    @Override
    public void exitAllStatements(ExpressionParser.AllStatementsContext ctx) {
    }

    @Override
    public void enterStatements(ExpressionParser.StatementsContext ctx) {
    }

    @Override
    public void exitStatements(ExpressionParser.StatementsContext ctx) {
    }

    @Override
    public void enterStatement(ExpressionParser.StatementContext ctx) {
    }

    @Override
    public void exitStatement(ExpressionParser.StatementContext ctx) {
    }

    @Override
    public void enterBlock(ExpressionParser.BlockContext ctx) {
    }

    @Override
    public void exitBlock(ExpressionParser.BlockContext ctx) {
    }

    @Override
    public void enterIfStatement(ExpressionParser.IfStatementContext ctx) {
    }

    @Override
    public void exitIfStatement(ExpressionParser.IfStatementContext ctx) {
    }

    @Override
    public void enterWhileStatement(ExpressionParser.WhileStatementContext ctx) {
    }

    @Override
    public void exitWhileStatement(ExpressionParser.WhileStatementContext ctx) {
    }

    @Override
    public void enterDoStatement(ExpressionParser.DoStatementContext ctx) {
    }

    @Override
    public void exitDoStatement(ExpressionParser.DoStatementContext ctx) {
    }

    @Override
    public void enterForStatement(ExpressionParser.ForStatementContext ctx) {
    }

    @Override
    public void exitForStatement(ExpressionParser.ForStatementContext ctx) {
    }

    @Override
    public void enterSimpleForStatement(ExpressionParser.SimpleForStatementContext ctx) {
    }

    @Override
    public void exitSimpleForStatement(ExpressionParser.SimpleForStatementContext ctx) {
    }

    @Override
    public void enterBreakStatement(ExpressionParser.BreakStatementContext ctx) {
    }

    @Override
    public void exitBreakStatement(ExpressionParser.BreakStatementContext ctx) {
    }

    @Override
    public void enterContinueStatement(ExpressionParser.ContinueStatementContext ctx) {
    }

    @Override
    public void exitContinueStatement(ExpressionParser.ContinueStatementContext ctx) {
    }

    @Override
    public void enterReturnStatement(ExpressionParser.ReturnStatementContext ctx) {
    }

    @Override
    public void exitReturnStatement(ExpressionParser.ReturnStatementContext ctx) {
    }

    @Override
    public void enterSwitchStatement(ExpressionParser.SwitchStatementContext ctx) {
    }

    @Override
    public void exitSwitchStatement(ExpressionParser.SwitchStatementContext ctx) {
    }

    @Override
    public void enterCase(ExpressionParser.CaseContext ctx) {
    }

    @Override
    public void exitCase(ExpressionParser.CaseContext ctx) {
    }

    @Override
    public void enterDefault(ExpressionParser.DefaultContext ctx) {
    }

    @Override
    public void exitDefault(ExpressionParser.DefaultContext ctx) {
    }

    @Override
    public void enterExpressionStatement(ExpressionParser.ExpressionStatementContext ctx) {
    }

    @Override
    public void exitExpressionStatement(ExpressionParser.ExpressionStatementContext ctx) {
    }

    @Override
    public void enterEmptyStatement(ExpressionParser.EmptyStatementContext ctx) {
    }

    @Override
    public void exitEmptyStatement(ExpressionParser.EmptyStatementContext ctx) {
    }

    @Override
    public void enterExpression(ExpressionParser.ExpressionContext ctx) {
    }

    @Override
    public void exitExpression(ExpressionParser.ExpressionContext ctx) {
    }

    @Override
    public void enterAssignmentExpression(ExpressionParser.AssignmentExpressionContext ctx) {
    }

    @Override
    public void exitAssignmentExpression(ExpressionParser.AssignmentExpressionContext ctx) {
    }

    @Override
    public void enterAssignment(ExpressionParser.AssignmentContext ctx) {
    }

    @Override
    public void exitAssignment(ExpressionParser.AssignmentContext ctx) {
    }

    @Override
    public void enterAssignmentOperator(ExpressionParser.AssignmentOperatorContext ctx) {
    }

    @Override
    public void exitAssignmentOperator(ExpressionParser.AssignmentOperatorContext ctx) {
    }

    @Override
    public void enterCEFallthrough(ExpressionParser.CEFallthroughContext ctx) {
    }

    @Override
    public void exitCEFallthrough(ExpressionParser.CEFallthroughContext ctx) {
    }

    @Override
    public void enterTernaryExpr(ExpressionParser.TernaryExprContext ctx) {
    }

    @Override
    public void exitTernaryExpr(ExpressionParser.TernaryExprContext ctx) {
    }

    @Override
    public void enterCOFallthrough(ExpressionParser.COFallthroughContext ctx) {
    }

    @Override
    public void exitCOFallthrough(ExpressionParser.COFallthroughContext ctx) {
    }

    @Override
    public void enterConditionalOrExpr(ExpressionParser.ConditionalOrExprContext ctx) {
    }

    @Override
    public void exitConditionalOrExpr(ExpressionParser.ConditionalOrExprContext ctx) {
    }

    @Override
    public void enterCAFallthrough(ExpressionParser.CAFallthroughContext ctx) {
    }

    @Override
    public void exitCAFallthrough(ExpressionParser.CAFallthroughContext ctx) {
    }

    @Override
    public void enterConditionalAndExpr(ExpressionParser.ConditionalAndExprContext ctx) {
    }

    @Override
    public void exitConditionalAndExpr(ExpressionParser.ConditionalAndExprContext ctx) {
    }

    @Override
    public void enterEqualityExpr(ExpressionParser.EqualityExprContext ctx) {
    }

    @Override
    public void exitEqualityExpr(ExpressionParser.EqualityExprContext ctx) {
    }

    @Override
    public void enterEqFallthrough(ExpressionParser.EqFallthroughContext ctx) {
    }

    @Override
    public void exitEqFallthrough(ExpressionParser.EqFallthroughContext ctx) {
    }

    @Override
    public void enterReFallthrough(ExpressionParser.ReFallthroughContext ctx) {
    }

    @Override
    public void exitReFallthrough(ExpressionParser.ReFallthroughContext ctx) {
    }

    @Override
    public void enterRelationalExpr(ExpressionParser.RelationalExprContext ctx) {
    }

    @Override
    public void exitRelationalExpr(ExpressionParser.RelationalExprContext ctx) {
    }

    @Override
    public void enterShFallthrough(ExpressionParser.ShFallthroughContext ctx) {
    }

    @Override
    public void exitShFallthrough(ExpressionParser.ShFallthroughContext ctx) {
    }

    @Override
    public void enterShiftExpr(ExpressionParser.ShiftExprContext ctx) {
    }

    @Override
    public void exitShiftExpr(ExpressionParser.ShiftExprContext ctx) {
    }

    @Override
    public void enterAddExpr(ExpressionParser.AddExprContext ctx) {
    }

    @Override
    public void exitAddExpr(ExpressionParser.AddExprContext ctx) {
    }

    @Override
    public void enterAdFallthrough(ExpressionParser.AdFallthroughContext ctx) {
    }

    @Override
    public void exitAdFallthrough(ExpressionParser.AdFallthroughContext ctx) {
    }

    @Override
    public void enterMultiplicativeExpr(ExpressionParser.MultiplicativeExprContext ctx) {
    }

    @Override
    public void exitMultiplicativeExpr(ExpressionParser.MultiplicativeExprContext ctx) {
    }

    @Override
    public void enterMuFallthrough(ExpressionParser.MuFallthroughContext ctx) {
    }

    @Override
    public void exitMuFallthrough(ExpressionParser.MuFallthroughContext ctx) {
    }

    @Override
    public void enterPowerExpr(ExpressionParser.PowerExprContext ctx) {
    }

    @Override
    public void exitPowerExpr(ExpressionParser.PowerExprContext ctx) {
    }

    @Override
    public void enterPwFallthrough(ExpressionParser.PwFallthroughContext ctx) {
    }

    @Override
    public void exitPwFallthrough(ExpressionParser.PwFallthroughContext ctx) {
    }

    @Override
    public void enterPreCrementExpr(ExpressionParser.PreCrementExprContext ctx) {
    }

    @Override
    public void exitPreCrementExpr(ExpressionParser.PreCrementExprContext ctx) {
    }

    @Override
    public void enterPlusMinusExpr(ExpressionParser.PlusMinusExprContext ctx) {
    }

    @Override
    public void exitPlusMinusExpr(ExpressionParser.PlusMinusExprContext ctx) {
    }

    @Override
    public void enterUaFallthrough(ExpressionParser.UaFallthroughContext ctx) {
    }

    @Override
    public void exitUaFallthrough(ExpressionParser.UaFallthroughContext ctx) {
    }

    @Override
    public void enterComplementExpr(ExpressionParser.ComplementExprContext ctx) {
    }

    @Override
    public void exitComplementExpr(ExpressionParser.ComplementExprContext ctx) {
    }

    @Override
    public void enterNotExpr(ExpressionParser.NotExprContext ctx) {
    }

    @Override
    public void exitNotExpr(ExpressionParser.NotExprContext ctx) {
    }

    @Override
    public void enterPostfixExpr(ExpressionParser.PostfixExprContext ctx) {
    }

    @Override
    public void exitPostfixExpr(ExpressionParser.PostfixExprContext ctx) {
    }

    @Override
    public void enterPoFallthrough(ExpressionParser.PoFallthroughContext ctx) {
    }

    @Override
    public void exitPoFallthrough(ExpressionParser.PoFallthroughContext ctx) {
    }

    @Override
    public void enterPostCrementExpr(ExpressionParser.PostCrementExprContext ctx) {
    }

    @Override
    public void exitPostCrementExpr(ExpressionParser.PostCrementExprContext ctx) {
    }

    @Override
    public void enterFunctionCallExpr(ExpressionParser.FunctionCallExprContext ctx) {
    }

    @Override
    public void exitFunctionCallExpr(ExpressionParser.FunctionCallExprContext ctx) {
    }

    @Override
    public void enterConstantExpr(ExpressionParser.ConstantExprContext ctx) {
    }

    @Override
    public void exitConstantExpr(ExpressionParser.ConstantExprContext ctx) {
    }

    @Override
    public void enterIdExpr(ExpressionParser.IdExprContext ctx) {
    }

    @Override
    public void exitIdExpr(ExpressionParser.IdExprContext ctx) {
    }

    @Override
    public void enterWrappedExpr(ExpressionParser.WrappedExprContext ctx) {
    }

    @Override
    public void exitWrappedExpr(ExpressionParser.WrappedExprContext ctx) {
    }

    @Override
    public void enterConstantExpression(ExpressionParser.ConstantExpressionContext ctx) {
    }

    @Override
    public void exitConstantExpression(ExpressionParser.ConstantExpressionContext ctx) {
    }

    @Override
    public void enterFunctionCall(ExpressionParser.FunctionCallContext ctx) {
    }

    @Override
    public void exitFunctionCall(ExpressionParser.FunctionCallContext ctx) {
    }

    @Override
    public void enterEveryRule(ParserRuleContext ctx) {
    }

    @Override
    public void exitEveryRule(ParserRuleContext ctx) {
    }

    @Override
    public void visitTerminal(TerminalNode node) {
    }

    @Override
    public void visitErrorNode(ErrorNode node) {
    }
}

