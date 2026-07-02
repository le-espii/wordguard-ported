/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 */
package com.sk89q.worldedit.blocks;

import com.google.common.collect.ImmutableMap;
import com.sk89q.jnbt.CompoundTag;
import com.sk89q.jnbt.ListTag;
import com.sk89q.jnbt.NBTUtils;
import com.sk89q.jnbt.ShortTag;
import com.sk89q.jnbt.StringTag;
import com.sk89q.jnbt.Tag;
import com.sk89q.worldedit.blocks.LegacyBaseBlockWrapper;
import com.sk89q.worldedit.world.block.BlockState;
import com.sk89q.worldedit.world.storage.InvalidFormatException;
import java.util.HashMap;
import java.util.Map;

@Deprecated
public class MobSpawnerBlock
extends LegacyBaseBlockWrapper {
    private String mobType;
    private short delay = (short)-1;
    private short spawnCount = (short)4;
    private short spawnRange = (short)4;
    private CompoundTag spawnData;
    private ListTag<?, ?> spawnPotentials;
    private short minSpawnDelay = (short)200;
    private short maxSpawnDelay = (short)800;
    private short maxNearbyEntities = (short)6;
    private short requiredPlayerRange = (short)16;

    public MobSpawnerBlock(BlockState blockState) {
        super(blockState);
    }

    public MobSpawnerBlock(BlockState blockState, String mobType) {
        super(blockState);
        this.mobType = mobType;
    }

    public String getMobType() {
        return this.mobType;
    }

    public void setMobType(String mobType) {
        this.mobType = mobType;
    }

    public short getDelay() {
        return this.delay;
    }

    public void setDelay(short delay) {
        this.delay = delay;
    }

    @Override
    @Deprecated
    public boolean hasNbtData() {
        return true;
    }

    @Override
    public String getNbtId() {
        return "minecraft:mob_spawner";
    }

    @Override
    @Deprecated
    public CompoundTag getNbtData() {
        HashMap values = new HashMap();
        values.put("Delay", new ShortTag(this.delay));
        values.put("SpawnCount", new ShortTag(this.spawnCount));
        values.put("SpawnRange", new ShortTag(this.spawnRange));
        values.put("MinSpawnDelay", new ShortTag(this.minSpawnDelay));
        values.put("MaxSpawnDelay", new ShortTag(this.maxSpawnDelay));
        values.put("MaxNearbyEntities", new ShortTag(this.maxNearbyEntities));
        values.put("RequiredPlayerRange", new ShortTag(this.requiredPlayerRange));
        if (this.spawnData == null) {
            values.put("SpawnData", new CompoundTag((Map<String, Tag<?, ?>>)ImmutableMap.of((Object)"entity", (Object)new CompoundTag((Map<String, Tag<?, ?>>)ImmutableMap.of((Object)"id", (Object)new StringTag(this.mobType))))));
        } else {
            values.put("SpawnData", new CompoundTag((Map<String, Tag<?, ?>>)this.spawnData.getValue()));
        }
        if (this.spawnPotentials != null) {
            values.put("SpawnPotentials", new ListTag(CompoundTag.class, this.spawnPotentials.getValue()));
        }
        return new CompoundTag(values);
    }

    @Override
    @Deprecated
    public void setNbtData(CompoundTag rootTag) {
        if (rootTag == null) {
            return;
        }
        Object values = rootTag.getValue();
        Tag t = (Tag)values.get("id");
        if (!(t instanceof StringTag) || !((StringTag)t).getValue().equals(this.getNbtId())) {
            throw new RuntimeException(String.format("'%s' tile entity expected", this.getNbtId()));
        }
        try {
            CompoundTag spawnDataTag = NBTUtils.getChildTag(values, "SpawnData", CompoundTag.class);
            String mobType = spawnDataTag.getString("id");
            if (mobType.isEmpty()) {
                throw new InvalidFormatException("No spawn id.");
            }
            this.mobType = mobType;
            this.spawnData = spawnDataTag;
        }
        catch (InvalidFormatException ignored) {
            throw new RuntimeException("Invalid mob spawner data: no SpawnData and/or no Delay");
        }
        try {
            ShortTag delayTag = NBTUtils.getChildTag(values, "Delay", ShortTag.class);
            this.delay = delayTag.getValue();
        }
        catch (InvalidFormatException ignored) {
            this.delay = (short)-1;
        }
        ShortTag spawnCountTag = null;
        ShortTag spawnRangeTag = null;
        ShortTag minSpawnDelayTag = null;
        ShortTag maxSpawnDelayTag = null;
        ShortTag maxNearbyEntitiesTag = null;
        ShortTag requiredPlayerRangeTag = null;
        ListTag spawnPotentialsTag = null;
        try {
            spawnCountTag = NBTUtils.getChildTag(values, "SpawnCount", ShortTag.class);
        }
        catch (InvalidFormatException invalidFormatException) {
            // empty catch block
        }
        try {
            spawnRangeTag = NBTUtils.getChildTag(values, "SpawnRange", ShortTag.class);
        }
        catch (InvalidFormatException invalidFormatException) {
            // empty catch block
        }
        try {
            minSpawnDelayTag = NBTUtils.getChildTag(values, "MinSpawnDelay", ShortTag.class);
        }
        catch (InvalidFormatException invalidFormatException) {
            // empty catch block
        }
        try {
            maxSpawnDelayTag = NBTUtils.getChildTag(values, "MaxSpawnDelay", ShortTag.class);
        }
        catch (InvalidFormatException invalidFormatException) {
            // empty catch block
        }
        try {
            maxNearbyEntitiesTag = NBTUtils.getChildTag(values, "MaxNearbyEntities", ShortTag.class);
        }
        catch (InvalidFormatException invalidFormatException) {
            // empty catch block
        }
        try {
            requiredPlayerRangeTag = NBTUtils.getChildTag(values, "RequiredPlayerRange", ShortTag.class);
        }
        catch (InvalidFormatException invalidFormatException) {
            // empty catch block
        }
        try {
            spawnPotentialsTag = NBTUtils.getChildTag(values, "SpawnPotentials", ListTag.class);
        }
        catch (InvalidFormatException invalidFormatException) {
            // empty catch block
        }
        if (spawnCountTag != null) {
            this.spawnCount = spawnCountTag.getValue();
        }
        if (spawnRangeTag != null) {
            this.spawnRange = spawnRangeTag.getValue();
        }
        if (minSpawnDelayTag != null) {
            this.minSpawnDelay = minSpawnDelayTag.getValue();
        }
        if (maxSpawnDelayTag != null) {
            this.maxSpawnDelay = maxSpawnDelayTag.getValue();
        }
        if (maxNearbyEntitiesTag != null) {
            this.maxNearbyEntities = maxNearbyEntitiesTag.getValue();
        }
        if (requiredPlayerRangeTag != null) {
            this.requiredPlayerRange = requiredPlayerRangeTag.getValue();
        }
        if (spawnPotentialsTag != null) {
            this.spawnPotentials = new ListTag(CompoundTag.class, spawnPotentialsTag.getValue());
        }
    }
}

