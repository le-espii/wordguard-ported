/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.extent.clipboard.io.legacycompat;

import com.sk89q.worldedit.extent.clipboard.io.legacycompat.EntityNBTCompatibilityHandler;
import com.sk89q.worldedit.internal.helper.MCDirections;
import com.sk89q.worldedit.util.Direction;
import com.sk89q.worldedit.world.entity.EntityType;
import org.enginehub.linbus.tree.LinCompoundTag;
import org.enginehub.linbus.tree.LinNumberTag;

public class Pre13HangingCompatibilityHandler
implements EntityNBTCompatibilityHandler {
    @Override
    public LinCompoundTag updateNbt(EntityType type, LinCompoundTag tag) {
        Direction newDirection;
        if (!type.id().startsWith("minecraft:")) {
            return tag;
        }
        Object v = tag.value().get("Dir");
        if (v instanceof LinNumberTag) {
            LinNumberTag legacyDir = (LinNumberTag)v;
            newDirection = MCDirections.fromPre13Hanging(MCDirections.fromLegacyHanging(((Number)legacyDir.value()).byteValue()));
        } else {
            v = tag.value().get("Direction");
            if (v instanceof LinNumberTag) {
                LinNumberTag legacyDirection = (LinNumberTag)v;
                newDirection = MCDirections.fromPre13Hanging(((Number)legacyDirection.value()).intValue());
            } else {
                v = tag.value().get("Facing");
                if (v instanceof LinNumberTag) {
                    LinNumberTag legacyFacing = (LinNumberTag)v;
                    newDirection = MCDirections.fromPre13Hanging(((Number)legacyFacing.value()).intValue());
                } else {
                    return tag;
                }
            }
        }
        byte hangingByte = (byte)MCDirections.toHanging(newDirection);
        LinCompoundTag.Builder builder = tag.toBuilder();
        builder.putByte("Facing", hangingByte);
        return builder.build();
    }
}

