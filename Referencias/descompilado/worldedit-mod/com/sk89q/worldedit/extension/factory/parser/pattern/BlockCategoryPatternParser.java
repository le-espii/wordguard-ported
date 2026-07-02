/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.extension.factory.parser.pattern;

import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.command.util.SuggestionHelper;
import com.sk89q.worldedit.extension.input.InputParseException;
import com.sk89q.worldedit.extension.input.NoMatchException;
import com.sk89q.worldedit.extension.input.ParserContext;
import com.sk89q.worldedit.function.pattern.Pattern;
import com.sk89q.worldedit.function.pattern.RandomPattern;
import com.sk89q.worldedit.internal.registry.InputParser;
import com.sk89q.worldedit.util.formatting.text.TextComponent;
import com.sk89q.worldedit.util.formatting.text.TranslatableComponent;
import com.sk89q.worldedit.world.block.BlockCategory;
import com.sk89q.worldedit.world.block.BlockType;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Stream;

public class BlockCategoryPatternParser
extends InputParser<Pattern> {
    public BlockCategoryPatternParser(WorldEdit worldEdit) {
        super(worldEdit);
    }

    @Override
    public Stream<String> getSuggestions(String input, ParserContext context) {
        return SuggestionHelper.getBlockCategorySuggestions(input, true);
    }

    @Override
    public Pattern parseFromInput(String input, ParserContext context) throws InputParseException {
        BlockCategory category;
        if (!input.startsWith("##")) {
            return null;
        }
        String tag = input.substring(2).toLowerCase(Locale.ROOT);
        boolean anyState = false;
        if (tag.startsWith("*")) {
            tag = tag.substring(1);
            anyState = true;
        }
        if ((category = BlockCategory.REGISTRY.get(tag)) == null) {
            throw new NoMatchException(TranslatableComponent.of("worldedit.error.unknown-tag", TextComponent.of(tag)));
        }
        RandomPattern randomPattern = new RandomPattern();
        Set blocks = category.getAll();
        if (blocks.isEmpty()) {
            throw new InputParseException(TranslatableComponent.of("worldedit.error.empty-tag", TextComponent.of(category.id())));
        }
        if (anyState) {
            blocks.stream().flatMap(blockType -> blockType.getAllStates().stream()).forEach(state -> randomPattern.add((Pattern)state, 1.0));
        } else {
            for (BlockType blockType2 : blocks) {
                randomPattern.add(blockType2.getDefaultState(), 1.0);
            }
        }
        return randomPattern;
    }
}

