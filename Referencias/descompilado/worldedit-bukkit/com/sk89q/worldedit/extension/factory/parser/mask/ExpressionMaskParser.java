/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.extension.factory.parser.mask;

import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.extension.input.InputParseException;
import com.sk89q.worldedit.extension.input.ParserContext;
import com.sk89q.worldedit.extension.platform.Actor;
import com.sk89q.worldedit.function.mask.ExpressionMask;
import com.sk89q.worldedit.function.mask.Mask;
import com.sk89q.worldedit.internal.expression.Expression;
import com.sk89q.worldedit.internal.expression.ExpressionException;
import com.sk89q.worldedit.internal.registry.InputParser;
import com.sk89q.worldedit.math.Vector3;
import com.sk89q.worldedit.regions.shape.WorldEditExpressionEnvironment;
import com.sk89q.worldedit.util.formatting.text.TranslatableComponent;
import java.util.function.IntSupplier;
import java.util.stream.Stream;

public class ExpressionMaskParser
extends InputParser<Mask> {
    public ExpressionMaskParser(WorldEdit worldEdit) {
        super(worldEdit);
    }

    @Override
    public Stream<String> getSuggestions(String input, ParserContext context) {
        if (input.isEmpty()) {
            return Stream.of("=");
        }
        return Stream.empty();
    }

    @Override
    public Mask parseFromInput(String input, ParserContext context) throws InputParseException {
        if (!input.startsWith("=")) {
            return null;
        }
        try {
            Expression exp = Expression.compile(input.substring(1), "x", "y", "z");
            WorldEditExpressionEnvironment env = new WorldEditExpressionEnvironment(context.requireExtent(), Vector3.ONE, Vector3.ZERO);
            exp.setEnvironment(env);
            if (context.getActor() != null) {
                Actor owner = context.getActor();
                IntSupplier timeout = () -> WorldEdit.getInstance().getSessionManager().get(owner).getTimeout();
                return new ExpressionMask(exp, timeout);
            }
            return new ExpressionMask(exp);
        }
        catch (ExpressionException e) {
            throw new InputParseException(TranslatableComponent.of("worldedit.error.parser.invalid-expression"));
        }
    }
}

