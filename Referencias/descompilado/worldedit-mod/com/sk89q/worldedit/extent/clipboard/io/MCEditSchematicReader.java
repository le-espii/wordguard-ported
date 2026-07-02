/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.UnmodifiableIterator
 *  org.apache.logging.log4j.Logger
 */
package com.sk89q.worldedit.extent.clipboard.io;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.UnmodifiableIterator;
import com.sk89q.jnbt.NBTInputStream;
import com.sk89q.jnbt.NamedTag;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.entity.BaseEntity;
import com.sk89q.worldedit.extension.platform.Capability;
import com.sk89q.worldedit.extent.Extent;
import com.sk89q.worldedit.extent.clipboard.BlockArrayClipboard;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.extent.clipboard.io.legacycompat.BannerBlockCompatibilityHandler;
import com.sk89q.worldedit.extent.clipboard.io.legacycompat.BedBlockCompatibilityHandler;
import com.sk89q.worldedit.extent.clipboard.io.legacycompat.EntityNBTCompatibilityHandler;
import com.sk89q.worldedit.extent.clipboard.io.legacycompat.FlowerPotCompatibilityHandler;
import com.sk89q.worldedit.extent.clipboard.io.legacycompat.NBTCompatibilityHandler;
import com.sk89q.worldedit.extent.clipboard.io.legacycompat.NoteBlockCompatibilityHandler;
import com.sk89q.worldedit.extent.clipboard.io.legacycompat.Pre13HangingCompatibilityHandler;
import com.sk89q.worldedit.extent.clipboard.io.legacycompat.SignCompatibilityHandler;
import com.sk89q.worldedit.extent.clipboard.io.legacycompat.SkullBlockCompatibilityHandler;
import com.sk89q.worldedit.internal.util.LogManagerCompat;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.util.Location;
import com.sk89q.worldedit.util.collection.BlockMap;
import com.sk89q.worldedit.util.concurrency.LazyReference;
import com.sk89q.worldedit.world.DataFixer;
import com.sk89q.worldedit.world.block.BaseBlock;
import com.sk89q.worldedit.world.block.BlockState;
import com.sk89q.worldedit.world.entity.EntityType;
import com.sk89q.worldedit.world.entity.EntityTypes;
import com.sk89q.worldedit.world.registry.LegacyMapper;
import com.sk89q.worldedit.world.storage.NBTConversions;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;
import org.apache.logging.log4j.Logger;
import org.enginehub.linbus.stream.LinStream;
import org.enginehub.linbus.tree.LinByteArrayTag;
import org.enginehub.linbus.tree.LinCompoundTag;
import org.enginehub.linbus.tree.LinListTag;
import org.enginehub.linbus.tree.LinRootEntry;
import org.enginehub.linbus.tree.LinTagType;

