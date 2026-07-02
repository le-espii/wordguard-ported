/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  javax.annotation.Nullable
 *  org.bukkit.Bukkit
 *  org.bukkit.GameMode
 *  org.bukkit.Location
 *  org.bukkit.Material
 *  org.bukkit.NamespacedKey
 *  org.bukkit.Registry
 *  org.bukkit.World
 *  org.bukkit.block.Biome
 *  org.bukkit.block.BlockFace
 *  org.bukkit.block.data.BlockData
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.EntityType
 *  org.bukkit.entity.Player
 *  org.bukkit.inventory.ItemStack
 */
package com.sk89q.worldedit.bukkit;

import com.google.common.base.Preconditions;
import com.sk89q.worldedit.NotABlockException;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.blocks.BaseItemStack;
import com.sk89q.worldedit.bukkit.BukkitBlockCommandSender;
import com.sk89q.worldedit.bukkit.BukkitCommandSender;
import com.sk89q.worldedit.bukkit.BukkitEntity;
import com.sk89q.worldedit.bukkit.BukkitPlayer;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.bukkit.adapter.BukkitImplAdapter;
import com.sk89q.worldedit.bukkit.fastutil.ints.Int2ObjectMap;
import com.sk89q.worldedit.bukkit.fastutil.ints.Int2ObjectMaps;
import com.sk89q.worldedit.bukkit.fastutil.ints.Int2ObjectOpenHashMap;
import com.sk89q.worldedit.entity.Entity;
import com.sk89q.worldedit.entity.Player;
import com.sk89q.worldedit.extension.input.InputParseException;
import com.sk89q.worldedit.extension.input.ParserContext;
import com.sk89q.worldedit.extension.platform.Actor;
import com.sk89q.worldedit.extent.Extent;
import com.sk89q.worldedit.internal.block.BlockStateIdAccess;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.math.Vector3;
import com.sk89q.worldedit.util.Direction;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldedit.world.biome.BiomeType;
import com.sk89q.worldedit.world.biome.BiomeTypes;
import com.sk89q.worldedit.world.block.BaseBlock;
import com.sk89q.worldedit.world.block.BlockState;
import com.sk89q.worldedit.world.block.BlockStateHolder;
import com.sk89q.worldedit.world.block.BlockType;
import com.sk89q.worldedit.world.block.BlockTypes;
import com.sk89q.worldedit.world.entity.EntityTypes;
import com.sk89q.worldedit.world.gamemode.GameMode;
import com.sk89q.worldedit.world.gamemode.GameModes;
import com.sk89q.worldedit.world.item.ItemType;
import com.sk89q.worldedit.world.item.ItemTypes;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.Nullable;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.block.Biome;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

public class BukkitAdapter {
    private static final ParserContext TO_BLOCK_CONTEXT = new ParserContext();
    private static final Map<Biome, BiomeType> biomeBiomeTypeCache;
    private static final Map<BiomeType, Biome> biomeTypeBiomeCache;
    private static final Map<Material, BlockType> materialBlockTypeCache;
    private static final Map<Material, ItemType> materialItemTypeCache;
    private static final Int2ObjectMap<BlockState> blockStateCache;
    private static final Map<String, BlockState> blockStateStringCache;
    private static final Int2ObjectMap<BlockData> blockDataCache;

    private BukkitAdapter() {
    }

    public static boolean equals(BlockType blockType, Material type) {
        return Objects.equals(blockType.id(), type.getKey().toString());
    }

    public static BukkitWorld asBukkitWorld(World world) {
        if (world instanceof BukkitWorld) {
            return (BukkitWorld)world;
        }
        BukkitWorld bukkitWorld = WorldEditPlugin.getInstance().getInternalPlatform().matchWorld(world);
        if (bukkitWorld == null) {
            throw new RuntimeException("World '" + world.getName() + "' has no matching version in Bukkit");
        }
        return bukkitWorld;
    }

    public static World adapt(org.bukkit.World world) {
        Preconditions.checkNotNull((Object)world);
        return new BukkitWorld(world);
    }

    public static Actor adapt(CommandSender sender) {
        return WorldEditPlugin.getInstance().wrapCommandSender(sender);
    }

    public static BukkitPlayer adapt(org.bukkit.entity.Player player) {
        return WorldEditPlugin.getInstance().wrapPlayer(player);
    }

