/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DynamicOps
 *  javax.annotation.Nullable
 *  net.minecraft.commands.CommandSource
 *  net.minecraft.commands.CommandSourceStack
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.component.DataComponentPatch
 *  net.minecraft.core.registries.BuiltInRegistries
 *  net.minecraft.core.registries.Registries
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.NbtOps
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.util.StringRepresentable
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.BaseCommandBlock
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.level.biome.Biome
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.DirectionProperty
 *  net.minecraft.world.level.block.state.properties.EnumProperty
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.Vec3
 *  net.neoforged.neoforge.server.ServerLifecycleHooks
 */
package com.sk89q.worldedit.neoforge;

import com.google.common.base.Preconditions;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DynamicOps;
import com.sk89q.worldedit.blocks.BaseItemStack;
import com.sk89q.worldedit.extension.platform.Actor;
import com.sk89q.worldedit.internal.block.BlockStateIdAccess;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.math.Vector3;
import com.sk89q.worldedit.neoforge.NeoForgeBlockCommandSender;
import com.sk89q.worldedit.neoforge.NeoForgeCommandSender;
import com.sk89q.worldedit.neoforge.NeoForgePlayer;
import com.sk89q.worldedit.neoforge.NeoForgeWorld;
import com.sk89q.worldedit.neoforge.NeoForgeWorldEdit;
import com.sk89q.worldedit.neoforge.internal.NBTConverter;
import com.sk89q.worldedit.neoforge.internal.NeoForgeTransmogrifier;
import com.sk89q.worldedit.registry.state.Property;
import com.sk89q.worldedit.util.concurrency.LazyReference;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldedit.world.biome.BiomeType;
import com.sk89q.worldedit.world.biome.BiomeTypes;
import com.sk89q.worldedit.world.block.BlockType;
import com.sk89q.worldedit.world.block.BlockTypes;
import com.sk89q.worldedit.world.item.ItemType;
import com.sk89q.worldedit.world.item.ItemTypes;
import java.util.Comparator;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import javax.annotation.Nullable;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BaseCommandBlock;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.enginehub.linbus.tree.LinCompoundTag;

public final class NeoForgeAdapter {
    private static final Codec<DataComponentPatch> COMPONENTS_CODEC = DataComponentPatch.CODEC.optionalFieldOf("components", (Object)DataComponentPatch.EMPTY).codec();

    private NeoForgeAdapter() {
    }

    public static World adapt(ServerLevel world) {
        return new NeoForgeWorld(world);
    }

    public static ServerLevel adapt(World world) {
        Preconditions.checkNotNull((Object)world);
        if (world instanceof NeoForgeWorld) {
            return ((NeoForgeWorld)world).getWorld();
        }
        throw new UnsupportedOperationException("Cannot adapt from a " + String.valueOf(world.getClass()));
    }

    public static Biome adapt(BiomeType biomeType) {
        return (Biome)ServerLifecycleHooks.getCurrentServer().registryAccess().registryOrThrow(Registries.BIOME).getOptional(ResourceLocation.parse((String)biomeType.id())).orElseThrow(() -> new IllegalStateException("No biome for " + biomeType.id()));
    }

    public static BiomeType adapt(Biome biome) {
        ResourceLocation id = ServerLifecycleHooks.getCurrentServer().registryAccess().registryOrThrow(Registries.BIOME).getKey((Object)biome);
        Objects.requireNonNull(id, "biome is not registered");
        return BiomeTypes.get(id.toString());
    }

    public static Vector3 adapt(Vec3 vector) {
        return Vector3.at(vector.x, vector.y, vector.z);
    }

    public static BlockVector3 adapt(BlockPos pos) {
        return BlockVector3.at(pos.getX(), pos.getY(), pos.getZ());
    }

    public static Vec3 toVec3(BlockVector3 vector) {
        return new Vec3((double)vector.x(), (double)vector.y(), (double)vector.z());
    }

    public static Direction adapt(com.sk89q.worldedit.util.Direction face) {
        switch (face) {
            case NORTH: {
                return Direction.NORTH;
            }
            case SOUTH: {
                return Direction.SOUTH;
            }
            case WEST: {
                return Direction.WEST;
            }
            case EAST: {
                return Direction.EAST;
            }
            case DOWN: {
                return Direction.DOWN;
            }
        }
        return Direction.UP;
    }

    public static com.sk89q.worldedit.util.Direction adaptEnumFacing(@Nullable Direction face) {
        if (face == null) {
            return null;
        }
        switch (face) {
            case NORTH: {
                return com.sk89q.worldedit.util.Direction.NORTH;
            }
            case SOUTH: {
                return com.sk89q.worldedit.util.Direction.SOUTH;
            }
            case WEST: {
                return com.sk89q.worldedit.util.Direction.WEST;
            }
            case EAST: {
                return com.sk89q.worldedit.util.Direction.EAST;
            }
            case DOWN: {
                return com.sk89q.worldedit.util.Direction.DOWN;
            }
        }
        return com.sk89q.worldedit.util.Direction.UP;
    }