public class MCEditSchematicReader
implements ClipboardReader {
    private static final Logger LOGGER = LogManagerCompat.getLogger();
    private final LinStream rootStream;
    private final DataFixer fixer;
    private static final ImmutableList<NBTCompatibilityHandler> COMPATIBILITY_HANDLERS = ImmutableList.of((Object)new SignCompatibilityHandler(), (Object)new FlowerPotCompatibilityHandler(), (Object)new NoteBlockCompatibilityHandler(), (Object)new SkullBlockCompatibilityHandler(), (Object)new BannerBlockCompatibilityHandler(), (Object)new BedBlockCompatibilityHandler());
    private static final ImmutableList<EntityNBTCompatibilityHandler> ENTITY_COMPATIBILITY_HANDLERS = ImmutableList.of((Object)new Pre13HangingCompatibilityHandler());

    @Deprecated
    public MCEditSchematicReader(NBTInputStream inputStream) {
        try {
            NamedTag tag = inputStream.readNamedTag();
            this.rootStream = new LinRootEntry(tag.getName(), (LinCompoundTag)tag.getTag().toLinTag()).linStream();
            this.fixer = WorldEdit.getInstance().getPlatformManager().queryCapability(Capability.WORLD_EDITING).getDataFixer();
        }
        catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    MCEditSchematicReader(LinStream rootStream) {
        this.rootStream = rootStream;
        this.fixer = WorldEdit.getInstance().getPlatformManager().queryCapability(Capability.WORLD_EDITING).getDataFixer();
    }

    @Override
    public Clipboard read() throws IOException {
        CuboidRegion region;
        BlockVector3 origin;
        LinRootEntry root = LinRootEntry.readFrom(this.rootStream);
        if (!root.name().equals("Schematic")) {
            throw new IOException("Tag 'Schematic' does not exist or is not first");
        }
        LinCompoundTag schematicTag = root.value();
        if (!schematicTag.value().containsKey("Blocks")) {
            throw new IOException("Schematic file is missing a 'Blocks' tag");
        }
        String materials = schematicTag.getTag("Materials", LinTagType.stringTag()).value();
        if (!materials.equals("Alpha")) {
            throw new IOException("Schematic file is not an Alpha schematic");
        }
        int width = schematicTag.getTag("Width", LinTagType.shortTag()).valueAsShort();
        int height = schematicTag.getTag("Height", LinTagType.shortTag()).valueAsShort();
        int length = schematicTag.getTag("Length", LinTagType.shortTag()).valueAsShort();
        try {
            int originX = schematicTag.getTag("WEOriginX", LinTagType.intTag()).valueAsInt();
            int originY = schematicTag.getTag("WEOriginY", LinTagType.intTag()).valueAsInt();
            int originZ = schematicTag.getTag("WEOriginZ", LinTagType.intTag()).valueAsInt();
            BlockVector3 min = BlockVector3.at(originX, originY, originZ);
            int offsetX = schematicTag.getTag("WEOffsetX", LinTagType.intTag()).valueAsInt();
            int offsetY = schematicTag.getTag("WEOffsetY", LinTagType.intTag()).valueAsInt();
            int offsetZ = schematicTag.getTag("WEOffsetZ", LinTagType.intTag()).valueAsInt();
            BlockVector3 offset = BlockVector3.at(offsetX, offsetY, offsetZ);
            origin = min.subtract(offset);
            region = new CuboidRegion(min, min.add(width, height, length).subtract(BlockVector3.ONE));
        }
        catch (NoSuchElementException e) {
            origin = BlockVector3.ZERO;
            region = new CuboidRegion(origin, origin.add(width, height, length).subtract(BlockVector3.ONE));
        }
        byte[] blockId = schematicTag.getTag("Blocks", LinTagType.byteArrayTag()).value();
        byte[] blockData = schematicTag.getTag("Data", LinTagType.byteArrayTag()).value();
        byte[] addId = new byte[]{};
        short[] blocks = new short[blockId.length];
        LinByteArrayTag addBlocks = schematicTag.findTag("AddBlocks", LinTagType.byteArrayTag());
        if (addBlocks != null) {
            addId = addBlocks.value();
        }
        for (int index = 0; index < blockId.length; ++index) {
            blocks[index] = index >> 1 >= addId.length ? (short)(blockId[index] & 0xFF) : ((index & 1) == 0 ? (short)(((addId[index >> 1] & 0xF) << 8) + (blockId[index] & 0xFF)) : (short)(((addId[index >> 1] & 0xF0) << 4) + (blockId[index] & 0xFF)));
        }
        LinListTag<LinCompoundTag> tileEntityTag = schematicTag.findListTag("TileEntities", LinTagType.compoundTag());
        Object tileEntities = tileEntityTag == null ? List.of() : tileEntityTag.value();
        BlockMap<BaseBlock> tileEntityBlocks = BlockMap.createForBaseBlock();
        Iterator iterator = tileEntities.iterator();
        while (iterator.hasNext()) {
            NBTCompatibilityHandler handler;
            LinCompoundTag tag = (LinCompoundTag)iterator.next();
            LinCompoundTag.Builder newTag = tag.toBuilder();
            String id = tag.getTag("id", LinTagType.stringTag()).value();
            newTag.putString("id", this.convertBlockEntityId(id));
            int x = tag.getTag("x", LinTagType.intTag()).valueAsInt();
            int y = tag.getTag("y", LinTagType.intTag()).valueAsInt();
            int z = tag.getTag("z", LinTagType.intTag()).valueAsInt();
            int index = y * width * length + z * width + x;
            BlockState block = this.getBlockState(blocks[index], blockData[index]);
            if (block == null) continue;
            BaseBlock updatedBlock = block.toBaseBlock(LazyReference.from(newTag::build));
            UnmodifiableIterator unmodifiableIterator = COMPATIBILITY_HANDLERS.iterator();
            while (unmodifiableIterator.hasNext() && (updatedBlock = (handler = (NBTCompatibilityHandler)unmodifiableIterator.next()).updateNbt(updatedBlock)).getNbtReference() != null) {
            }
            if (this.fixer != null && updatedBlock.getNbtReference() != null) {
                try {
                    updatedBlock = updatedBlock.toBaseBlock(this.fixer.fixUp(DataFixer.FixTypes.BLOCK_ENTITY, updatedBlock.getNbt(), 1631));
                }
                catch (Exception e) {
                    LOGGER.warn("Failed to run DFU on a block entity. Is there something wrong with the schematic?", (Throwable)e);
                }
            }
            tileEntityBlocks.put(BlockVector3.at(x, y, z), updatedBlock);
        }
        BlockArrayClipboard clipboard = new BlockArrayClipboard(region);
        clipboard.setOrigin(origin);
        HashSet<Integer> unknownBlocks = new HashSet<Integer>();
        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                for (int z = 0; z < length; ++z) {
                    int index = y * width * length + z * width + x;
                    BlockVector3 pt = BlockVector3.at(x, y, z);
                    BaseBlock state = tileEntityBlocks.get((Object)pt);
                    if (state == null) {
                        BlockState blockState = this.getBlockState(blocks[index], blockData[index]);
                        if (blockState == null) {
                            short block = blocks[index];
                            byte data = blockData[index];
                            int combined = block << 8 | data;
                            if (!unknownBlocks.add(combined)) continue;
                            LOGGER.warn("Unknown block when loading schematic: " + block + ":" + data + ". This is most likely a bad schematic.");
                            continue;
                        }
                        state = blockState.toBaseBlock();
                    }
                    clipboard.setBlock(region.getMinimumPoint().add(pt), state);
                }
            }
        }
        LinListTag<LinCompoundTag> entityList = schematicTag.findListTag("Entities", LinTagType.compoundTag());
        if (entityList != null) {
            Iterator iterator2 = entityList.value().iterator();
            while (iterator2.hasNext()) {
                LinCompoundTag tag = (LinCompoundTag)iterator2.next();
                if (this.fixer != null) {
                    try {
                        tag = this.fixer.fixUp(DataFixer.FixTypes.ENTITY, tag, -1);
                    }
                    catch (Exception e) {
                        LOGGER.warn("Failed to run DFU on an entity. Is there something wrong with the schematic?", (Throwable)e);
                    }
                }
                String id = this.fixer != null ? tag.getTag("id", LinTagType.stringTag()).value() : this.convertEntityId(tag.getTag("id", LinTagType.stringTag()).value());
                Location location = NBTConversions.toLocation((Extent)clipboard, tag.getListTag("Pos", LinTagType.doubleTag()), tag.getListTag("Rotation", LinTagType.floatTag()));
                if (id.isEmpty()) continue;
                EntityType entityType = EntityTypes.get(id.toLowerCase(Locale.ROOT));
                if (entityType != null) {
                    if (this.fixer == null) {
                        for (EntityNBTCompatibilityHandler compatibilityHandler : ENTITY_COMPATIBILITY_HANDLERS) {
                            tag = compatibilityHandler.updateNbt(entityType, tag);
                        }
                    }
                    BaseEntity state = new BaseEntity(entityType, LazyReference.computed(tag));
                    clipboard.createEntity(location, state);
                    continue;
                }
                LOGGER.warn("Unknown entity when pasting schematic: " + id.toLowerCase(Locale.ROOT));
            }
        }
        return clipboard;
    }

    private String convertEntityId(String id) {
        return switch (id) {
            case "AreaEffectCloud" -> "area_effect_cloud";
            case "ArmorStand" -> "armor_stand";
            case "CaveSpider" -> "cave_spider";
            case "MinecartChest" -> "chest_minecart";
            case "DragonFireball" -> "dragon_fireball";
            case "ThrownEgg" -> "egg";
            case "EnderDragon" -> "ender_dragon";
            case "ThrownEnderpearl" -> "ender_pearl";
            case "FallingSand" -> "falling_block";
            case "FireworksRocketEntity" -> "fireworks_rocket";
            case "MinecartFurnace" -> "furnace_minecart";
            case "MinecartHopper" -> "hopper_minecart";
            case "EntityHorse" -> "horse";
            case "ItemFrame" -> "item_frame";
            case "LeashKnot" -> "leash_knot";
            case "LightningBolt" -> "lightning_bolt";
            case "LavaSlime" -> "magma_cube";
            case "MinecartRideable" -> "minecart";
            case "MushroomCow" -> "mooshroom";
            case "Ozelot" -> "ocelot";
            case "PolarBear" -> "polar_bear";
            case "ThrownPotion" -> "potion";
            case "ShulkerBullet" -> "shulker_bullet";
            case "SmallFireball" -> "small_fireball";
            case "MinecartSpawner" -> "spawner_minecart";
            case "SpectralArrow" -> "spectral_arrow";
            case "PrimedTnt" -> "tnt";
            case "MinecartTNT" -> "tnt_minecart";
            case "VillagerGolem" -> "villager_golem";
            case "WitherBoss" -> "wither";
            case "WitherSkull" -> "wither_skull";
            case "PigZombie" -> "zombie_pigman";
            case "XPOrb", "xp_orb" -> "experience_orb";
            case "ThrownExpBottle", "xp_bottle" -> "experience_bottle";
            case "EyeOfEnderSignal", "eye_of_ender_signal" -> "eye_of_ender";
            case "EnderCrystal", "ender_crystal" -> "end_crystal";
            case "fireworks_rocket" -> "firework_rocket";
            case "MinecartCommandBlock", "commandblock_minecart" -> "command_block_minecart";
            case "snowman" -> "snow_golem";
            case "villager_golem" -> "iron_golem";
            case "evocation_fangs" -> "evoker_fangs";
            case "evocation_illager" -> "evoker";
            case "vindication_illager" -> "vindicator";
            case "illusion_illager" -> "illusioner";
            default -> id;
        };
    }

    private String convertBlockEntityId(String id) {
        return switch (id) {
            case "Cauldron" -> "brewing_stand";
            case "Control" -> "command_block";
            case "DLDetector" -> "daylight_detector";
            case "Trap" -> "dispenser";
            case "EnchantTable" -> "enchanting_table";
            case "EndGateway" -> "end_gateway";
            case "AirPortal" -> "end_portal";
            case "EnderChest" -> "ender_chest";
            case "FlowerPot" -> "flower_pot";
            case "RecordPlayer" -> "jukebox";
            case "MobSpawner" -> "mob_spawner";
            case "Music", "noteblock" -> "note_block";
            case "Structure" -> "structure_block";
            case "Chest" -> "chest";
            case "Sign" -> "sign";
            case "Banner" -> "banner";
            case "Beacon" -> "beacon";
            case "Comparator" -> "comparator";
            case "Dropper" -> "dropper";
            case "Furnace" -> "furnace";
            case "Hopper" -> "hopper";
            case "Skull" -> "skull";
            default -> id;
        };
    }

    private BlockState getBlockState(int id, int data) {
        BlockState foundBlock = LegacyMapper.getInstance().getBlockFromLegacy(id, data);
        if (foundBlock == null && data != 0) {
            return LegacyMapper.getInstance().getBlockFromLegacy(id, 0);
        }
        return foundBlock;
    }

    @Override
    public void close() throws IOException {
    }
}

