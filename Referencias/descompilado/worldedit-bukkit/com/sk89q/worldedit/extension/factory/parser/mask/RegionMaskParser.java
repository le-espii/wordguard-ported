/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 */
package com.sk89q.worldedit.extension.factory.parser.mask;

import com.google.common.collect.ImmutableList;
import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.extension.input.InputParseException;
import com.sk89q.worldedit.extension.input.ParserContext;
import com.sk89q.worldedit.function.mask.Mask;
import com.sk89q.worldedit.function.mask.RegionMask;
import com.sk89q.worldedit.internal.registry.SimpleInputParser;
import com.sk89q.worldedit.util.formatting.text.TranslatableComponent;
import java.util.List;

public class RegionMaskParser
extends SimpleInputParser<Mask> {
    private final List<String> aliases = ImmutableList.of((Object)"#region", (Object)"#selection", (Object)"#sel");

    public RegionMaskParser(WorldEdit worldEdit) {
        super(worldEdit);
    }

    @Override
    public List<String> getMatchedAliases() {
        return this.aliases;
    }

    @Override
    public Mask parseFromSimpleInput(String input, ParserContext context) throws InputParseException {
        try {
            return new RegionMask(context.requireSession().getSelection(context.requireWorld()).clone());
        }
        catch (IncompleteRegionException e) {
            throw new InputParseException(TranslatableComponent.of("worldedit.error.incomplete-region"));
        }
    }
}

