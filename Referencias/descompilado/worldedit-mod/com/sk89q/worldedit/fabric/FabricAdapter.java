/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DynamicOps
 *  javax.annotation.Nullable
 *  net.minecraft.class_1792
 *  net.minecraft.class_1799
 *  net.minecraft.class_1918
 *  net.minecraft.class_1935
 *  net.minecraft.class_1937
 *  net.minecraft.class_1959
 *  net.minecraft.class_2165
 *  net.minecraft.class_2168
 *  net.minecraft.class_2248
 *  net.minecraft.class_2338
 *  net.minecraft.class_2350
 *  net.minecraft.class_243
 *  net.minecraft.class_2487
 *  net.minecraft.class_2509
 *  net.minecraft.class_2586
 *  net.minecraft.class_2680
 *  net.minecraft.class_2753
 *  net.minecraft.class_2754
 *  net.minecraft.class_2769
 *  net.minecraft.class_2960
 *  net.minecraft.class_3222
 *  net.minecraft.class_3542
 *  net.minecraft.class_7225$class_7874
 *  net.minecraft.class_7924
 *  net.minecraft.class_9326
 */
package com.sk89q.worldedit.fabric;

import com.google.common.base.Preconditions;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DynamicOps;
import com.sk89q.worldedit.blocks.BaseItemStack;
import com.sk89q.worldedit.extension.platform.Actor;
import com.sk89q.worldedit.fabric.FabricBlockCommandSender;
import com.sk89q.worldedit.fabric.FabricCommandSender;
import com.sk89q.worldedit.fabric.FabricPlayer;
import com.sk89q.worldedit.fabric.FabricWorld;
import com.sk89q.worldedit.fabric.FabricWorldEdit;
import com.sk89q.worldedit.fabric.internal.FabricTransmogrifier;
import com.sk89q.worldedit.fabric.internal.NBTConverter;
import com.sk89q.worldedit.internal.block.BlockStateIdAccess;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.math.Vector3;
import com.sk89q.worldedit.registry.state.Property;
import com.sk89q.worldedit.util.Direction;
import com.sk89q.worldedit.util.concurrency.LazyReference;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldedit.world.biome.BiomeType;
import com.sk89q.worldedit.world.biome.BiomeTypes;
import com.sk89q.worldedit.world.block.BaseBlock;
import com.sk89q.worldedit.world.block.BlockState;
import com.sk89q.worldedit.world.block.BlockType;
import com.sk89q.worldedit.world.block.BlockTypes;
import com.sk89q.worldedit.world.item.ItemType;
import com.sk89q.worldedit.world.item.ItemTypes;
import java.util.Comparator;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import javax.annotation.Nullable;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_1918;
import net.minecraft.class_1935;
import net.minecraft.class_1937;
import net.minecraft.class_1959;
import net.minecraft.class_2165;
import net.minecraft.class_2168;
import net.minecraft.class_2248;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_243;
import net.minecraft.class_2487;
import net.minecraft.class_2509;
import net.minecraft.class_2586;
import net.minecraft.class_2680;
import net.minecraft.class_2753;
import net.minecraft.class_2754;
import net.minecraft.class_2769;
import net.minecraft.class_2960;
import net.minecraft.class_3222;
import net.minecraft.class_3542;
import net.minecraft.class_7225;
import net.minecraft.class_7924;
import net.minecraft.class_9326;
import org.enginehub.linbus.tree.LinCompoundTag;

public final class FabricAdapter {
    private static final Codec<class_9326> COMPONENTS_CODEC = class_9326.field_49589.optionalFieldOf("components", (Object)class_9326.field_49588).codec();

    private FabricAdapter() {
    }

    public static World adapt(class_1937 world) {
        return new FabricWorld(world);
    }

    public static class_1937 adapt(World world) {
        Preconditions.checkNotNull((Object)world);
        if (world instanceof FabricWorld) {
            return ((FabricWorld)world).getWorld();
        }
        throw new UnsupportedOperationException("Cannot adapt from a " + String.valueOf(world.getClass()));
    }

    public static class_1959 adapt(BiomeType biomeType) {
        return (class_1959)FabricWorldEdit.getRegistry(class_7924.field_41236).method_10223(class_2960.method_60654((String)biomeType.id()));
    }

    public static BiomeType adapt(class_1959 biome) {
        class_2960 id = FabricWorldEdit.getRegistry(class_7924.field_41236).method_10221((Object)biome);
        Objects.requireNonNull(id, "biome is not registered");
        return BiomeTypes.get(id.toString());
    }

    public static Vector3 adapt(class_243 vector) {
        return Vector3.at(vector.field_1352, vector.field_1351, vector.field_1350);
    }

    public static BlockVector3 adapt(class_2338 pos) {
        return BlockVector3.at(pos.method_10263(), pos.method_10264(), pos.method_10260());
    }

    public static class_243 toVec3(BlockVector3 vector) {
        return new class_243((double)vector.x(), (double)vector.y(), (double)vector.z());
    }

    public static class_2350 adapt(Direction face) {
        switch (face) {
            case NORTH: {
                return class_2350.field_11043;
            }
            case SOUTH: {
                return class_2350.field_11035;
            }
            case WEST: {
                return class_2350.field_11039;
            }
            case EAST: {
                return class_2350.field_11034;
            }
            case DOWN: {
                return class_2350.field_11033;
            }
        }
        return class_2350.field_11036;
    }

