/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.internal.registry;

import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.extension.input.InputParseException;
import com.sk89q.worldedit.extension.input.ParserContext;
import com.sk89q.worldedit.internal.registry.InputParser;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;

public abstract class SimpleInputParser<E>
extends InputParser<E> {
    protected SimpleInputParser(WorldEdit worldEdit) {
        super(worldEdit);
    }

    public abstract List<String> getMatchedAliases();

    @Override
    public E parseFromInput(String input, ParserContext context) throws InputParseException {
        if (!this.getMatchedAliases().contains(input)) {
            return null;
        }
        return this.parseFromSimpleInput(input, context);
    }

    public abstract E parseFromSimpleInput(String var1, ParserContext var2) throws InputParseException;

    public String getPrimaryMatcher() {
        return this.getMatchedAliases().get(0);
    }

    @Override
    public Stream<String> getSuggestions(String input, ParserContext context) {
        if (input.isEmpty()) {
            return Stream.of(this.getPrimaryMatcher());
        }
        String prefix = input.toLowerCase(Locale.ROOT);
        for (String alias : this.getMatchedAliases()) {
            if (!alias.startsWith(prefix)) continue;
            return Stream.of(alias);
        }
        return Stream.empty();
    }
}

