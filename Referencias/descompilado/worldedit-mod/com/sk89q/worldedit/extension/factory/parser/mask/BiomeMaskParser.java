/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Splitter
 */
package com.sk89q.worldedit.extension.factory.parser.mask;

import com.google.common.base.Splitter;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.command.util.SuggestionHelper;
import com.sk89q.worldedit.extension.input.InputParseException;
import com.sk89q.worldedit.extension.input.NoMatchException;
import com.sk89q.worldedit.extension.input.ParserContext;
import com.sk89q.worldedit.function.mask.BiomeMask;
import com.sk89q.worldedit.function.mask.Mask;
import com.sk89q.worldedit.internal.registry.InputParser;
import com.sk89q.worldedit.util.formatting.text.TextComponent;
import com.sk89q.worldedit.util.formatting.text.TranslatableComponent;
import com.sk89q.worldedit.world.biome.BiomeType;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BiomeMaskParser
extends InputParser<Mask> {
    public BiomeMaskParser(WorldEdit worldEdit) {
        super(worldEdit);
    }

    @Override
    public Stream<String> getSuggestions(String input, ParserContext context) {
        if (input.isEmpty()) {
            return Stream.of("$");
        }
        if (input.charAt(0) == '$') {
            int lastTermIdx = (input = input.substring(1)).lastIndexOf(44);
            if (lastTermIdx <= 0) {
                return SuggestionHelper.getNamespacedRegistrySuggestions(BiomeType.REGISTRY, input).map(s -> "$" + s);
            }
            String prev = input.substring(0, lastTermIdx) + ",";
            Set prevBiomes = Arrays.stream(prev.split(",", 0)).collect(Collectors.toSet());
            String search = input.substring(lastTermIdx + 1);
            return SuggestionHelper.getNamespacedRegistrySuggestions(BiomeType.REGISTRY, search).filter(s -> !prevBiomes.contains(s)).map(s -> "$" + prev + s);
        }
        return Stream.empty();
    }

    @Override
    public Mask parseFromInput(String input, ParserContext context) throws InputParseException {
        if (!input.startsWith("$")) {
            return null;
        }
        HashSet<BiomeType> biomes = new HashSet<BiomeType>();
        for (String biomeName : Splitter.on((String)",").split((CharSequence)input.substring(1))) {
            BiomeType biome = BiomeType.REGISTRY.get(biomeName);
            if (biome == null) {
                throw new NoMatchException(TranslatableComponent.of("worldedit.error.unknown-biome", TextComponent.of(biomeName)));
            }
            biomes.add(biome);
        }
        return new BiomeMask(context.requireExtent(), biomes);
    }
}

