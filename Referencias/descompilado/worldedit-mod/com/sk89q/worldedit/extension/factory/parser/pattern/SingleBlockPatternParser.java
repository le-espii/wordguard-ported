/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.extension.factory.parser.pattern;

import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.extension.input.InputParseException;
import com.sk89q.worldedit.extension.input.ParserContext;
import com.sk89q.worldedit.function.pattern.Pattern;
import com.sk89q.worldedit.internal.registry.InputParser;
import java.util.stream.Stream;

public class SingleBlockPatternParser
extends InputParser<Pattern> {
    public SingleBlockPatternParser(WorldEdit worldEdit) {
        super(worldEdit);
    }

    @Override
    public Stream<String> getSuggestions(String input, ParserContext context) {
        return this.worldEdit.getBlockFactory().getSuggestions(input, context).stream();
    }

    @Override
    public Pattern parseFromInput(String input, ParserContext context) throws InputParseException {
        return (Pattern)this.worldEdit.getBlockFactory().parseFromInput(input, context);
    }
}

