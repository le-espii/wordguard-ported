/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Throwables
 *  javax.annotation.Nullable
 */
package com.sk89q.worldedit.internal.expression.invoke;

import com.google.common.base.Throwables;
import com.sk89q.worldedit.antlr4.runtime.ParserRuleContext;
import com.sk89q.worldedit.antlr4.runtime.Token;
import com.sk89q.worldedit.bukkit.fastutil.doubles.Double2ObjectMap;
import com.sk89q.worldedit.bukkit.fastutil.doubles.Double2ObjectMaps;
import com.sk89q.worldedit.internal.expression.CompiledExpression;
import com.sk89q.worldedit.internal.expression.EvaluationException;
import com.sk89q.worldedit.internal.expression.ExecutionData;
import com.sk89q.worldedit.internal.expression.ExpressionHelper;
import com.sk89q.worldedit.internal.expression.LocalSlot;
import com.sk89q.worldedit.internal.expression.invoke.BreakException;
import com.sk89q.worldedit.internal.expression.invoke.ExecNode;
import com.sk89q.worldedit.internal.expression.invoke.ReturnException;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.Objects;
import java.util.function.DoubleBinaryOperator;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import javax.annotation.Nullable;

class ExpressionHandles {
    static final MethodType COMPILED_EXPRESSION_SIG = MethodType.methodType(Double.class, ExecutionData.class);
    private static final MethodHandle EVAL_EXCEPTION_CONSTR;
    private static final MethodHandle CALL_EXPRESSION;
    private static final MethodHandle GET_VARIABLE;
    private static final MethodHandle WHILE_FOR_LOOP_IMPL;
    private static final MethodHandle DO_WHILE_LOOP_IMPL;
    private static final MethodHandle SIMPLE_FOR_LOOP_IMPL;
    private static final MethodHandle SWITCH_IMPL;
    static final MethodHandle IS_NULL;
    static final MethodHandle DOUBLE_TO_BOOL;
    static final MethodHandle CALL_BINARY_OP;
    static final MethodHandle NEW_LS_CONSTANT;
    static final MethodHandle NEW_RETURN_EXCEPTION;
    static final MethodHandle RETURN_EXCEPTION_GET_RESULT;
    static final MethodHandle NULL_DOUBLE;

    static MethodHandle boxDoubles(MethodHandle handle) {
        MethodType type = handle.type();
        type = MethodType.methodType(ExpressionHandles.boxIfPrimitiveDouble(type.returnType()), type.parameterList().stream().map(ExpressionHandles::boxIfPrimitiveDouble).collect(Collectors.toList()));
        return handle.asType(type);
    }

    private static Class<?> boxIfPrimitiveDouble(Class<?> clazz) {
        return clazz == Double.TYPE ? Double.class : clazz;
    }

    static MethodHandle unboxDoubles(MethodHandle handle) {
        MethodType type = handle.type();
        type = MethodType.methodType(ExpressionHandles.unboxIfDouble(type.returnType()), type.parameterList().stream().map(ExpressionHandles::unboxIfDouble).collect(Collectors.toList()));
        return handle.asType(type);
    }

    private static Class<?> unboxIfDouble(Class<?> clazz) {
        return clazz == Double.class ? Double.TYPE : clazz;
    }

    static Object safeInvoke(MethodHandle handle, Invokable invokable) {
        try {
            return invokable.invoke(handle);
        }
        catch (Throwable t) {
            Throwables.throwIfUnchecked((Throwable)t);
            throw new RuntimeException(t);
        }
    }

    static Object standardInvoke(MethodHandle handle, ExecutionData data) {
        return ExpressionHandles.safeInvoke(handle, h -> h.invoke(data));
    }

    static Object constantInvoke(MethodHandle handle) {
        return ExpressionHandles.standardInvoke(handle, ExecutionData.CONSTANT_EVALUATOR);
    }

    static MethodHandle dropData(MethodHandle handle) {
        return MethodHandles.dropArguments(handle, 0, ExecutionData.class);
    }

    static MethodHandle dedupData(MethodHandle doubleData) {
        return MethodHandles.permuteArguments(doubleData, COMPILED_EXPRESSION_SIG, 0, 0);
    }

    static LocalSlot.Variable initVariable(ExecutionData data, Token nameToken) {
        String name = nameToken.getText();
        return data.slots().initVariable(name).orElseThrow(() -> ExpressionHelper.evalException(nameToken, "Cannot overwrite non-variable '" + name + "'"));
    }

    private static Supplier<EvaluationException> varNotInitException(Token nameToken) {
        return () -> ExpressionHelper.evalException(nameToken, "'" + nameToken.getText() + "' is not initialized yet");
    }

    static MethodHandle mhGetVariable(Token nameToken) {
        return MethodHandles.insertArguments(GET_VARIABLE, 1, nameToken);
    }

