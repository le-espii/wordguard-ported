/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.extension.factory;

import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.extension.factory.parser.mask.AirMaskParser;
import com.sk89q.worldedit.extension.factory.parser.mask.BiomeMaskParser;
import com.sk89q.worldedit.extension.factory.parser.mask.BlockCategoryMaskParser;
import com.sk89q.worldedit.extension.factory.parser.mask.BlockStateMaskParser;
import com.sk89q.worldedit.extension.factory.parser.mask.BlocksMaskParser;
import com.sk89q.worldedit.extension.factory.parser.mask.ExistingMaskParser;
import com.sk89q.worldedit.extension.factory.parser.mask.ExposedMaskParser;
import com.sk89q.worldedit.extension.factory.parser.mask.ExpressionMaskParser;
import com.sk89q.worldedit.extension.factory.parser.mask.LazyRegionMaskParser;
import com.sk89q.worldedit.extension.factory.parser.mask.NegateMaskParser;
import com.sk89q.worldedit.extension.factory.parser.mask.NoiseMaskParser;
import com.sk89q.worldedit.extension.factory.parser.mask.OffsetMaskParser;
import com.sk89q.worldedit.extension.factory.parser.mask.RegionMaskParser;
import com.sk89q.worldedit.extension.factory.parser.mask.SolidMaskParser;
import com.sk89q.worldedit.extension.input.InputParseException;
import com.sk89q.worldedit.extension.input.NoMatchException;
import com.sk89q.worldedit.extension.input.ParserContext;
import com.sk89q.worldedit.function.mask.Mask;
import com.sk89q.worldedit.function.mask.MaskIntersection;
import com.sk89q.worldedit.internal.registry.AbstractFactory;
import com.sk89q.worldedit.internal.registry.InputParser;
import com.sk89q.worldedit.util.formatting.text.TextComponent;
import com.sk89q.worldedit.util.formatting.text.TranslatableComponent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public final class MaskFactory
extends AbstractFactory<Mask> {
    public MaskFactory(WorldEdit worldEdit) {
        super(worldEdit, new BlocksMaskParser(worldEdit));
        this.register(new ExistingMaskParser(worldEdit));
        this.register(new AirMaskParser(worldEdit));
        this.register(new ExposedMaskParser(worldEdit));
        this.register(new SolidMaskParser(worldEdit));
        this.register(new LazyRegionMaskParser(worldEdit));
        this.register(new RegionMaskParser(worldEdit));
        this.register(new OffsetMaskParser(worldEdit));
        this.register(new NoiseMaskParser(worldEdit));
        this.register(new BlockStateMaskParser(worldEdit));
        this.register(new NegateMaskParser(worldEdit));
        this.register(new ExpressionMaskParser(worldEdit));
        this.register(new BlockCategoryMaskParser(worldEdit));
        this.register(new BiomeMaskParser(worldEdit));
    }

    @Override
    public List<String> getSuggestions(String input, ParserContext context) {
        String[] split = input.split(" ");
        if (split.length > 1) {
            String prev = input.substring(0, input.lastIndexOf(32)) + " ";
            return super.getSuggestions(split[split.length - 1], context).stream().map(s -> prev + s).collect(Collectors.toList());
        }
        return super.getSuggestions(input, context);
    }

    @Override
    public Mask parseFromInput(String input, ParserContext context) throws InputParseException {
        ArrayList<Mask> masks = new ArrayList<Mask>();
        for (String component : input.split(" ")) {
            InputParser parser;
            if (component.isEmpty()) continue;
            Mask match = null;
            Iterator iterator = this.getParsers().iterator();
            while (iterator.hasNext() && (match = (Mask)(parser = iterator.next()).parseFromInput(component, context)) == null) {
            }
            if (match == null) {
                throw new NoMatchException(TranslatableComponent.of("worldedit.error.no-match", TextComponent.of(component)));
            }
            masks.add(match);
        }
        return switch (masks.size()) {
            case 0 -> throw new NoMatchException(TranslatableComponent.of("worldedit.error.no-match", TextComponent.of(input)));
            case 1 -> (Mask)masks.get(0);
            default -> new MaskIntersection(masks);
        };
    }
}

