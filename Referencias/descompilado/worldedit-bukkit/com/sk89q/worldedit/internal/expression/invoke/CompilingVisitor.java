/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.internal.expression.invoke;

import com.sk89q.worldedit.antlr.ExpressionBaseVisitor;
import com.sk89q.worldedit.antlr.ExpressionParser;
import com.sk89q.worldedit.antlr4.runtime.CommonToken;
import com.sk89q.worldedit.antlr4.runtime.ParserRuleContext;
import com.sk89q.worldedit.antlr4.runtime.Token;
import com.sk89q.worldedit.antlr4.runtime.tree.ParseTree;
import com.sk89q.worldedit.antlr4.runtime.tree.RuleNode;
import com.sk89q.worldedit.antlr4.runtime.tree.TerminalNode;
import com.sk89q.worldedit.bukkit.fastutil.doubles.Double2ObjectLinkedOpenHashMap;
import com.sk89q.worldedit.internal.expression.EvaluationException;
import com.sk89q.worldedit.internal.expression.ExecutionData;
import com.sk89q.worldedit.internal.expression.ExpressionHelper;
import com.sk89q.worldedit.internal.expression.Functions;
import com.sk89q.worldedit.internal.expression.LocalSlot;
import com.sk89q.worldedit.internal.expression.invoke.BreakException;
import com.sk89q.worldedit.internal.expression.invoke.ExecNode;
import com.sk89q.worldedit.internal.expression.invoke.ExpressionHandles;
import com.sk89q.worldedit.internal.expression.invoke.ReturnException;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.List;
import java.util.function.DoubleBinaryOperator;
import java.util.function.Supplier;