    static LocalSlot.Variable getVariable(ExecutionData data, Token nameToken) {
        String name = nameToken.getText();
        LocalSlot slot = data.slots().getSlot(name).orElseThrow(ExpressionHandles.varNotInitException(nameToken));
        if (!(slot instanceof LocalSlot.Variable)) {
            throw ExpressionHelper.evalException(nameToken, "'" + name + "' is not a variable");
        }
        return (LocalSlot.Variable)slot;
    }

    static double getSlotValue(ExecutionData data, Token nameToken) {
        String name = nameToken.getText();
        return data.slots().getSlotValue(name).orElseThrow(ExpressionHandles.varNotInitException(nameToken));
    }

    private static MethodHandle evalException(ParserRuleContext ctx, String message) {
        return MethodHandles.insertArguments(EVAL_EXCEPTION_CONSTR, 0, ExpressionHelper.getErrorPosition(ctx.start), message);
    }

    static MethodHandle throwEvalException(ParserRuleContext ctx, String message) {
        return MethodHandles.collectArguments(MethodHandles.throwException(Double.class, EvaluationException.class), 0, ExpressionHandles.evalException(ctx, message));
    }

    private static boolean doubleToBool(double bool) {
        return bool != 0.0;
    }

    static double boolToDouble(boolean bool) {
        return bool ? 1.0 : 0.0;
    }

    static MethodHandle call(CompiledExpression runnable) {
        return CALL_EXPRESSION.bindTo(runnable).asType(COMPILED_EXPRESSION_SIG);
    }

    static MethodHandle whileLoop(MethodHandle condition, ExecNode body) {
        return MethodHandles.insertArguments(WHILE_FOR_LOOP_IMPL, 1, new Object[]{null, condition, body, null});
    }

    static MethodHandle forLoop(MethodHandle init, MethodHandle condition, ExecNode body, MethodHandle update) {
        return MethodHandles.insertArguments(WHILE_FOR_LOOP_IMPL, 1, new Object[]{init, condition, body, update});
    }

    private static Double whileForLoopImpl(ExecutionData data, @Nullable MethodHandle init, MethodHandle condition, ExecNode body, @Nullable MethodHandle update) {
        Double result = null;
        int iterations = 0;
        if (init != null) {
            ExpressionHandles.standardInvoke(init, data);
        }
        while (((Boolean)ExpressionHandles.standardInvoke(condition, data)).booleanValue()) {
            ExpressionHelper.checkIterations(iterations, body.positionInLine());
            data.checkDeadline();
            ++iterations;
            try {
                result = (Double)ExpressionHandles.standardInvoke(body.handle(), data);
            }
            catch (BreakException ex) {
                if (!ex.doContinue) break;
            }
            if (update == null) continue;
            ExpressionHandles.standardInvoke(update, data);
        }
        return result;
    }

    static MethodHandle doWhileLoop(MethodHandle condition, ExecNode body) {
        return MethodHandles.insertArguments(DO_WHILE_LOOP_IMPL, 1, new Object[]{condition, body});
    }

    private static Double doWhileLoopImpl(ExecutionData data, MethodHandle condition, ExecNode body) {
        Double result = null;
        int iterations = 0;
        do {
            ExpressionHelper.checkIterations(iterations, body.positionInLine());
            data.checkDeadline();
            ++iterations;
            try {
                result = (Double)ExpressionHandles.standardInvoke(body.handle(), data);
            }
            catch (BreakException ex) {
                if (!ex.doContinue) break;
            }
        } while (((Boolean)ExpressionHandles.standardInvoke(condition, data)).booleanValue());
        return result;
    }

    static MethodHandle simpleForLoop(MethodHandle first, MethodHandle last, Token counter, ExecNode body) {
        return MethodHandles.insertArguments(SIMPLE_FOR_LOOP_IMPL, 1, new Object[]{first, last, counter, body});
    }

    private static Double simpleForLoopImpl(ExecutionData data, MethodHandle getFirst, MethodHandle getLast, Token counterToken, ExecNode body) {
        Double result = null;
        int iterations = 0;
        double first = (Double)ExpressionHandles.standardInvoke(getFirst, data);
        double last = (Double)ExpressionHandles.standardInvoke(getLast, data);
        LocalSlot.Variable variable = ExpressionHandles.initVariable(data, counterToken);
        for (double i = first; i <= last; i += 1.0) {
            ExpressionHelper.checkIterations(iterations, body.positionInLine());
            data.checkDeadline();
            ++iterations;
            variable.setValue(i);
            try {
                result = (Double)ExpressionHandles.standardInvoke(body.handle(), data);
                continue;
            }
            catch (BreakException ex) {
                if (!ex.doContinue) break;
            }
        }
        return result;
    }

