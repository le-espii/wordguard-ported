/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 */
package com.sk89q.worldedit.world.storage;

import com.google.common.base.Preconditions;
import com.sk89q.jnbt.ListTag;
import com.sk89q.worldedit.extent.Extent;
import com.sk89q.worldedit.util.Location;
import org.enginehub.linbus.tree.LinDoubleTag;
import org.enginehub.linbus.tree.LinFloatTag;
import org.enginehub.linbus.tree.LinListTag;

public final class NBTConversions {
    private NBTConversions() {
    }

    @Deprecated
    public static Location toLocation(Extent extent, ListTag<?, ?> positionTag, ListTag<?, ?> directionTag) {
        Preconditions.checkNotNull((Object)extent);
        Preconditions.checkNotNull(positionTag);
        Preconditions.checkNotNull(directionTag);
        return new Location(extent, positionTag.asDouble(0), positionTag.asDouble(1), positionTag.asDouble(2), directionTag.getFloat(0), directionTag.getFloat(1));
    }

    public static Location toLocation(Extent extent, LinListTag<LinDoubleTag> positionTag, LinListTag<LinFloatTag> rotationTag) {
        int posTagSize = positionTag.value().size();
        int rotTagSize = rotationTag.value().size();
        return new Location(extent, posTagSize > 0 ? positionTag.get(0).valueAsDouble() : 0.0, posTagSize > 1 ? positionTag.get(1).valueAsDouble() : 0.0, posTagSize > 2 ? positionTag.get(2).valueAsDouble() : 0.0, rotTagSize > 0 ? rotationTag.get(0).valueAsFloat() : 0.0f, rotTagSize > 1 ? rotationTag.get(1).valueAsFloat() : 0.0f);
    }
}

