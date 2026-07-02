/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 */
package com.sk89q.worldedit.extension.factory.parser.mask;

import com.google.common.collect.ImmutableList;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.extension.input.ParserContext;
import com.sk89q.worldedit.function.mask.Mask;
import com.sk89q.worldedit.function.mask.RegionMask;
import com.sk89q.worldedit.internal.registry.SimpleInputParser;
import com.sk89q.worldedit.session.request.RequestSelection;
import java.util.List;

public class LazyRegionMaskParser
extends SimpleInputParser<Mask> {
    private final List<String> aliases = ImmutableList.of((Object)"#dregion", (Object)"#dselection", (Object)"#dsel");

    public LazyRegionMaskParser(WorldEdit worldEdit) {
        super(worldEdit);
    }

    @Override
    public List<String> getMatchedAliases() {
        return this.aliases;
    }

    @Override
    public Mask parseFromSimpleInput(String input, ParserContext context) {
        return new RegionMask(new RequestSelection());
    }
}

