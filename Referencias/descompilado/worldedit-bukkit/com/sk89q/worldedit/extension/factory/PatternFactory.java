/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.extension.factory;

import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.extension.factory.parser.pattern.BlockCategoryPatternParser;
import com.sk89q.worldedit.extension.factory.parser.pattern.ClipboardPatternParser;
import com.sk89q.worldedit.extension.factory.parser.pattern.RandomPatternParser;
import com.sk89q.worldedit.extension.factory.parser.pattern.RandomStatePatternParser;
import com.sk89q.worldedit.extension.factory.parser.pattern.SingleBlockPatternParser;
import com.sk89q.worldedit.extension.factory.parser.pattern.TypeOrStateApplyingPatternParser;
import com.sk89q.worldedit.function.pattern.Pattern;
import com.sk89q.worldedit.internal.registry.AbstractFactory;

public final class PatternFactory
extends AbstractFactory<Pattern> {
    public PatternFactory(WorldEdit worldEdit) {
        super(worldEdit, new SingleBlockPatternParser(worldEdit));
        this.register(new RandomPatternParser(worldEdit));
        this.register(new ClipboardPatternParser(worldEdit));
        this.register(new TypeOrStateApplyingPatternParser(worldEdit));
        this.register(new RandomStatePatternParser(worldEdit));
        this.register(new BlockCategoryPatternParser(worldEdit));
    }
}