    public static CommandSender adapt(Actor actor) {
        if (actor instanceof Player) {
            return BukkitAdapter.adapt((Player)actor);
        }
        if (actor instanceof BukkitBlockCommandSender) {
            return ((BukkitBlockCommandSender)actor).getSender();
        }
        return ((BukkitCommandSender)actor).getSender();
    }

    public static org.bukkit.entity.Player adapt(Player player) {
        return ((BukkitPlayer)player).getPlayer();
    }

    public static Direction adapt(@Nullable BlockFace face) {
        if (face == null) {
            return null;
        }
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

    public static org.bukkit.World adapt(World world) {
        Preconditions.checkNotNull((Object)world);
        if (world instanceof BukkitWorld) {
            return ((BukkitWorld)world).getWorld();
        }
        org.bukkit.World match = Bukkit.getServer().getWorld(world.getName());
        if (match != null) {
            return match;
        }
        throw new IllegalArgumentException("Can't find a Bukkit world for " + world.getName());
    }

    public static com.sk89q.worldedit.util.Location adapt(Location location) {
        Preconditions.checkNotNull((Object)location);
        Vector3 position = BukkitAdapter.asVector(location);
        return new com.sk89q.worldedit.util.Location((Extent)BukkitAdapter.adapt(location.getWorld()), position, location.getYaw(), location.getPitch());
    }

    public static Location adapt(com.sk89q.worldedit.util.Location location) {
        Preconditions.checkNotNull((Object)location);
        Vector3 position = location.toVector();
        return new Location(BukkitAdapter.adapt((World)location.getExtent()), position.x(), position.y(), position.z(), location.getYaw(), location.getPitch());
    }

    public static Location adapt(org.bukkit.World world, Vector3 position) {
        Preconditions.checkNotNull((Object)world);
        Preconditions.checkNotNull((Object)((Object)position));
        return new Location(world, position.x(), position.y(), position.z());
    }

    public static Location adapt(org.bukkit.World world, BlockVector3 position) {
        Preconditions.checkNotNull((Object)world);
        Preconditions.checkNotNull((Object)((Object)position));
        return new Location(world, (double)position.x(), (double)position.y(), (double)position.z());
    }

    public static Location adapt(org.bukkit.World world, com.sk89q.worldedit.util.Location location) {
        Preconditions.checkNotNull((Object)world);
        Preconditions.checkNotNull((Object)location);
        return new Location(world, location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
    }

    public static Vector3 asVector(Location location) {
        Preconditions.checkNotNull((Object)location);
        return Vector3.at(location.getX(), location.getY(), location.getZ());
    }

    public static BlockVector3 asBlockVector(Location location) {
        Preconditions.checkNotNull((Object)location);
        return BlockVector3.at(location.getX(), location.getY(), location.getZ());
    }

    public static Entity adapt(org.bukkit.entity.Entity entity) {
        Preconditions.checkNotNull((Object)entity);
        return new BukkitEntity(entity);
    }

    public static Material adapt(ItemType itemType) {
        Preconditions.checkNotNull((Object)itemType);
        NamespacedKey key = (NamespacedKey)Preconditions.checkNotNull((Object)NamespacedKey.fromString((String)itemType.id()), (Object)"Item type key is invalid");
        return (Material)Registry.MATERIAL.get(key);
    }

    public static Material adapt(BlockType blockType) {
        Preconditions.checkNotNull((Object)blockType);
        NamespacedKey key = (NamespacedKey)Preconditions.checkNotNull((Object)NamespacedKey.fromString((String)blockType.id()), (Object)"Block type key is invalid");
        return (Material)Registry.MATERIAL.get(key);
    }

    public static GameMode adapt(org.bukkit.GameMode gameMode) {
        Preconditions.checkNotNull((Object)gameMode);
        return GameModes.get(gameMode.name().toLowerCase(Locale.ROOT));
    }

    public static BiomeType adapt(Biome biome) {
        return biomeBiomeTypeCache.computeIfAbsent(biome, b -> BiomeTypes.get(b.getKey().toString()));
    }

    public static Biome adapt(BiomeType biomeType) {
        NamespacedKey biomeKey = NamespacedKey.fromString((String)biomeType.id());
        if (biomeKey == null) {
            throw new IllegalArgumentException("Biome key '" + biomeType.id() + "' does not map to Bukkit");
        }
        try {
            return biomeTypeBiomeCache.computeIfAbsent(biomeType, type -> (Biome)Registry.BIOME.get(biomeKey));
        }
        catch (IllegalArgumentException e) {
            return null;
        }
    }

    public static com.sk89q.worldedit.world.entity.EntityType adapt(EntityType entityType) {
        return EntityTypes.get(entityType.getKey().toString());
    }

    public static EntityType adapt(com.sk89q.worldedit.world.entity.EntityType entityType) {
        NamespacedKey entityKey = NamespacedKey.fromString((String)entityType.id());
        if (entityKey == null) {
            throw new IllegalArgumentException("Entity key '" + entityType.id() + "' does not map to Bukkit");
        }
        return (EntityType)Registry.ENTITY_TYPE.get(entityKey);
    }

    @Nullable
    public static BlockType asBlockType(Material material) {
        Preconditions.checkNotNull((Object)material);
        return materialBlockTypeCache.computeIfAbsent(material, input -> BlockTypes.get(material.getKey().toString()));
    }

    @Nullable
    public static ItemType asItemType(Material material) {
        Preconditions.checkNotNull((Object)material);
        return materialItemTypeCache.computeIfAbsent(material, input -> ItemTypes.get(material.getKey().toString()));
    }

    public static BlockState adapt(BlockData blockData) {
        Preconditions.checkNotNull((Object)blockData);
        BukkitImplAdapter adapter = WorldEditPlugin.getInstance().getBukkitImplAdapter();
        if (adapter == null) {
            return blockStateStringCache.computeIfAbsent(blockData.getAsString(), input -> {
                try {
                    return ((BaseBlock)WorldEdit.getInstance().getBlockFactory().parseFromInput((String)input, TO_BLOCK_CONTEXT)).toImmutableState();
                }
                catch (InputParseException e) {
                    e.printStackTrace();
                    return null;
                }
            });
        }
        return blockStateCache.computeIfAbsent(adapter.getInternalBlockStateId(blockData).orElseGet(() -> blockData.getAsString().hashCode()), input -> {
            try {
                return ((BaseBlock)WorldEdit.getInstance().getBlockFactory().parseFromInput(blockData.getAsString(), TO_BLOCK_CONTEXT)).toImmutableState();
            }
            catch (InputParseException e) {
                e.printStackTrace();
                return null;
            }
        });
    }

    public static <B extends BlockStateHolder<B>> BlockData adapt(B block) {
        Preconditions.checkNotNull(block);
        int cacheKey = BlockStateIdAccess.getBlockStateId(block.toImmutableState());
        if (cacheKey == BlockStateIdAccess.invalidId()) {
            cacheKey = block.hashCode();
        }
        return blockDataCache.computeIfAbsent(cacheKey, input -> Bukkit.createBlockData((String)block.toImmutableState().getAsString())).clone();
    }

    public static BlockState asBlockState(ItemStack itemStack) throws WorldEditException {
        Preconditions.checkNotNull((Object)itemStack);
        if (itemStack.getType().isBlock()) {
            return BukkitAdapter.adapt(itemStack.getType().createBlockData());
        }
        throw new NotABlockException();
    }

    public static BaseItemStack adapt(ItemStack itemStack) {
        Preconditions.checkNotNull((Object)itemStack);
        BukkitImplAdapter adapter = WorldEditPlugin.getInstance().getBukkitImplAdapter();
        if (adapter != null) {
            return adapter.adapt(itemStack);
        }
        return new BaseItemStack(ItemTypes.get(itemStack.getType().getKey().toString()), itemStack.getAmount());
    }

    public static ItemStack adapt(BaseItemStack item) {
        Preconditions.checkNotNull((Object)item);
        BukkitImplAdapter adapter = WorldEditPlugin.getInstance().getBukkitImplAdapter();
        if (adapter != null) {
            return adapter.adapt(item);
        }
        return new ItemStack(BukkitAdapter.adapt(item.getType()), item.getAmount());
    }

    static {
        TO_BLOCK_CONTEXT.setRestricted(false);
        TO_BLOCK_CONTEXT.setTryLegacy(false);
        biomeBiomeTypeCache = new ConcurrentHashMap<Biome, BiomeType>();
        biomeTypeBiomeCache = new ConcurrentHashMap<BiomeType, Biome>();
        materialBlockTypeCache = new ConcurrentHashMap<Material, BlockType>();
        materialItemTypeCache = new ConcurrentHashMap<Material, ItemType>();
        blockStateCache = Int2ObjectMaps.synchronize(new Int2ObjectOpenHashMap());
        blockStateStringCache = new ConcurrentHashMap<String, BlockState>();
        blockDataCache = Int2ObjectMaps.synchronize(new Int2ObjectOpenHashMap());
    }
}

