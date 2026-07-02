/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.sk89q.worldedit.internal.cui;

import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.entity.Player;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.RegionSelector;
import com.sk89q.worldedit.regions.selector.CuboidRegionSelector;
import com.sk89q.worldedit.util.Location;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldedit.world.block.BaseBlock;
import com.sk89q.worldedit.world.block.BlockTypes;
import javax.annotation.Nullable;
import org.enginehub.linbus.tree.LinCompoundTag;

public class ServerCUIHandler {
    private static final int MAX_DISTANCE = 32;

    private ServerCUIHandler() {
    }

    public static int getMaxServerCuiSize() {
        return 48;
    }

    @Nullable
    public static BaseBlock createStructureBlock(Player player) {
        int length;
        int height;
        int width;
        int posZ;
        int posY;
        int posX;
        LocalSession session = WorldEdit.getInstance().getSessionManager().get(player);
        World selectionWorld = session.getSelectionWorld();
        if (!player.getWorld().equals(selectionWorld)) {
            return null;
        }
        RegionSelector regionSelector = session.getRegionSelector(selectionWorld);
        if (regionSelector instanceof CuboidRegionSelector) {
            if (regionSelector.isDefined()) {
                try {
                    region = ((CuboidRegionSelector)regionSelector).getRegion();
                    posX = region.getMinimumPoint().x();
                    posY = region.getMinimumPoint().y();
                    posZ = region.getMinimumPoint().z();
                    width = region.getWidth();
                    height = region.getHeight();
                    length = region.getLength();
                }
                catch (IncompleteRegionException e) {
                    e.printStackTrace();
                    return null;
                }
            } else {
                BlockVector3 point;
                region = ((CuboidRegionSelector)regionSelector).getIncompleteRegion();
                if (region.getPos1() != null) {
                    point = region.getPos1();
                } else if (region.getPos2() != null) {
                    point = region.getPos2();
                } else {
                    return null;
                }
                posX = point.x();
                posY = point.y();
                posZ = point.z();
                width = 1;
                height = 1;
                length = 1;
            }
        } else {
            return null;
        }
        int maxSize = ServerCUIHandler.getMaxServerCuiSize();
        if (width > maxSize || length > maxSize || height > maxSize) {
            return null;
        }
        Location location = player.getLocation();
        double rotX = location.getYaw();
        double rotY = location.getPitch();
        double xz = Math.cos(Math.toRadians(rotY));
        int x = (int)(location.getX() - -xz * Math.sin(Math.toRadians(rotX)) * 12.0);
        int z = (int)(location.getZ() - xz * Math.cos(Math.toRadians(rotX)) * 12.0);
        int y = Math.max(player.getWorld().getMinY(), Math.min(Math.min(player.getWorld().getMaxY(), posY + 32), posY + 3));
        if (Math.abs(posX -= x) > 32 || Math.abs(posY -= y) > 32 || Math.abs(posZ -= z) > 32) {
            return null;
        }
        LinCompoundTag.Builder structureTag = LinCompoundTag.builder();
        structureTag.putString("name", "worldedit:" + player.getName());
        structureTag.putString("author", player.getName());
        structureTag.putString("metadata", "");
        structureTag.putInt("x", x);
        structureTag.putInt("y", y);
        structureTag.putInt("z", z);
        structureTag.putInt("posX", posX);
        structureTag.putInt("posY", posY);
        structureTag.putInt("posZ", posZ);
        structureTag.putInt("sizeX", width);
        structureTag.putInt("sizeY", height);
        structureTag.putInt("sizeZ", length);
        structureTag.putString("rotation", "NONE");
        structureTag.putString("mirror", "NONE");
        structureTag.putString("mode", "SAVE");
        structureTag.putByte("ignoreEntities", (byte)1);
        structureTag.putByte("showboundingbox", (byte)1);
        structureTag.putString("id", BlockTypes.STRUCTURE_BLOCK.id());
        return BlockTypes.STRUCTURE_BLOCK.getDefaultState().toBaseBlock(structureTag.build());
    }
}

