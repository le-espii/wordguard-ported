/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.extension.factory;

import com.sk89q.util.StringUtil;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.extension.factory.parser.DefaultBlockParser;
import com.sk89q.worldedit.extension.input.InputParseException;
import com.sk89q.worldedit.extension.input.ParserContext;
import com.sk89q.worldedit.internal.registry.AbstractFactory;
import com.sk89q.worldedit.world.block.BaseBlock;
import java.util.HashSet;
import java.util.Set;

public class BlockFactory
extends AbstractFactory<BaseBlock> {
    public BlockFactory(WorldEdit worldEdit) {
        super(worldEdit, new DefaultBlockParser(worldEdit));
    }

    public Set<BaseBlock> parseFromListInput(String input, ParserContext context) throws InputParseException {
        HashSet<BaseBlock> blocks = new HashSet<BaseBlock>();
        String[] splits = input.split(",");
        for (String token : StringUtil.parseListInQuotes(splits, ',', new char[]{'[', '{'}, new char[]{']', '}'}, true)) {
            blocks.add((BaseBlock)this.parseFromInput(token, context));
        }
        return blocks;
    }
}

