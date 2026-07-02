/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.extension.factory.parser.mask;

import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.extension.input.InputParseException;
import com.sk89q.worldedit.extension.input.ParserContext;
import com.sk89q.worldedit.function.mask.Mask;
import com.sk89q.worldedit.function.mask.Masks;
import com.sk89q.worldedit.internal.registry.InputParser;
import com.sk89q.worldedit.util.formatting.text.TranslatableComponent;
import java.util.stream.Stream;

public class NegateMaskParser
extends InputParser<Mask> {
    public NegateMaskParser(WorldEdit worldEdit) {
        super(worldEdit);
    }

    @Override
    public Stream<String> getSuggestions(String input, ParserContext context) {
        if (input.isEmpty()) {
            return Stream.of("!");
        }
        if (input.charAt(0) != '!') {
            return Stream.empty();
        }
        return this.worldEdit.getMaskFactory().getSuggestions(input.substring(1), context).stream().map(s -> "!" + s);
    }

    @Override
    public Mask parseFromInput(String input, ParserContext context) throws InputParseException {
        if (!input.startsWith("!")) {
            return null;
        }
        if (input.length() > 1) {
            return Masks.negate(this.worldEdit.getMaskFactory().parseFromInput(input.substring(1), context));
        }
        throw new InputParseException(TranslatableComponent.of("worldedit.error.parser.negate-nothing"));
    }
}

