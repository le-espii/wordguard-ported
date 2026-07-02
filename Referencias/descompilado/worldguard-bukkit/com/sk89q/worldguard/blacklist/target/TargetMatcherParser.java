/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sk89q.worldedit.world.block.BlockType
 *  com.sk89q.worldedit.world.block.BlockTypes
 *  com.sk89q.worldedit.world.item.ItemType
 *  com.sk89q.worldedit.world.item.ItemTypes
 */
package com.sk89q.worldguard.blacklist.target;

import com.sk89q.worldedit.world.block.BlockType;
import com.sk89q.worldedit.world.block.BlockTypes;
import com.sk89q.worldedit.world.item.ItemType;
import com.sk89q.worldedit.world.item.ItemTypes;
import com.sk89q.worldguard.blacklist.target.BlockMatcher;
import com.sk89q.worldguard.blacklist.target.ItemBlockMatcher;
import com.sk89q.worldguard.blacklist.target.ItemMatcher;
import com.sk89q.worldguard.blacklist.target.TargetMatcher;
import com.sk89q.worldguard.blacklist.target.TargetMatcherParseException;

public class TargetMatcherParser {
    public TargetMatcher fromInput(String input) throws TargetMatcherParseException {
        BlockType blockType = BlockTypes.get((String)(input = input.toLowerCase().trim()));
        if (blockType != null) {
            if (blockType.hasItemType()) {
                return new ItemBlockMatcher(blockType);
            }
            return new BlockMatcher(blockType);
        }
        ItemType itemType = ItemTypes.get((String)input);
        if (itemType == null) {
            throw new TargetMatcherParseException("Unknown block or item name: " + input);
        }
        return new ItemMatcher(itemType);
    }
}

