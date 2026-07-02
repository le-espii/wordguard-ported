/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.sk89q.worldedit.world.registry;

import com.sk89q.worldedit.entity.BaseEntity;
import com.sk89q.worldedit.world.registry.EntityRegistry;
import javax.annotation.Nullable;

public class NullEntityRegistry
implements EntityRegistry {
    @Override
    @Nullable
    public BaseEntity createFromId(String id) {
        return null;
    }
}

