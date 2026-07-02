/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.extension.factory.parser.pattern;

import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.extension.input.InputParseException;
import com.sk89q.worldedit.extension.input.ParserContext;
import com.sk89q.worldedit.function.pattern.Pattern;
import com.sk89q.worldedit.function.pattern.RandomStatePattern;
import com.sk89q.worldedit.internal.registry.InputParser;
import com.sk89q.worldedit.world.block.BaseBlock;
import com.sk89q.worldedit.world.block.FuzzyBlockState;
import java.util.stream.Stream;

public class RandomStatePatternParser
extends InputParser<Pattern> {
    public RandomStatePatternParser(WorldEdit worldEdit) {
        super(worldEdit);
    }

    @Override
    public Stream<String> getSuggestions(String input, ParserContext context) {
        if (input.isEmpty()) {
            return Stream.of("*");
        }
        if (!input.startsWith("*")) {
            return Stream.empty();
        }
        return this.worldEdit.getBlockFactory().getSuggestions(input.substring(1), context).stream().map(s -> "*" + s);
    }

    @Override
    public Pattern parseFromInput(String input, ParserContext context) throws InputParseException {
        if (!input.startsWith("*")) {
            return null;
        }
        boolean wasFuzzy = context.isPreferringWildcard();
        context.setPreferringWildcard(true);
        BaseBlock block = (BaseBlock)this.worldEdit.getBlockFactory().parseFromInput(input.substring(1), context);
        context.setPreferringWildcard(wasFuzzy);
        if (block.getStates().size() == block.getBlockType().getPropertyMap().size()) {
            return block;
        }
        if (block.toImmutableState() instanceof FuzzyBlockState) {
            return new RandomStatePattern((FuzzyBlockState)block.toImmutableState());
        }
        return null;
    }
}