    public static Direction adaptEnumFacing(@Nullable class_2350 face) {
        if (face == null) {
            return null;
        }
        switch (face) {
            case field_11043: {
                return Direction.NORTH;
            }
            case field_11035: {
                return Direction.SOUTH;
            }
            case field_11039: {
                return Direction.WEST;
            }
            case field_11034: {
                return Direction.EAST;
            }
            case field_11033: {
                return Direction.DOWN;
            }
        }
        return Direction.UP;
    }

    public static class_2338 toBlockPos(BlockVector3 vector) {
        return new class_2338(vector.x(), vector.y(), vector.z());
    }

    @Deprecated
    public static Property<?> adaptProperty(class_2769<?> property) {
        return FabricTransmogrifier.transmogToWorldEditProperty(property);
    }

    @Deprecated
    public static Map<Property<?>, Object> adaptProperties(BlockType block, Map<class_2769<?>, Comparable<?>> mcProps) {
        TreeMap props = new TreeMap(Comparator.comparing(Property::getName));
        for (Map.Entry<class_2769<?>, Comparable<?>> prop : mcProps.entrySet()) {
            Object value = prop.getValue();
            if (prop.getKey() instanceof class_2753) {
                value = FabricAdapter.adaptEnumFacing((class_2350)value);
            } else if (prop.getKey() instanceof class_2754) {
                value = ((class_3542)value).method_15434();
            }
            props.put(block.getProperty(prop.getKey().method_11899()), value);
        }
        return props;
    }

    public static class_2680 adapt(BlockState blockState) {
        int blockStateId = BlockStateIdAccess.getBlockStateId(blockState);
        if (!BlockStateIdAccess.isValidInternalId(blockStateId)) {
            return FabricTransmogrifier.transmogToMinecraft(blockState);
        }
        return class_2248.method_9531((int)blockStateId);
    }

    public static BlockState adapt(class_2680 blockState) {
        int blockStateId = class_2248.method_9507((class_2680)blockState);
        BlockState worldEdit = BlockStateIdAccess.getBlockStateById(blockStateId);
        if (worldEdit == null) {
            return FabricTransmogrifier.transmogToWorldEdit(blockState);
        }
        return worldEdit;
    }

    public static BaseBlock adapt(class_2586 blockEntity) {
        if (!blockEntity.method_11002()) {
            throw new IllegalArgumentException("BlockEntity must have a level");
        }
        int blockStateId = class_2248.method_9507((class_2680)blockEntity.method_11010());
        BlockState worldEdit = BlockStateIdAccess.getBlockStateById(blockStateId);
        if (worldEdit == null) {
            worldEdit = FabricTransmogrifier.transmogToWorldEdit(blockEntity.method_11010());
        }
        class_2487 savedNative = blockEntity.method_38243((class_7225.class_7874)blockEntity.method_10997().method_30349());
        return worldEdit.toBaseBlock(LazyReference.from(() -> NBTConverter.fromNative(savedNative)));
    }

    public static class_2248 adapt(BlockType blockType) {
        return (class_2248)FabricWorldEdit.getRegistry(class_7924.field_41254).method_10223(class_2960.method_60654((String)blockType.id()));
    }

    public static BlockType adapt(class_2248 block) {
        return BlockTypes.get(FabricWorldEdit.getRegistry(class_7924.field_41254).method_10221((Object)block).toString());
    }

    public static class_1792 adapt(ItemType itemType) {
        return (class_1792)FabricWorldEdit.getRegistry(class_7924.field_41197).method_10223(class_2960.method_60654((String)itemType.id()));
    }

    public static ItemType adapt(class_1792 item) {
        return ItemTypes.get(FabricWorldEdit.getRegistry(class_7924.field_41197).method_10221((Object)item).toString());
    }

    public static class_1799 adapt(BaseItemStack baseItemStack) {
        class_1799 itemStack = new class_1799((class_1935)FabricAdapter.adapt(baseItemStack.getType()), baseItemStack.getAmount());
        LinCompoundTag nbt = baseItemStack.getNbt();
        if (nbt != null) {
            class_9326 componentPatch = (class_9326)COMPONENTS_CODEC.parse((DynamicOps)FabricWorldEdit.registryAccess().method_57093((DynamicOps)class_2509.field_11560), (Object)NBTConverter.toNative(nbt)).getOrThrow();
            itemStack.method_57366(componentPatch);
        }
        return itemStack;
    }

    public static BaseItemStack adapt(class_1799 itemStack) {
        class_2487 tag = (class_2487)COMPONENTS_CODEC.encodeStart((DynamicOps)FabricWorldEdit.registryAccess().method_57093((DynamicOps)class_2509.field_11560), (Object)itemStack.method_57380()).getOrThrow();
        return new BaseItemStack(FabricAdapter.adapt(itemStack.method_7909()), LazyReference.from(() -> NBTConverter.fromNative(tag)), itemStack.method_7947());
    }

    public static FabricPlayer adaptPlayer(class_3222 player) {
        Preconditions.checkNotNull((Object)player);
        return new FabricPlayer(player);
    }

    public static Actor adaptCommandSource(class_2168 commandSourceStack) {
        class_2165 class_21652;
        Preconditions.checkNotNull((Object)commandSourceStack);
        if (commandSourceStack.method_43737()) {
            return FabricAdapter.adaptPlayer(commandSourceStack.method_44023());
        }
        if (FabricWorldEdit.inst.getConfig().commandBlockSupport && (class_21652 = commandSourceStack.field_9819) instanceof class_1918) {
            class_1918 commandBlock = (class_1918)class_21652;
            return new FabricBlockCommandSender(commandBlock);
        }
        return new FabricCommandSender(commandSourceStack);
    }
}

