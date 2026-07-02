/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.extension.factory.parser.pattern;

import com.sk89q.util.StringUtil;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.extension.input.InputParseException;
import com.sk89q.worldedit.extension.input.ParserContext;
import com.sk89q.worldedit.function.pattern.RandomPattern;
import com.sk89q.worldedit.internal.registry.InputParser;
import com.sk89q.worldedit.util.formatting.text.TextComponent;
import com.sk89q.worldedit.util.formatting.text.TranslatableComponent;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class RandomPatternParser
extends InputParser<com.sk89q.worldedit.function.pattern.Pattern> {
    private final Pattern regex = Pattern.compile("[0-9]+(\\.[0-9]*)?%.*");

    public RandomPatternParser(WorldEdit worldEdit) {
        super(worldEdit);
    }

    @Override
    public Stream<String> getSuggestions(String input, ParserContext context) {
        String[] splits = input.split(",", -1);
        List<String> patterns = StringUtil.parseListInQuotes(splits, ',', new char[]{'[', '{'}, new char[]{']', '}'}, true);
        String percent = null;
        String token = patterns.get(patterns.size() - 1);
        if (this.regex.matcher(token).matches()) {
            String[] p = token.split("%", 2);
            percent = p[0];
            token = p[1];
        } else if (patterns.size() == 1) {
            return Stream.empty();
        }
        String previous = patterns.size() == 1 ? "" : String.join((CharSequence)",", patterns.subList(0, patterns.size() - 1)) + ",";
        String prefix = previous + (String)(percent == null ? "" : percent + "%");
        List<String> innerSuggestions = this.worldEdit.getPatternFactory().getSuggestions(token, context);
        return innerSuggestions.stream().map(s -> prefix + s);
    }

    @Override
    public com.sk89q.worldedit.function.pattern.Pattern parseFromInput(String input, ParserContext context) throws InputParseException {
        RandomPattern randomPattern = new RandomPattern();
        String[] splits = input.split(",", -1);
        List<String> patterns = StringUtil.parseListInQuotes(splits, ',', new char[]{'[', '{'}, new char[]{']', '}'}, true);
        if (patterns.size() == 1) {
            return null;
        }
        for (String token : patterns) {
            com.sk89q.worldedit.function.pattern.Pattern innerPattern;
            double chance;
            if (token.matches("[0-9]+(\\.[0-9]*)?%.*")) {
                String[] p = token.split("%");
                if (p.length < 2) {
                    throw new InputParseException(TranslatableComponent.of("worldedit.error.parser.missing-random-type", TextComponent.of(input)));
                }
                chance = Double.parseDouble(p[0]);
                innerPattern = (com.sk89q.worldedit.function.pattern.Pattern)this.worldEdit.getPatternFactory().parseFromInput(p[1], context);
            } else {
                chance = 1.0;
                innerPattern = (com.sk89q.worldedit.function.pattern.Pattern)this.worldEdit.getPatternFactory().parseFromInput(token, context);
            }
            randomPattern.add(innerPattern, chance);
        }
        return randomPattern;
    }
}

