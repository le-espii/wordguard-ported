/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 */
package com.sk89q.worldedit.internal.registry;

import com.google.common.base.Preconditions;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.extension.input.InputParseException;
import com.sk89q.worldedit.extension.input.NoMatchException;
import com.sk89q.worldedit.extension.input.ParserContext;
import com.sk89q.worldedit.internal.registry.InputParser;
import com.sk89q.worldedit.util.formatting.text.TextComponent;
import com.sk89q.worldedit.util.formatting.text.TranslatableComponent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public abstract class AbstractFactory<E> {
    protected final WorldEdit worldEdit;
    private final List<InputParser<E>> parsers = new ArrayList<InputParser<E>>();

    protected AbstractFactory(WorldEdit worldEdit, InputParser<E> defaultParser) {
        Preconditions.checkNotNull((Object)worldEdit);
        Preconditions.checkNotNull(defaultParser);
        this.worldEdit = worldEdit;
        this.parsers.add(defaultParser);
    }

    public List<InputParser<E>> getParsers() {
        return Collections.unmodifiableList(this.parsers);
    }

    public E parseFromInput(String input, ParserContext context) throws InputParseException {
        for (InputParser<E> parser : this.parsers) {
            E match = parser.parseFromInput(input, context);
            if (match == null) continue;
            return match;
        }
        throw new NoMatchException(TranslatableComponent.of("worldedit.error.no-match", TextComponent.of(input)));
    }

    @Deprecated
    public List<String> getSuggestions(String input) {
        return this.getSuggestions(input, new ParserContext());
    }

    public List<String> getSuggestions(String input, ParserContext context) {
        return this.parsers.stream().flatMap(p -> p.getSuggestions(input, context)).collect(Collectors.toList());
    }

    public void register(InputParser<E> inputParser) {
        Preconditions.checkNotNull(inputParser);
        this.parsers.add(this.parsers.size() - 1, inputParser);
    }
}

