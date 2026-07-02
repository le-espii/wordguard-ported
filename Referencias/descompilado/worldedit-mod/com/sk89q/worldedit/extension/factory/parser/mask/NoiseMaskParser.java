/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.extension.factory.parser.mask;

import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.extension.input.ParserContext;
import com.sk89q.worldedit.function.mask.Mask;
import com.sk89q.worldedit.function.mask.NoiseFilter;
import com.sk89q.worldedit.internal.registry.InputParser;
import com.sk89q.worldedit.math.noise.RandomNoise;
import java.util.stream.Stream;

public class NoiseMaskParser
extends InputParser<Mask> {
    public NoiseMaskParser(WorldEdit worldEdit) {
        super(worldEdit);
    }

    @Override
    public Stream<String> getSuggestions(String input, ParserContext context) {
        if (input.isEmpty()) {
            return Stream.of("%");
        }
        if (input.charAt(0) != '%') {
            return Stream.empty();
        }
        return Stream.of("%10", "%25", "%50", "%75").filter(s -> s.startsWith(input));
    }

    @Override
    public Mask parseFromInput(String input, ParserContext context) {
        if (!input.startsWith("%")) {
            return null;
        }
        int i = Integer.parseInt(input.substring(1));
        return new NoiseFilter(new RandomNoise(), (double)i / 100.0);
    }
}

