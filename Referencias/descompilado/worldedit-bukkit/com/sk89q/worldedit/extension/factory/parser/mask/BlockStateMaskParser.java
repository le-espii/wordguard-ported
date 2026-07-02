/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Splitter
 */
package com.sk89q.worldedit.extension.factory.parser.mask;

import com.google.common.base.Splitter;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.extension.input.InputParseException;
import com.sk89q.worldedit.extension.input.ParserContext;
import com.sk89q.worldedit.function.mask.BlockStateMask;
import com.sk89q.worldedit.function.mask.Mask;
import com.sk89q.worldedit.internal.registry.InputParser;
import com.sk89q.worldedit.util.formatting.text.TextComponent;
import com.sk89q.worldedit.util.formatting.text.TranslatableComponent;
import java.util.stream.Stream;

public class BlockStateMaskParser
extends InputParser<Mask> {
    public BlockStateMaskParser(WorldEdit worldEdit) {
        super(worldEdit);
    }

    @Override
    public Stream<String> getSuggestions(String input, ParserContext context) {
        if (input.isEmpty()) {
            return Stream.of("^[", "^=[");
        }
        return Stream.of("^[", "^=[").filter(s -> s.startsWith(input));
    }

    @Override
    public Mask parseFromInput(String input, ParserContext context) throws InputParseException {
        if (!input.startsWith("^[") && !input.startsWith("^=[") || !input.endsWith("]")) {
            return null;
        }
        boolean strict = input.charAt(1) == '=';
        String states = input.substring(2 + (strict ? 1 : 0), input.length() - 1);
        try {
            return new BlockStateMask(context.requireExtent(), Splitter.on((char)',').omitEmptyStrings().trimResults().withKeyValueSeparator('=').split((CharSequence)states), strict);
        }
        catch (Exception e) {
            throw new InputParseException(TranslatableComponent.of("worldedit.error.parser.bad-state-format", TextComponent.of(states)), (Throwable)e);
        }
    }
}

