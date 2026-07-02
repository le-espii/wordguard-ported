/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.internal.registry;

import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.extension.input.InputParseException;
import com.sk89q.worldedit.extension.input.ParserContext;
import java.util.stream.Stream;

public abstract class InputParser<E> {
    protected final WorldEdit worldEdit;

    protected InputParser(WorldEdit worldEdit) {
        this.worldEdit = worldEdit;
    }

    public abstract E parseFromInput(String var1, ParserContext var2) throws InputParseException;

    @Deprecated(forRemoval=true)
    public Stream<String> getSuggestions(String input) {
        return Stream.empty();
    }

    public Stream<String> getSuggestions(String input, ParserContext context) {
        return this.getSuggestions(input);
    }
}

