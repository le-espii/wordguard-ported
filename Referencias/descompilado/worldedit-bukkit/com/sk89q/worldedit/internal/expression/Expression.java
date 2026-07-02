/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ImmutableList
 */
package com.sk89q.worldedit.internal.expression;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.antlr.ExpressionLexer;
import com.sk89q.worldedit.antlr.ExpressionParser;
import com.sk89q.worldedit.antlr4.runtime.CharStreams;
import com.sk89q.worldedit.antlr4.runtime.CodePointCharStream;
import com.sk89q.worldedit.antlr4.runtime.CommonTokenStream;
import com.sk89q.worldedit.antlr4.runtime.misc.ParseCancellationException;
import com.sk89q.worldedit.antlr4.runtime.tree.ParseTreeWalker;
import com.sk89q.worldedit.internal.expression.CompiledExpression;
import com.sk89q.worldedit.internal.expression.EvaluationException;
import com.sk89q.worldedit.internal.expression.ExecutionData;
import com.sk89q.worldedit.internal.expression.ExpressionEnvironment;
import com.sk89q.worldedit.internal.expression.ExpressionException;
import com.sk89q.worldedit.internal.expression.ExpressionValidator;
import com.sk89q.worldedit.internal.expression.Functions;
import com.sk89q.worldedit.internal.expression.LexerErrorListener;
import com.sk89q.worldedit.internal.expression.LocalSlot;
import com.sk89q.worldedit.internal.expression.ParserErrorListener;
import com.sk89q.worldedit.internal.expression.ParserException;
import com.sk89q.worldedit.internal.expression.SlotTable;
import com.sk89q.worldedit.internal.expression.invoke.ExpressionCompiler;
import java.time.Instant;
import java.util.List;
import java.util.Objects;

public class Expression {
    private final String source;
    private final SlotTable slots = new SlotTable();
    private final List<String> providedSlots;
    private final ExpressionParser.AllStatementsContext root;
    private final Functions functions = Functions.create();
    private final CompiledExpression compiledExpression;

    public static Expression compile(String expression, String ... variableNames) throws ExpressionException {
        return new Expression(expression, variableNames);
    }

    private Expression(String expression, String ... variableNames) throws ExpressionException {
        Preconditions.checkNotNull((Object)expression, (Object)"Expression cannot be null.");
        Preconditions.checkArgument((!expression.isEmpty() ? 1 : 0) != 0, (Object)"Expression cannot be empty string.");
        this.source = expression;
        this.slots.putSlot("e", new LocalSlot.Constant(Math.E));
        this.slots.putSlot("pi", new LocalSlot.Constant(Math.PI));
        this.slots.putSlot("true", new LocalSlot.Constant(1.0));
        this.slots.putSlot("false", new LocalSlot.Constant(0.0));
        for (String variableName : variableNames) {
            this.slots.initVariable(variableName).orElseThrow(() -> new ExpressionException(-1, "Tried to overwrite identifier '" + variableName + "'"));
        }
        this.providedSlots = ImmutableList.copyOf((Object[])variableNames);
        CodePointCharStream cs = CharStreams.fromString(expression, "<input>");
        ExpressionLexer lexer = new ExpressionLexer(cs);
        lexer.removeErrorListeners();
        lexer.addErrorListener(new LexerErrorListener());
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        ExpressionParser parser = new ExpressionParser(tokens);
        parser.removeErrorListeners();
        parser.addErrorListener(new ParserErrorListener());
        try {
            this.root = parser.allStatements();
            Objects.requireNonNull(this.root, "Unable to parse root, but no exceptions?");
        }
        catch (ParseCancellationException e) {
            throw new ParserException(parser.getState(), (Throwable)e);
        }
        ParseTreeWalker.DEFAULT.walk(new ExpressionValidator(this.slots.keySet(), this.functions), this.root);
        this.compiledExpression = new ExpressionCompiler().compileExpression(this.root, this.functions);
    }

    public double evaluate(double ... values) throws EvaluationException {
        return this.evaluate(values, WorldEdit.getInstance().getConfiguration().calculationTimeout);
    }

    public double evaluate(double[] values, int timeout) throws EvaluationException {
        for (int i = 0; i < values.length; ++i) {
            String slotName = this.providedSlots.get(i);
            LocalSlot.Variable slot = this.slots.getVariable(slotName).orElseThrow(() -> new EvaluationException(-1, "Tried to assign to non-variable " + slotName + "."));
            slot.setValue(values[i]);
        }
        Instant deadline = Instant.now().plusMillis(timeout);
        Double result = this.compiledExpression.execute(new ExecutionData(this.slots, this.functions, deadline));
        if (result == null) {
            throw new EvaluationException(-1, "Expression must result in a value");
        }
        return result;
    }

    public void optimize() {
    }

    public String getSource() {
        return this.source;
    }

    public String toString() {
        return this.root.toString();
    }

    public SlotTable getSlots() {
        return this.slots;
    }

    public ExpressionEnvironment getEnvironment() {
        return this.functions.getEnvironment();
    }

    public void setEnvironment(ExpressionEnvironment environment) {
        this.functions.setEnvironment(environment);
    }
}

