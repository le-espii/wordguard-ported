/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.internal.expression.invoke;

import com.sk89q.worldedit.antlr.ExpressionParser;
import com.sk89q.worldedit.internal.expression.CompiledExpression;
import com.sk89q.worldedit.internal.expression.Functions;
import com.sk89q.worldedit.internal.expression.invoke.CompilingVisitor;
import com.sk89q.worldedit.internal.expression.invoke.ExpressionHandles;
import com.sk89q.worldedit.internal.expression.invoke.ReturnException;
import java.lang.invoke.LambdaConversionException;
import java.lang.invoke.LambdaMetafactory;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

public class ExpressionCompiler {
    private static final String CE_EXECUTE = "execute";
    private static final MethodType HANDLE_TO_CE = MethodType.methodType(CompiledExpression.class, MethodHandle.class);
    private static final MethodHandle HANDLE_TO_CE_CONVERTER;

    public CompiledExpression compileExpression(ExpressionParser.AllStatementsContext root, Functions functions) {
        MethodHandle invokable = root.accept(new CompilingVisitor(functions));
        MethodHandle finalInvokable = invokable = MethodHandles.catchException(invokable, ReturnException.class, ExpressionHandles.RETURN_EXCEPTION_GET_RESULT);
        return (CompiledExpression)ExpressionHandles.safeInvoke(HANDLE_TO_CE_CONVERTER, h -> h.invoke(finalInvokable));
    }

    static {
        MethodHandle handleInvoker = MethodHandles.invoker(ExpressionHandles.COMPILED_EXPRESSION_SIG);
        try {
            HANDLE_TO_CE_CONVERTER = LambdaMetafactory.metafactory(MethodHandles.lookup(), CE_EXECUTE, HANDLE_TO_CE, ExpressionHandles.COMPILED_EXPRESSION_SIG, handleInvoker, ExpressionHandles.COMPILED_EXPRESSION_SIG).dynamicInvoker().asType(HANDLE_TO_CE);
        }
        catch (LambdaConversionException e) {
            throw new IllegalStateException("Failed to load ExpressionCompiler MetaFactory", e);
        }
    }
}