    public static BlockPos toBlockPos(BlockVector3 vector) {
        return new BlockPos(vector.x(), vector.y(), vector.z());
    }

    @Deprecated
    public static Property<?> adaptProperty(net.minecraft.world.level.block.state.properties.Property<?> property) {
        return NeoForgeTransmogrifier.transmogToWorldEditProperty(property);
    }

    @Deprecated
    public static Map<Property<?>, Object> adaptProperties(BlockType block, Map<net.minecraft.world.level.block.state.properties.Property<?>, Comparable<?>> mcProps) {
        TreeMap props = new TreeMap(Comparator.comparing(Property::getName));
        for (Map.Entry<net.minecraft.world.level.block.state.properties.Property<?>, Comparable<?>> prop : mcProps.entrySet()) {
            Object value = prop.getValue();
            if (prop.getKey() instanceof DirectionProperty) {
                value = NeoForgeAdapter.adaptEnumFacing((Direction)value);
            } else if (prop.getKey() instanceof EnumProperty) {
                value = ((StringRepresentable)value).getSerializedName();
            }
            props.put(block.getProperty(prop.getKey().getName()), value);
        }
        return props;
    }

    public static BlockState adapt(com.sk89q.worldedit.world.block.BlockState blockState) {
        int blockStateId = BlockStateIdAccess.getBlockStateId(blockState);
        if (!BlockStateIdAccess.isValidInternalId(blockStateId)) {
            return NeoForgeTransmogrifier.transmogToMinecraft(blockState);
        }
        return Block.stateById((int)blockStateId);
    }

    public static com.sk89q.worldedit.world.block.BlockState adapt(BlockState blockState) {
        int blockStateId = Block.getId((BlockState)blockState);
        com.sk89q.worldedit.world.block.BlockState worldEdit = BlockStateIdAccess.getBlockStateById(blockStateId);
        if (worldEdit == null) {
            return NeoForgeTransmogrifier.transmogToWorldEdit(blockState);
        }
        return worldEdit;
    }

    public static Block adapt(BlockType blockType) {
        return (Block)BuiltInRegistries.BLOCK.get(ResourceLocation.parse((String)blockType.id()));
    }

    public static BlockType adapt(Block block) {
        return BlockTypes.get(BuiltInRegistries.BLOCK.getKey((Object)block).toString());
    }

    public static Item adapt(ItemType itemType) {
        return (Item)BuiltInRegistries.ITEM.get(ResourceLocation.parse((String)itemType.id()));
    }

    public static ItemType adapt(Item item) {
        return ItemTypes.get(BuiltInRegistries.ITEM.getKey((Object)item).toString());
    }

    public static ItemStack adapt(BaseItemStack baseItemStack) {
        ItemStack itemStack = new ItemStack((ItemLike)NeoForgeAdapter.adapt(baseItemStack.getType()), baseItemStack.getAmount());
        LinCompoundTag nbt = baseItemStack.getNbt();
        if (nbt != null) {
            DataComponentPatch componentPatch = (DataComponentPatch)COMPONENTS_CODEC.parse((DynamicOps)ServerLifecycleHooks.getCurrentServer().registryAccess().createSerializationContext((DynamicOps)NbtOps.INSTANCE), (Object)NBTConverter.toNative(nbt)).getOrThrow();
            itemStack.applyComponents(componentPatch);
        }
        return itemStack;
    }

    public static BaseItemStack adapt(ItemStack itemStack) {
        CompoundTag tag = (CompoundTag)COMPONENTS_CODEC.encodeStart((DynamicOps)ServerLifecycleHooks.getCurrentServer().registryAccess().createSerializationContext((DynamicOps)NbtOps.INSTANCE), (Object)itemStack.getComponentsPatch()).getOrThrow();
        return new BaseItemStack(NeoForgeAdapter.adapt(itemStack.getItem()), LazyReference.from(() -> NBTConverter.fromNative(tag)), itemStack.getCount());
    }

    public static NeoForgePlayer adaptPlayer(ServerPlayer player) {
        Preconditions.checkNotNull((Object)player);
        return new NeoForgePlayer(player);
    }

    public static Actor adaptCommandSource(CommandSourceStack commandSourceStack) {
        CommandSource commandSource;
        Preconditions.checkNotNull((Object)commandSourceStack);
        if (commandSourceStack.isPlayer()) {
            return NeoForgeAdapter.adaptPlayer(commandSourceStack.getPlayer());
        }
        if (NeoForgeWorldEdit.inst.getConfig().commandBlockSupport && (commandSource = commandSourceStack.source) instanceof BaseCommandBlock) {
            BaseCommandBlock commandBlock = (BaseCommandBlock)commandSource;
            return new NeoForgeBlockCommandSender(commandBlock);
        }
        return new NeoForgeCommandSender(commandSourceStack);
    }
}

