/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.extension.factory.parser.mask;

import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.extension.input.InputParseException;
import com.sk89q.worldedit.extension.input.ParserContext;
import com.sk89q.worldedit.function.mask.ExistingBlockMask;
import com.sk89q.worldedit.function.mask.Mask;
import com.sk89q.worldedit.function.mask.OffsetsMask;
import com.sk89q.worldedit.internal.registry.InputParser;
import com.sk89q.worldedit.math.BlockVector3;
import java.util.stream.Stream;

public class OffsetMaskParser
extends InputParser<Mask> {
    public OffsetMaskParser(WorldEdit worldEdit) {
        super(worldEdit);
    }

    @Override
    public Stream<String> getSuggestions(String input, ParserContext context) {
        if (input.isEmpty()) {
            return Stream.of(">", "<");
        }
        char firstChar = input.charAt(0);
        if (firstChar != '>' && firstChar != '<') {
            return Stream.empty();
        }
        return this.worldEdit.getMaskFactory().getSuggestions(input.substring(1), context).stream().map(s -> firstChar + s);
    }

    @Override
    public Mask parseFromInput(String input, ParserContext context) throws InputParseException {
        char firstChar = input.charAt(0);
        if (firstChar != '>' && firstChar != '<') {
            return null;
        }
        Mask submask = input.length() > 1 ? this.worldEdit.getMaskFactory().parseFromInput(input.substring(1), context) : new ExistingBlockMask(context.requireExtent());
        return OffsetsMask.single(submask, BlockVector3.at(0, firstChar == '>' ? -1 : 1, 0));
    }
}