class CompilingVisitor
extends ExpressionBaseVisitor<MethodHandle> {
    private final Functions functions;
    private static final MethodHandle BREAK_STATEMENT = ExpressionHandles.dropData(MethodHandles.throwException(Double.class, BreakException.class).bindTo(BreakException.BREAK));
    private static final MethodHandle CONTINUE_STATEMENT = ExpressionHandles.dropData(MethodHandles.throwException(Double.class, BreakException.class).bindTo(BreakException.CONTINUE));
    private static final MethodHandle RETURN_STATEMENT_BASE = MethodHandles.filterReturnValue(ExpressionHandles.NEW_RETURN_EXCEPTION, MethodHandles.throwException(Double.class, ReturnException.class));
    private static final double[] factorials = new double[171];
    private static final MethodHandle DEFAULT_RESULT;

    CompilingVisitor(Functions functions) {
        this.functions = functions;
    }

    private Token extractToken(ParserRuleContext ctx) {
        List children = ctx.children.stream().filter(TerminalNode.class::isInstance).map(TerminalNode.class::cast).toList();
        ExpressionHelper.check(children.size() == 1, ctx, "Expected exactly one token, got " + children.size());
        return ((TerminalNode)children.get(0)).getSymbol();
    }

    private ExecNode evaluate(ParserRuleContext ctx) {
        MethodHandle mh = ctx.accept(this);
        if (ctx.parent instanceof ParserRuleContext) {
            this.checkHandle(mh, (ParserRuleContext)ctx.parent);
        }
        return new ExecNode(ctx.start.getCharPositionInLine(), mh);
    }

    private void checkHandle(MethodHandle mh, ParserRuleContext ctx) {
        ExpressionHelper.check(mh.type().equals((Object)ExpressionHandles.COMPILED_EXPRESSION_SIG), ctx, "Incorrect type returned from handler for " + String.valueOf(ctx.getClass()));
    }

    private MethodHandle evaluateForNamedValue(ParserRuleContext ctx, String name) {
        MethodHandle guard = MethodHandles.guardWithTest(ExpressionHandles.IS_NULL.asType(MethodType.methodType(Boolean.TYPE, Double.class)), MethodHandles.dropArguments(ExpressionHandles.throwEvalException(ctx, "Invalid expression for " + name), 0, Double.class), MethodHandles.identity(Double.class));
        MethodHandle result = this.evaluate(ctx).handle();
        return MethodHandles.collectArguments(guard, 0, result);
    }

    private MethodHandle evaluateForValue(ParserRuleContext ctx) {
        return this.evaluateForNamedValue(ctx, "a value");
    }

    private MethodHandle evaluateBoolean(ParserRuleContext boolExpression) {
        MethodHandle value = this.evaluateForNamedValue(boolExpression, "a boolean");
        return MethodHandles.collectArguments(ExpressionHandles.DOUBLE_TO_BOOL, 0, value);
    }

    private MethodHandle evaluateConditional(ParserRuleContext condition, ParserRuleContext trueBranch, ParserRuleContext falseBranch) {
        return MethodHandles.guardWithTest(this.evaluateBoolean(condition), trueBranch == null ? ExpressionHandles.NULL_DOUBLE : this.evaluate(trueBranch).handle(), falseBranch == null ? ExpressionHandles.NULL_DOUBLE : this.evaluate(falseBranch).handle());
    }

    @Override
    public MethodHandle visitIfStatement(ExpressionParser.IfStatementContext ctx) {
        return this.evaluateConditional(ctx.condition, ctx.trueBranch, ctx.falseBranch);
    }

    @Override
    public MethodHandle visitTernaryExpr(ExpressionParser.TernaryExprContext ctx) {
        return this.evaluateConditional(ctx.condition, ctx.trueBranch, ctx.falseBranch);
    }

    @Override
    public MethodHandle visitWhileStatement(ExpressionParser.WhileStatementContext ctx) {
        return ExpressionHandles.whileLoop(this.evaluateBoolean(ctx.condition), this.evaluate(ctx.body));
    }

    @Override
    public MethodHandle visitDoStatement(ExpressionParser.DoStatementContext ctx) {
        return ExpressionHandles.doWhileLoop(this.evaluateBoolean(ctx.condition), this.evaluate(ctx.body));
    }

    @Override
    public MethodHandle visitForStatement(ExpressionParser.ForStatementContext ctx) {
        return ExpressionHandles.forLoop(this.evaluate(ctx.init).handle(), this.evaluateBoolean(ctx.condition), this.evaluate(ctx.body), this.evaluate(ctx.update).handle());
    }

    @Override
    public MethodHandle visitSimpleForStatement(ExpressionParser.SimpleForStatementContext ctx) {
        return ExpressionHandles.simpleForLoop(this.evaluateForValue(ctx.first), this.evaluateForValue(ctx.last), ctx.counter, this.evaluate(ctx.body));
    }

    @Override
    public MethodHandle visitBreakStatement(ExpressionParser.BreakStatementContext ctx) {
        return BREAK_STATEMENT;
    }

    @Override
    public MethodHandle visitContinueStatement(ExpressionParser.ContinueStatementContext ctx) {
        return CONTINUE_STATEMENT;
    }

    @Override
    public MethodHandle visitReturnStatement(ExpressionParser.ReturnStatementContext ctx) {
        return MethodHandles.filterArguments(RETURN_STATEMENT_BASE, 0, this.evaluate(ctx.value).handle());
    }

    @Override
    public MethodHandle visitSwitchStatement(ExpressionParser.SwitchStatementContext ctx) {
        Double2ObjectLinkedOpenHashMap<ExecNode> cases = new Double2ObjectLinkedOpenHashMap<ExecNode>(ctx.labels.size());
        ExecNode defaultCase = null;
        for (int i = 0; i < ctx.labels.size(); ++i) {
            ExpressionParser.SwitchLabelContext label = ctx.labels.get(i);
            ExpressionParser.StatementsContext body = ctx.bodies.get(i);
            ExecNode node = this.evaluate(body);
            if (label instanceof ExpressionParser.CaseContext) {
                ExpressionParser.CaseContext caseContext = (ExpressionParser.CaseContext)label;
                double key = (Double)ExpressionHandles.constantInvoke(this.evaluateForValue(caseContext.constant));
                ExpressionHelper.check(!cases.containsKey(key), body, "Duplicate cases detected.");
                cases.put(key, node);
                continue;
            }
            ExpressionHelper.check(defaultCase == null, body, "Duplicate default cases detected.");
            defaultCase = node;
        }
        return ExpressionHandles.switchStatement(cases, this.evaluateForValue(ctx.target), defaultCase);
    }

    @Override
    public MethodHandle visitExpressionStatement(ExpressionParser.ExpressionStatementContext ctx) {
        return this.evaluate(ctx.expression()).handle();
    }

    @Override
    public MethodHandle visitPostCrementExpr(ExpressionParser.PostCrementExprContext ctx) {
        Token target = ctx.target;
        int opType = ctx.op.getType();
        return ExpressionHandles.call(data -> {
            double value;
            LocalSlot.Variable variable = ExpressionHandles.getVariable(data, target);
            double result = value = variable.value();
            value = opType == 26 ? (value += 1.0) : (value -= 1.0);
            variable.setValue(value);
            return result;
        });
    }

    @Override
    public MethodHandle visitPreCrementExpr(ExpressionParser.PreCrementExprContext ctx) {
        Token target = ctx.target;
        int opType = ctx.op.getType();
        return ExpressionHandles.call(data -> {
            LocalSlot.Variable variable = ExpressionHandles.getVariable(data, target);
            double value = variable.value();
            value = opType == 26 ? (value += 1.0) : (value -= 1.0);
            variable.setValue(value);
            return value;
        });
    }

    @Override
    public MethodHandle visitPlusMinusExpr(ExpressionParser.PlusMinusExprContext ctx) {
        MethodHandle value = this.evaluateForValue(ctx.expr);
        switch (ctx.op.getType()) {
            case 1: {
                return value;
            }
            case 2: {
                return ExpressionHandles.call(data -> -((Double)ExpressionHandles.standardInvoke(value, data)).doubleValue());
            }
        }
        throw ExpressionHelper.evalException(ctx, "Invalid text for plus/minus expr: " + ctx.op.getText());
    }

    @Override
    public MethodHandle visitNotExpr(ExpressionParser.NotExprContext ctx) {
        MethodHandle expr = this.evaluateBoolean(ctx.expr);
        return ExpressionHandles.call(data -> ExpressionHandles.boolToDouble((Boolean)ExpressionHandles.standardInvoke(expr, data) == false));
    }

    @Override
    public MethodHandle visitComplementExpr(ExpressionParser.ComplementExprContext ctx) {
        MethodHandle expr = this.evaluateForValue(ctx.expr);
        return ExpressionHandles.call(data -> (long)((Double)ExpressionHandles.standardInvoke(expr, data)).doubleValue() ^ 0xFFFFFFFFFFFFFFFFL);
    }

    @Override
    public MethodHandle visitConditionalAndExpr(ExpressionParser.ConditionalAndExprContext ctx) {
        MethodHandle left = this.evaluateBoolean(ctx.left);
        MethodHandle right = this.evaluateForValue(ctx.right);
        return MethodHandles.guardWithTest(left, right, ExpressionHandles.dropData(MethodHandles.constant(Double.class, ExpressionHandles.boolToDouble(false))));
    }

    @Override
    public MethodHandle visitConditionalOrExpr(ExpressionParser.ConditionalOrExprContext ctx) {
        MethodHandle left = this.evaluateForValue(ctx.left);
        MethodHandle right = this.evaluateForValue(ctx.right);
        MethodHandle logic = MethodHandles.guardWithTest(ExpressionHandles.DOUBLE_TO_BOOL, MethodHandles.dropArguments(MethodHandles.identity(Double.class), 1, ExecutionData.class), MethodHandles.dropArguments(right, 0, Double.class));
        MethodHandle mixed = MethodHandles.collectArguments(logic, 0, left);
        return ExpressionHandles.dedupData(mixed);
    }

    private MethodHandle evaluateBinary(ParserRuleContext left, ParserRuleContext right, DoubleBinaryOperator op) {
        MethodHandle mhLeft = this.evaluateForValue(left);
        MethodHandle mhRight = this.evaluateForValue(right);
        MethodHandle doubleData = MethodHandles.filterArguments(ExpressionHandles.CALL_BINARY_OP.bindTo(op), 0, ExpressionHandles.unboxDoubles(mhLeft), ExpressionHandles.unboxDoubles(mhRight));
        return ExpressionHandles.dedupData(doubleData);
    }

    private MethodHandle evaluateBinary(ParserRuleContext left, ParserRuleContext right, Supplier<DoubleBinaryOperator> op) {
        return this.evaluateBinary(left, right, op.get());
    }

    @Override
    public MethodHandle visitPowerExpr(ExpressionParser.PowerExprContext ctx) {
        return this.evaluateBinary((ParserRuleContext)ctx.left, (ParserRuleContext)ctx.right, Math::pow);
    }

    @Override
    public MethodHandle visitMultiplicativeExpr(ExpressionParser.MultiplicativeExprContext ctx) {
        return this.evaluateBinary((ParserRuleContext)ctx.left, (ParserRuleContext)ctx.right, () -> {
            switch (ctx.op.getType()) {
                case 3: {
                    return (l, r) -> l * r;
                }
                case 4: {
                    return (l, r) -> l / r;
                }
                case 5: {
                    return (l, r) -> l % r;
                }
            }
            throw ExpressionHelper.evalException(ctx, "Invalid text for multiplicative expr: " + ctx.op.getText());
        });
    }

    @Override
    public MethodHandle visitAddExpr(ExpressionParser.AddExprContext ctx) {
        return this.evaluateBinary((ParserRuleContext)ctx.left, (ParserRuleContext)ctx.right, () -> {
            switch (ctx.op.getType()) {
                case 1: {
                    return Double::sum;
                }
                case 2: {
                    return (l, r) -> l - r;
                }
            }
            throw ExpressionHelper.evalException(ctx, "Invalid text for additive expr: " + ctx.op.getText());
        });
    }

    @Override
    public MethodHandle visitShiftExpr(ExpressionParser.ShiftExprContext ctx) {
        return this.evaluateBinary((ParserRuleContext)ctx.left, (ParserRuleContext)ctx.right, () -> {
            switch (ctx.op.getType()) {
                case 7: {
                    return (l, r) -> (long)l << (int)r;
                }
                case 8: {
                    return (l, r) -> (long)l >> (int)r;
                }
            }
            throw ExpressionHelper.evalException(ctx, "Invalid text for shift expr: " + ctx.op.getText());
        });
    }

    @Override
    public MethodHandle visitRelationalExpr(ExpressionParser.RelationalExprContext ctx) {
        return this.evaluateBinary((ParserRuleContext)ctx.left, (ParserRuleContext)ctx.right, () -> {
            switch (ctx.op.getType()) {
                case 20: {
                    return (l, r) -> ExpressionHandles.boolToDouble(l < r);
                }
                case 21: {
                    return (l, r) -> ExpressionHandles.boolToDouble(l <= r);
                }
                case 22: {
                    return (l, r) -> ExpressionHandles.boolToDouble(l > r);
                }
                case 23: {
                    return (l, r) -> ExpressionHandles.boolToDouble(l >= r);
                }
            }
            throw ExpressionHelper.evalException(ctx, "Invalid text for relational expr: " + ctx.op.getText());
        });
    }

    @Override
    public MethodHandle visitEqualityExpr(ExpressionParser.EqualityExprContext ctx) {
        return this.evaluateBinary((ParserRuleContext)ctx.left, (ParserRuleContext)ctx.right, () -> {
            switch (ctx.op.getType()) {
                case 17: {
                    return (l, r) -> ExpressionHandles.boolToDouble(l == r);
                }
                case 18: {
                    return (l, r) -> ExpressionHandles.boolToDouble(l != r);
                }
                case 19: {
                    return (l, r) -> ExpressionHandles.boolToDouble(CompilingVisitor.almostEqual2sComplement(l, r));
                }
            }
            throw ExpressionHelper.evalException(ctx, "Invalid text for equality expr: " + ctx.op.getText());
        });
    }

    private static boolean almostEqual2sComplement(double a, double b) {
        long longDiff;
        long bLong;
        long aLong = Double.doubleToRawLongBits(a);
        if (aLong < 0L) {
            aLong = Long.MIN_VALUE - aLong;
        }
        if ((bLong = Double.doubleToRawLongBits(b)) < 0L) {
            bLong = Long.MIN_VALUE - bLong;
        }
        return (longDiff = Math.abs(aLong - bLong)) <= 450359963L;
    }

    @Override
    public MethodHandle visitPostfixExpr(ExpressionParser.PostfixExprContext ctx) {
        MethodHandle value = this.evaluateForValue(ctx.expr);
        if (ctx.op.getType() == 36) {
            return ExpressionHandles.call(data -> CompilingVisitor.factorial((Double)ExpressionHandles.standardInvoke(value, data)));
        }
        throw ExpressionHelper.evalException(ctx, "Invalid text for post-unary expr: " + ctx.op.getText());
    }

    private static double factorial(double x) throws EvaluationException {
        int n = (int)x;
        if (n < 0) {
            return 0.0;
        }
        if (n >= factorials.length) {
            return Double.POSITIVE_INFINITY;
        }
        return factorials[n];
    }

    @Override
    public MethodHandle visitAssignment(ExpressionParser.AssignmentContext ctx) {
        int type = this.extractToken(ctx.assignmentOperator()).getType();
        Token target = ctx.target;
        MethodHandle getArg = this.evaluateForValue(ctx.expression());
        return ExpressionHandles.call(data -> {
            double value;
            LocalSlot.Variable variable;
            double arg = (Double)ExpressionHandles.standardInvoke(getArg, data);
            if (type == 9) {
                variable = ExpressionHandles.initVariable(data, target);
                value = arg;
            } else {
                variable = ExpressionHandles.getVariable(data, target);
                value = variable.value();
                switch (type) {
                    case 16: {
                        value = Math.pow(value, arg);
                        break;
                    }
                    case 13: {
                        value *= arg;
                        break;
                    }
                    case 14: {
                        value /= arg;
                        break;
                    }
                    case 15: {
                        value %= arg;
                        break;
                    }
                    case 11: {
                        value += arg;
                        break;
                    }
                    case 12: {
                        value -= arg;
                        break;
                    }
                    default: {
                        throw ExpressionHelper.evalException(ctx, "Invalid text for assign expr: " + ctx.assignmentOperator().getText());
                    }
                }
            }
            variable.setValue(value);
            return value;
        });
    }

    @Override
    public MethodHandle visitFunctionCall(ExpressionParser.FunctionCallContext ctx) {
        MethodHandle handle = ExpressionHelper.resolveFunction(this.functions, ctx);
        String fnName = ctx.name.getText();
        MethodHandle[] arguments = new MethodHandle[ctx.args.size()];
        for (int i = 0; i < arguments.length; ++i) {
            Class<?> rtype;
            ExpressionParser.ExpressionContext arg = ctx.args.get(i);
            MethodHandle transformed = this.getArgument(fnName, handle.type(), i, arg);
            Class<?> ptype = handle.type().parameterType(i);
            if (ptype != (rtype = transformed.type().returnType()) && ptype.isAssignableFrom(rtype)) {
                transformed = transformed.asType(transformed.type().changeReturnType(ptype));
            }
            arguments[i] = transformed;
        }
        MethodHandle manyData = MethodHandles.filterArguments(handle, 0, arguments);
        int[] permutation = new int[arguments.length];
        return MethodHandles.permuteArguments(manyData, ExpressionHandles.COMPILED_EXPRESSION_SIG, permutation);
    }

    private MethodHandle getArgument(String fnName, MethodType type, int i, ParserRuleContext arg) {
        String handleName = ExpressionHelper.getArgumentHandleName(fnName, type, i, arg);
        if (handleName == null) {
            return this.evaluateForValue(arg);
        }
        if (handleName.equals("<wrapped constant>")) {
            MethodHandle filter = this.evaluateForValue(arg);
            filter = filter.asType(filter.type().unwrap());
            return MethodHandles.collectArguments(ExpressionHandles.NEW_LS_CONSTANT, 0, filter);
        }
        CommonToken fake = new CommonToken(arg.start);
        fake.setText(handleName);
        return ExpressionHandles.mhGetVariable(fake);
    }

    @Override
    public MethodHandle visitConstantExpression(ExpressionParser.ConstantExpressionContext ctx) {
        return ExpressionHandles.dropData(MethodHandles.constant(Double.class, Double.parseDouble(ctx.getText())));
    }

    @Override
    public MethodHandle visitIdExpr(ExpressionParser.IdExprContext ctx) {
        Token source = ctx.source;
        return ExpressionHandles.call(data -> ExpressionHandles.getSlotValue(data, source));
    }

    @Override
    protected MethodHandle defaultResult() {
        return DEFAULT_RESULT;
    }

    @Override
    public MethodHandle visitChildren(RuleNode node) {
        ParseTree c;
        MethodHandle result = this.defaultResult();
        int n = node.getChildCount();
        for (int i = 0; !(i >= n || (c = node.getChild(i)) instanceof TerminalNode && ((TerminalNode)c).getSymbol().getType() == -1); ++i) {
            MethodHandle childResult = c.accept(this);
            if (c instanceof ParserRuleContext) {
                this.checkHandle(childResult, (ParserRuleContext)c);
            }
            result = this.aggregateHandleResult(result, childResult);
        }
        return result;
    }

    @Override
    protected MethodHandle aggregateResult(MethodHandle aggregate, MethodHandle nextResult) {
        throw new UnsupportedOperationException();
    }

    private MethodHandle aggregateHandleResult(MethodHandle oldResult, MethodHandle result) {
        if (oldResult == DEFAULT_RESULT) {
            return result;
        }
        if (result == DEFAULT_RESULT) {
            return oldResult;
        }
        MethodHandle dummyDouble = MethodHandles.dropArguments(result, 1, Double.class);
        MethodHandle doubledData = MethodHandles.collectArguments(dummyDouble, 1, oldResult);
        return ExpressionHandles.dedupData(doubledData);
    }

    static {
        CompilingVisitor.factorials[0] = 1.0;
        for (int i = 1; i < factorials.length; ++i) {
            CompilingVisitor.factorials[i] = factorials[i - 1] * (double)i;
        }
        DEFAULT_RESULT = ExpressionHandles.dropData(MethodHandles.constant(Double.class, null));
    }
}

