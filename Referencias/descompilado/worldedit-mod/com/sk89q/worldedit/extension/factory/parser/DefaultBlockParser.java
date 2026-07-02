/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 */
package com.sk89q.worldedit.extension.factory.parser;

import com.google.common.collect.ImmutableMap;
import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.NotABlockException;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.blocks.BaseItemStack;
import com.sk89q.worldedit.blocks.MobSpawnerBlock;
import com.sk89q.worldedit.blocks.SignBlock;
import com.sk89q.worldedit.blocks.SkullBlock;
import com.sk89q.worldedit.command.util.SuggestionHelper;
import com.sk89q.worldedit.entity.Player;
import com.sk89q.worldedit.extension.input.DisallowedUsageException;
import com.sk89q.worldedit.extension.input.InputParseException;
import com.sk89q.worldedit.extension.input.NoMatchException;
import com.sk89q.worldedit.extension.input.ParserContext;
import com.sk89q.worldedit.extension.platform.Actor;
import com.sk89q.worldedit.extension.platform.Capability;
import com.sk89q.worldedit.internal.registry.InputParser;
import com.sk89q.worldedit.internal.util.DeprecationUtil;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.registry.state.Property;
import com.sk89q.worldedit.util.HandSide;
import com.sk89q.worldedit.util.concurrency.LazyReference;
import com.sk89q.worldedit.util.formatting.text.TextComponent;
import com.sk89q.worldedit.util.formatting.text.TranslatableComponent;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldedit.world.block.BaseBlock;
import com.sk89q.worldedit.world.block.BlockState;
import com.sk89q.worldedit.world.block.BlockStateHolder;
import com.sk89q.worldedit.world.block.BlockType;
import com.sk89q.worldedit.world.block.BlockTypes;
import com.sk89q.worldedit.world.block.FuzzyBlockState;
import com.sk89q.worldedit.world.entity.EntityType;
import com.sk89q.worldedit.world.entity.EntityTypes;
import com.sk89q.worldedit.world.registry.LegacyMapper;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Stream;
import org.enginehub.linbus.format.snbt.LinStringIO;
import org.enginehub.linbus.stream.exception.NbtParseException;
import org.enginehub.linbus.tree.LinCompoundTag;
import org.enginehub.linbus.tree.LinTag;