    static MethodHandle switchStatement(Double2ObjectMap<ExecNode> cases, MethodHandle getValue, @Nullable ExecNode defaultCase) {
        return MethodHandles.insertArguments(SWITCH_IMPL, 1, new Object[]{cases, getValue, defaultCase});
    }

    private static Double switchImpl(ExecutionData data, Double2ObjectMap<ExecNode> cases, MethodHandle getValue, @Nullable ExecNode defaultCase) {
        double value = (Double)ExpressionHandles.standardInvoke(getValue, data);
        boolean matched = false;
        Double evaluated = null;
        boolean falling = false;
        for (Double2ObjectMap.Entry entry : Double2ObjectMaps.fastIterable(cases)) {
            if (!falling && entry.getDoubleKey() != value) continue;
            matched = true;
            try {
                evaluated = (Double)ExpressionHandles.standardInvoke(((ExecNode)((Object)entry.getValue())).handle(), data);
                falling = true;
            }
            catch (BreakException brk) {
                ExpressionHelper.check(!brk.doContinue, ((ExecNode)((Object)entry.getValue())).positionInLine(), "Cannot continue in a switch");
                falling = false;
                break;
            }
        }
        if ((falling || !matched) && defaultCase != null) {
            try {
                evaluated = (Double)ExpressionHandles.standardInvoke(defaultCase.handle(), data);
            }
            catch (BreakException brk) {
                ExpressionHelper.check(!brk.doContinue, defaultCase.positionInLine(), "Cannot continue in a switch");
            }
        }
        return evaluated;
    }

    private ExpressionHandles() {
    }

    static {
        NULL_DOUBLE = ExpressionHandles.dropData(MethodHandles.constant(Double.class, null));
        MethodHandles.Lookup lookup = MethodHandles.lookup();
        try {
            EVAL_EXCEPTION_CONSTR = lookup.findConstructor(EvaluationException.class, MethodType.methodType(Void.TYPE, Integer.TYPE, String.class));
            CALL_EXPRESSION = lookup.findVirtual(CompiledExpression.class, "execute", MethodType.methodType(Double.class, ExecutionData.class));
            GET_VARIABLE = lookup.findStatic(ExpressionHandles.class, "getVariable", MethodType.methodType(LocalSlot.Variable.class, ExecutionData.class, Token.class));
            WHILE_FOR_LOOP_IMPL = lookup.findStatic(ExpressionHandles.class, "whileForLoopImpl", MethodType.methodType(Double.class, ExecutionData.class, MethodHandle.class, MethodHandle.class, ExecNode.class, MethodHandle.class));
            DO_WHILE_LOOP_IMPL = lookup.findStatic(ExpressionHandles.class, "doWhileLoopImpl", MethodType.methodType(Double.class, ExecutionData.class, MethodHandle.class, ExecNode.class));
            SIMPLE_FOR_LOOP_IMPL = lookup.findStatic(ExpressionHandles.class, "simpleForLoopImpl", MethodType.methodType(Double.class, ExecutionData.class, MethodHandle.class, MethodHandle.class, Token.class, ExecNode.class));
            SWITCH_IMPL = lookup.findStatic(ExpressionHandles.class, "switchImpl", MethodType.methodType(Double.class, ExecutionData.class, Double2ObjectMap.class, MethodHandle.class, ExecNode.class));
            IS_NULL = lookup.findStatic(Objects.class, "isNull", MethodType.methodType(Boolean.TYPE, Object.class));
            DOUBLE_TO_BOOL = ExpressionHandles.boxDoubles(lookup.findStatic(ExpressionHandles.class, "doubleToBool", MethodType.methodType(Boolean.TYPE, Double.TYPE)));
            CALL_BINARY_OP = lookup.findVirtual(DoubleBinaryOperator.class, "applyAsDouble", MethodType.methodType(Double.TYPE, Double.TYPE, Double.TYPE)).asType(MethodType.methodType(Double.class, DoubleBinaryOperator.class, Double.TYPE, Double.TYPE));
            NEW_LS_CONSTANT = lookup.findConstructor(LocalSlot.Constant.class, MethodType.methodType(Void.TYPE, Double.TYPE));
            NEW_RETURN_EXCEPTION = lookup.findConstructor(ReturnException.class, MethodType.methodType(Void.TYPE, Double.class));
            RETURN_EXCEPTION_GET_RESULT = lookup.findVirtual(ReturnException.class, "getResult", MethodType.methodType(Double.class));
        }
        catch (IllegalAccessException | NoSuchMethodException e) {
            throw new IllegalStateException(e);
        }
    }

    @FunctionalInterface
    static interface Invokable {
        public Object invoke(MethodHandle var1) throws Throwable;
    }
}

