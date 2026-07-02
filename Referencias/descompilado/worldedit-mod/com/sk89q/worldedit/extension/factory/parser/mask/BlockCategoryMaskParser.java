/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.extension.factory.parser.mask;

import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.command.util.SuggestionHelper;
import com.sk89q.worldedit.extension.input.InputParseException;
import com.sk89q.worldedit.extension.input.NoMatchException;
import com.sk89q.worldedit.extension.input.ParserContext;
import com.sk89q.worldedit.function.mask.BlockCategoryMask;
import com.sk89q.worldedit.function.mask.Mask;
import com.sk89q.worldedit.internal.registry.InputParser;
import com.sk89q.worldedit.util.formatting.text.TextComponent;
import com.sk89q.worldedit.util.formatting.text.TranslatableComponent;
import com.sk89q.worldedit.world.block.BlockCategory;
import java.util.Locale;
import java.util.stream.Stream;

public class BlockCategoryMaskParser
extends InputParser<Mask> {
    public BlockCategoryMaskParser(WorldEdit worldEdit) {
        super(worldEdit);
    }

    @Override
    public Stream<String> getSuggestions(String input, ParserContext context) {
        return SuggestionHelper.getBlockCategorySuggestions(input, false);
    }

    @Override
    public Mask parseFromInput(String input, ParserContext context) throws InputParseException {
        if (!input.startsWith("##")) {
            return null;
        }
        BlockCategory category = BlockCategory.REGISTRY.get(input.substring(2).toLowerCase(Locale.ROOT));
        if (category == null) {
            throw new NoMatchException(TranslatableComponent.of("worldedit.error.unknown-tag", TextComponent.of(input.substring(2))));
        }
        return new BlockCategoryMask(context.requireExtent(), category);
    }
}

