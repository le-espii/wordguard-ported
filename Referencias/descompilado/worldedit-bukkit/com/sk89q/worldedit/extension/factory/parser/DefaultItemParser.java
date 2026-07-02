/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.extension.factory.parser;

import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.blocks.BaseItem;
import com.sk89q.worldedit.blocks.BaseItemStack;
import com.sk89q.worldedit.command.util.SuggestionHelper;
import com.sk89q.worldedit.entity.Player;
import com.sk89q.worldedit.extension.input.InputParseException;
import com.sk89q.worldedit.extension.input.NoMatchException;
import com.sk89q.worldedit.extension.input.ParserContext;
import com.sk89q.worldedit.extension.platform.Actor;
import com.sk89q.worldedit.internal.registry.InputParser;
import com.sk89q.worldedit.util.HandSide;
import com.sk89q.worldedit.util.concurrency.LazyReference;
import com.sk89q.worldedit.util.formatting.text.TextComponent;
import com.sk89q.worldedit.util.formatting.text.TranslatableComponent;
import com.sk89q.worldedit.world.item.ItemType;
import com.sk89q.worldedit.world.item.ItemTypes;
import com.sk89q.worldedit.world.registry.LegacyMapper;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Stream;
import org.enginehub.linbus.format.snbt.LinStringIO;
import org.enginehub.linbus.stream.exception.NbtParseException;
import org.enginehub.linbus.tree.LinCompoundTag;
import org.enginehub.linbus.tree.LinTag;

public class DefaultItemParser
extends InputParser<BaseItem> {
    public DefaultItemParser(WorldEdit worldEdit) {
        super(worldEdit);
    }

    @Override
    public Stream<String> getSuggestions(String input, ParserContext context) {
        return SuggestionHelper.getNamespacedRegistrySuggestions(ItemType.REGISTRY, input);
    }

    @Override
    public BaseItem parseFromInput(String input, ParserContext context) throws InputParseException {
        ItemType itemType;
        LinCompoundTag itemNbtData = null;
        BaseItem item = null;
        if (context.isTryingLegacy()) {
            try {
                String[] split = input.split(":");
                if (split.length == 0) {
                    throw new InputParseException(TranslatableComponent.of("worldedit.error.parser.invalid-colon"));
                }
                itemType = split.length == 1 ? LegacyMapper.getInstance().getItemFromLegacy(Integer.parseInt(split[0])) : LegacyMapper.getInstance().getItemFromLegacy(Integer.parseInt(split[0]), Integer.parseInt(split[1]));
                if (itemType != null) {
                    item = new BaseItem(itemType);
                }
            }
            catch (NumberFormatException split) {
                // empty catch block
            }
        }
        if (item == null) {
            String typeString;
            String nbtString = null;
            int nbtStart = input.indexOf(123);
            if (nbtStart == -1) {
                typeString = input;
            } else {
                typeString = input.substring(0, nbtStart);
                if (nbtStart + 1 >= input.length()) {
                    throw new InputParseException(TranslatableComponent.of("worldedit.error.parser.hanging-lbrace", TextComponent.of(nbtStart)));
                }
                int stateEnd = input.lastIndexOf(125);
                if (stateEnd < 0) {
                    throw new InputParseException(TranslatableComponent.of("worldedit.error.parser.missing-rbrace"));
                }
                nbtString = input.substring(nbtStart);
            }
            if ("hand".equalsIgnoreCase(typeString)) {
                BaseItemStack heldItem = this.getItemInHand(context.requireActor(), HandSide.MAIN_HAND);
                itemType = heldItem.getType();
                itemNbtData = heldItem.getNbt();
            } else if ("offhand".equalsIgnoreCase(typeString)) {
                BaseItemStack heldItem = this.getItemInHand(context.requireActor(), HandSide.OFF_HAND);
                itemType = heldItem.getType();
                itemNbtData = heldItem.getNbt();
            } else {
                itemType = ItemTypes.get(typeString.toLowerCase(Locale.ROOT));
            }
            if (itemType == null) {
                throw new NoMatchException(TranslatableComponent.of("worldedit.error.unknown-item", TextComponent.of(input)));
            }
            if (nbtString != null) {
                LinCompoundTag otherTag;
                try {
                    otherTag = LinStringIO.readFromStringUsing(nbtString, LinCompoundTag::readFrom);
                }
                catch (NbtParseException e) {
                    throw new NoMatchException(TranslatableComponent.of("worldedit.error.invalid-nbt", TextComponent.of(input), TextComponent.of(e.getMessage())));
                }
                itemNbtData = itemNbtData == null ? otherTag : itemNbtData.toBuilder().putAll((Map<String, ? extends LinTag<?>>)otherTag.value()).build();
            }
            item = new BaseItem(itemType, itemNbtData == null ? null : LazyReference.computed(itemNbtData));
        }
        return item;
    }

    private BaseItemStack getItemInHand(Actor actor, HandSide handSide) throws InputParseException {
        if (actor instanceof Player) {
            return ((Player)actor).getItemInHand(handSide);
        }
        throw new InputParseException(TranslatableComponent.of("worldedit.error.parser.player-only", TextComponent.of(handSide == HandSide.MAIN_HAND ? "hand" : "offhand")));
    }
}