public class DefaultBlockParser
extends InputParser<BaseBlock> {
    private static final String[] EMPTY_STRING_ARRAY = new String[0];

    public DefaultBlockParser(WorldEdit worldEdit) {
        super(worldEdit);
    }

    private static BaseBlock getBlockInHand(Actor actor, HandSide handSide) throws InputParseException {
        if (actor instanceof Player) {
            try {
                return ((Player)actor).getBlockInHand(handSide);
            }
            catch (NotABlockException e) {
                throw new InputParseException(e.getRichMessage());
            }
            catch (WorldEditException e) {
                throw new InputParseException(TranslatableComponent.of("worldedit.error.unknown", e.getRichMessage()), (Throwable)e);
            }
        }
        throw new InputParseException(TranslatableComponent.of("worldedit.error.parser.player-only", TextComponent.of(handSide == HandSide.MAIN_HAND ? "hand" : "offhand")));
    }

    @Override
    public BaseBlock parseFromInput(String input, ParserContext context) throws InputParseException {
        String originalInput = input;
        input = input.replace(";", "|");
        Exception suppressed = null;
        try {
            BaseBlock modified = this.parseLogic(input, context);
            if (modified != null) {
                return modified;
            }
        }
        catch (Exception e) {
            suppressed = e;
        }
        try {
            return this.parseLogic(originalInput, context);
        }
        catch (Exception e) {
            if (suppressed != null) {
                e.addSuppressed(suppressed);
            }
            throw e;
        }
    }

    private String woolMapper(String string) {
        return switch (string.toLowerCase(Locale.ROOT)) {
            case "white" -> BlockTypes.WHITE_WOOL.id();
            case "black" -> BlockTypes.BLACK_WOOL.id();
            case "blue" -> BlockTypes.BLUE_WOOL.id();
            case "brown" -> BlockTypes.BROWN_WOOL.id();
            case "cyan" -> BlockTypes.CYAN_WOOL.id();
            case "gray", "grey" -> BlockTypes.GRAY_WOOL.id();
            case "green" -> BlockTypes.GREEN_WOOL.id();
            case "light_blue", "lightblue" -> BlockTypes.LIGHT_BLUE_WOOL.id();
            case "light_gray", "light_grey", "lightgray", "lightgrey" -> BlockTypes.LIGHT_GRAY_WOOL.id();
            case "lime" -> BlockTypes.LIME_WOOL.id();
            case "magenta" -> BlockTypes.MAGENTA_WOOL.id();
            case "orange" -> BlockTypes.ORANGE_WOOL.id();
            case "pink" -> BlockTypes.PINK_WOOL.id();
            case "purple" -> BlockTypes.PURPLE_WOOL.id();
            case "yellow" -> BlockTypes.YELLOW_WOOL.id();
            case "red" -> BlockTypes.RED_WOOL.id();
            default -> string;
        };
    }

    private static Map<Property<?>, Object> parseProperties(BlockType type, String[] stateProperties, ParserContext context) throws InputParseException {
        HashMap blockStates = new HashMap();
        for (String parseableData : stateProperties) {
            try {
                Object value;
                String[] parts = parseableData.split("=");
                if (parts.length != 2) {
                    throw new InputParseException(TranslatableComponent.of("worldedit.error.parser.bad-state-format", TextComponent.of(parseableData)));
                }
                Property<?> propertyKey = type.getPropertyMap().get(parts[0]);
                if (propertyKey == null) {
                    if (context.getActor() != null) {
                        throw new NoMatchException(TranslatableComponent.of("worldedit.error.parser.unknown-property", TextComponent.of(parts[0]), TextComponent.of(type.id())));
                    }
                    WorldEdit.logger.debug("Unknown property " + parts[0] + " for block " + type.id());
                    return Map.of();
                }
                if (blockStates.containsKey(propertyKey)) {
                    throw new InputParseException(TranslatableComponent.of("worldedit.error.parser.duplicate-property", TextComponent.of(parts[0])));
                }
                try {
                    value = propertyKey.getValueFor(parts[1]);
                }
                catch (IllegalArgumentException e) {
                    throw new NoMatchException(TranslatableComponent.of("worldedit.error.parser.unknown-value", TextComponent.of(parts[1]), TextComponent.of(propertyKey.getName())));
                }
                blockStates.put(propertyKey, value);
            }
            catch (InputParseException e) {
                throw e;
            }
            catch (Exception e) {
                throw new InputParseException(TranslatableComponent.of("worldedit.error.parser.bad-state-format", TextComponent.of(parseableData)));
            }
        }
        return ImmutableMap.copyOf(blockStates);
    }

    @Override
    public Stream<String> getSuggestions(String input, ParserContext context) {
        String props;
        int idx = input.lastIndexOf(91);
        if (idx < 0) {
            return SuggestionHelper.getNamespacedRegistrySuggestions(BlockType.REGISTRY, input);
        }
        String blockType = input.substring(0, idx);
        BlockType type = BlockTypes.get(blockType.toLowerCase(Locale.ROOT));
        if (type == null) {
            String lowerBlockType;
            switch (lowerBlockType = blockType.toLowerCase(Locale.ROOT)) {
                case "hand": 
                case "offhand": {
                    Player player;
                    BaseItemStack itemInHand;
                    Actor actor = context.getActor();
                    if (!(actor instanceof Player) || !(itemInHand = (player = (Player)actor).getItemInHand(lowerBlockType.equals("hand") ? HandSide.MAIN_HAND : HandSide.OFF_HAND)).getType().hasBlockType()) break;
                    type = itemInHand.getType().getBlockType();
                    break;
                }
                case "pos1": {
                    World world = context.getWorld();
                    LocalSession session = context.getSession();
                    if (world == null || session == null) break;
                    try {
                        BlockVector3 primaryPosition = session.getRegionSelector(world).getPrimaryPosition();
                        type = world.getBlock(primaryPosition).getBlockType();
                    }
                    catch (IncompleteRegionException incompleteRegionException) {}
                    break;
                }
            }
            if (type == null) {
                return Stream.empty();
            }
        }
        if ((props = input.substring(idx + 1)).isEmpty()) {
            return type.getProperties().stream().map(p -> input + p.getName() + "=");
        }
        return SuggestionHelper.getBlockPropertySuggestions(blockType, type, props);
    }

    private BaseBlock parseLogic(String input, ParserContext context) throws InputParseException {
        Actor actor;
        BlockType blockType = null;
        HashMap blockStates = new HashMap();
        String[] blockAndExtraData = input.trim().split("\\|");
        if (blockAndExtraData.length == 0) {
            throw new NoMatchException(TranslatableComponent.of("worldedit.error.unknown-block", TextComponent.of(input)));
        }
        if (context.isTryingLegacy()) {
            blockAndExtraData[0] = this.woolMapper(blockAndExtraData[0]);
        }
        BlockStateHolder<BlockState> state = null;
        LinCompoundTag blockNbtData = null;
        if (context.isTryingLegacy()) {
            try {
                String[] split = blockAndExtraData[0].split(":", 2);
                if (split.length == 0) {
                    throw new InputParseException(TranslatableComponent.of("worldedit.error.parser.invalid-colon"));
                }
                state = split.length == 1 ? LegacyMapper.getInstance().getBlockFromLegacy(Integer.parseInt(split[0])) : LegacyMapper.getInstance().getBlockFromLegacy(Integer.parseInt(split[0]), Integer.parseInt(split[1]));
                if (state != null) {
                    blockType = state.getBlockType();
                }
            }
            catch (NumberFormatException split) {
                // empty catch block
            }
        }
        if (state == null) {
            int stateStart = blockAndExtraData[0].indexOf(91);
            int nbtStart = blockAndExtraData[0].indexOf(123);
            int typeEnd = stateStart == -1 ? nbtStart : (nbtStart == -1 ? stateStart : Math.min(nbtStart, stateStart));
            String typeString = typeEnd == -1 ? blockAndExtraData[0] : blockAndExtraData[0].substring(0, typeEnd);
            String stateString = null;
            if (stateStart != -1 && (nbtStart == -1 || stateStart < nbtStart)) {
                if (stateStart + 1 >= blockAndExtraData[0].length()) {
                    throw new InputParseException(TranslatableComponent.of("worldedit.error.parser.hanging-lbracket", TextComponent.of(stateStart)));
                }
                int stateEnd = blockAndExtraData[0].indexOf(93);
                if (stateEnd < 0) {
                    throw new InputParseException(TranslatableComponent.of("worldedit.error.parser.missing-rbracket"));
                }
                stateString = blockAndExtraData[0].substring(stateStart + 1, stateEnd);
            }
            String nbtString = null;
            if (nbtStart != -1) {
                if (nbtStart + 1 >= blockAndExtraData[0].length()) {
                    throw new InputParseException(TranslatableComponent.of("worldedit.error.parser.hanging-lbrace", TextComponent.of(nbtStart)));
                }
                int nbtEnd = blockAndExtraData[0].lastIndexOf(125);
                if (nbtEnd < 0) {
                    throw new InputParseException(TranslatableComponent.of("worldedit.error.parser.missing-rbrace"));
                }
                nbtString = blockAndExtraData[0].substring(nbtStart, nbtEnd + 1);
            }
            if (typeString.isEmpty()) {
                throw new InputParseException(TranslatableComponent.of("worldedit.error.parser.bad-state-format", TextComponent.of(blockAndExtraData[0])));
            }
            String[] stateProperties = EMPTY_STRING_ARRAY;
            if (stateString != null) {
                stateProperties = stateString.split(",");
            }
            if ("hand".equalsIgnoreCase(typeString)) {
                blockInHand = DefaultBlockParser.getBlockInHand(context.requireActor(), HandSide.MAIN_HAND);
                if (blockInHand.getClass() != BaseBlock.class) {
                    return blockInHand;
                }
                blockType = blockInHand.getBlockType();
                blockStates.putAll(blockInHand.getStates());
                blockNbtData = blockInHand.getNbt();
            } else if ("offhand".equalsIgnoreCase(typeString)) {
                blockInHand = DefaultBlockParser.getBlockInHand(context.requireActor(), HandSide.OFF_HAND);
                if (blockInHand.getClass() != BaseBlock.class) {
                    return blockInHand;
                }
                blockType = blockInHand.getBlockType();
                blockStates.putAll(blockInHand.getStates());
                blockNbtData = blockInHand.getNbt();
            } else if ("pos1".equalsIgnoreCase(typeString)) {
                BlockVector3 blockVector3;
                World world = context.requireWorld();
                try {
                    blockVector3 = context.requireSession().getRegionSelector(world).getPrimaryPosition();
                }
                catch (IncompleteRegionException incompleteRegionException) {
                    throw new InputParseException(TranslatableComponent.of("worldedit.error.incomplete-region"));
                }
                BaseBlock baseBlock = world.getFullBlock(blockVector3);
                blockType = baseBlock.getBlockType();
                blockStates.putAll(baseBlock.getStates());
                blockNbtData = baseBlock.getNbt();
            } else {
                blockType = BlockTypes.get(typeString.toLowerCase(Locale.ROOT));
            }
            if (blockType == null) {
                throw new NoMatchException(TranslatableComponent.of("worldedit.error.unknown-block", TextComponent.of(input)));
            }
            blockStates.putAll(DefaultBlockParser.parseProperties(blockType, stateProperties, context));
            if (context.isPreferringWildcard()) {
                FuzzyBlockState.Builder fuzzyBuilder = FuzzyBlockState.builder();
                fuzzyBuilder.type(blockType);
                for (Map.Entry entry : blockStates.entrySet()) {
                    Property objProp = (Property)entry.getKey();
                    fuzzyBuilder.withProperty(objProp, entry.getValue());
                }
                state = fuzzyBuilder.build();
            } else {
                state = blockType.getDefaultState();
                for (Map.Entry entry : blockStates.entrySet()) {
                    Property property = (Property)entry.getKey();
                    state = state.with(property, entry.getValue());
                }
            }
            if (nbtString != null) {
                LinCompoundTag otherTag;
                try {
                    otherTag = LinStringIO.readFromStringUsing(nbtString, LinCompoundTag::readFrom);
                }
                catch (NbtParseException nbtParseException) {
                    throw new NoMatchException(TranslatableComponent.of("worldedit.error.parser.invalid-nbt", TextComponent.of(input), TextComponent.of(nbtParseException.getMessage())));
                }
                blockNbtData = blockNbtData == null ? otherTag : blockNbtData.toBuilder().putAll((Map<String, ? extends LinTag<?>>)otherTag.value()).build();
            }
        }
        if (blockType == null) {
            throw new NoMatchException(TranslatableComponent.of("worldedit.error.unknown-block", TextComponent.of(input)));
        }
        if (context.isRestricted() && (actor = context.requireActor()) != null && !actor.hasPermission("worldedit.anyblock") && this.worldEdit.getConfiguration().disallowedBlocks.contains(blockType.id())) {
            throw new DisallowedUsageException(TranslatableComponent.of("worldedit.error.disallowed-block", TextComponent.of(input)));
        }
        BaseBlock baseBlock = state.toBaseBlock(blockNbtData == null ? null : LazyReference.computed(blockNbtData));
        if (!context.isTryingLegacy()) {
            return baseBlock;
        }
        if (DeprecationUtil.isSign(blockType) && blockAndExtraData.length > 1) {
            String[] text = new String[]{blockAndExtraData[1], blockAndExtraData.length > 2 ? blockAndExtraData[2] : "", blockAndExtraData.length > 3 ? blockAndExtraData[3] : "", blockAndExtraData.length > 4 ? blockAndExtraData[4] : ""};
            SignBlock signBlock = new SignBlock((BlockState)state, text);
            return signBlock;
        }
        if (blockType == BlockTypes.SPAWNER && (blockAndExtraData.length > 1 || blockNbtData != null)) {
            String mobName;
            if (blockAndExtraData.length > 1) {
                mobName = blockAndExtraData[1];
                EntityType ent = EntityTypes.get(mobName.toLowerCase(Locale.ROOT));
                if (ent == null) {
                    throw new NoMatchException(TranslatableComponent.of("worldedit.error.unknown-entity", TextComponent.of(mobName)));
                }
                mobName = ent.id();
                if (!this.worldEdit.getPlatformManager().queryCapability(Capability.USER_COMMANDS).isValidMobType(mobName)) {
                    throw new NoMatchException(TranslatableComponent.of("worldedit.error.unknown-mob", TextComponent.of(mobName)));
                }
            } else {
                mobName = EntityTypes.PIG.id();
            }
            MobSpawnerBlock mobSpawnerBlock = new MobSpawnerBlock((BlockState)state, mobName);
            return mobSpawnerBlock;
        }
        if (!(blockType != BlockTypes.PLAYER_HEAD && blockType != BlockTypes.PLAYER_WALL_HEAD || blockAndExtraData.length <= 1 && blockNbtData == null)) {
            if (blockAndExtraData.length == 1) {
                SkullBlock skullBlock = new SkullBlock((BlockState)state);
                return skullBlock;
            }
            String type = blockAndExtraData[1];
            SkullBlock skullBlock = new SkullBlock((BlockState)state, type.replace(" ", "_"));
            return skullBlock;
        }
        return baseBlock;
    }
}

