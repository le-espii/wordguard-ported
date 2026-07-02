/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sk89q.worldedit.world.entity.EntityType
 *  com.sk89q.worldedit.world.entity.EntityTypes
 *  javax.annotation.Nullable
 */
package com.sk89q.worldguard.protection.flags;

import com.sk89q.worldedit.world.entity.EntityType;
import com.sk89q.worldedit.world.entity.EntityTypes;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.FlagContext;
import com.sk89q.worldguard.protection.flags.InvalidFlagFormat;
import com.sk89q.worldguard.protection.flags.RegionGroup;
import javax.annotation.Nullable;

@Deprecated
public class EntityTypeFlag
extends Flag<EntityType> {
    protected EntityTypeFlag(String name, @Nullable RegionGroup defaultGroup) {
        super(name, defaultGroup);
    }

    protected EntityTypeFlag(String name) {
        super(name);
    }

    @Override
    public EntityType parseInput(FlagContext context) throws InvalidFlagFormat {
        String input = context.getUserInput();
        EntityType entityType = this.unmarshal(input = input.trim());
        if (entityType == null) {
            throw new InvalidFlagFormat("Unknown entity type: " + input);
        }
        return entityType;
    }

    @Override
    public EntityType unmarshal(@Nullable Object o) {
        return EntityTypes.get((String)String.valueOf(o).toLowerCase());
    }

    @Override
    public Object marshal(EntityType o) {
        return o.id();
    }
}

