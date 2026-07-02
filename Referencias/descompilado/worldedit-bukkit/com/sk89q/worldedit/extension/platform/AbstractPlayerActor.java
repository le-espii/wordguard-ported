/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.sk89q.worldedit.extension.platform;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.NotABlockException;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.entity.Player;
import com.sk89q.worldedit.extension.platform.Actor;
import com.sk89q.worldedit.extent.Extent;
import com.sk89q.worldedit.function.mask.Mask;
import com.sk89q.worldedit.internal.cui.CUIEvent;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.math.Vector3;
import com.sk89q.worldedit.util.Direction;
import com.sk89q.worldedit.util.HandSide;
import com.sk89q.worldedit.util.Location;
import com.sk89q.worldedit.util.TargetBlock;
import com.sk89q.worldedit.util.auth.AuthorizationException;
import com.sk89q.worldedit.util.formatting.text.TranslatableComponent;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldedit.world.block.BaseBlock;
import com.sk89q.worldedit.world.block.BlockCategories;
import com.sk89q.worldedit.world.block.BlockState;
import com.sk89q.worldedit.world.block.BlockStateHolder;
import com.sk89q.worldedit.world.block.BlockType;
import com.sk89q.worldedit.world.block.BlockTypes;
import com.sk89q.worldedit.world.gamemode.GameMode;
import com.sk89q.worldedit.world.gamemode.GameModes;
import com.sk89q.worldedit.world.item.ItemType;
import com.sk89q.worldedit.world.item.ItemTypes;
import java.io.File;
import javax.annotation.Nullable;

public abstract class AbstractPlayerActor
implements Actor,
Player,
Cloneable {
    @Override
    public final Extent getExtent() {
        return this.getWorld();
    }

    private static Direction getDirection(double rot) {
        if (0.0 <= rot && rot < 22.5) {
            return Direction.SOUTH;
        }
        if (22.5 <= rot && rot < 67.5) {
            return Direction.SOUTHWEST;
        }
        if (67.5 <= rot && rot < 112.5) {
            return Direction.WEST;
        }
        if (112.5 <= rot && rot < 157.5) {
            return Direction.NORTHWEST;
        }
        if (157.5 <= rot && rot < 202.5) {
            return Direction.NORTH;
        }
        if (202.5 <= rot && rot < 247.5) {
            return Direction.NORTHEAST;
        }
        if (247.5 <= rot && rot < 292.5) {
            return Direction.EAST;
        }
        if (292.5 <= rot && rot < 337.5) {
            return Direction.SOUTHEAST;
        }
        if (337.5 <= rot && rot < 360.0) {
            return Direction.SOUTH;
        }
        return null;
    }

    @Override
    public boolean isHoldingPickAxe() {
        ItemType item = this.getItemInHand(HandSide.MAIN_HAND).getType();
        return item == ItemTypes.IRON_PICKAXE || item == ItemTypes.WOODEN_PICKAXE || item == ItemTypes.STONE_PICKAXE || item == ItemTypes.DIAMOND_PICKAXE || item == ItemTypes.GOLDEN_PICKAXE || item == ItemTypes.NETHERITE_PICKAXE;
    }

    @Override
    public void findFreePosition(Location searchPos) {
        int y;
        Extent world = searchPos.getExtent();
        int worldMinY = world.getMinimumPoint().y();
        int worldMaxY = world.getMaximumPoint().y();
        int x = searchPos.getBlockX();
        int origY = y = Math.max(worldMinY, searchPos.getBlockY());
        int z = searchPos.getBlockZ();
        int yPlusSearchHeight = y + WorldEdit.getInstance().getConfiguration().defaultVerticalHeight;
        int maxY = Math.min(worldMaxY, yPlusSearchHeight) + 2;
        int free = 0;
        while (y <= maxY) {
            free = !world.getBlock(BlockVector3.at(x, y, z)).getBlockType().getMaterial().isMovementBlocker() ? (int)((byte)(free + 1)) : 0;
            if (free == 2) {
                boolean worked = true;
                if (y - 1 != origY) {
                    worked = this.trySetPosition(Vector3.at((double)x + 0.5, y - 2 + 1, (double)z + 0.5));
                }
                if (worked) {
                    return;
                }
            }
            ++y;
        }
    }

    @Override
    public void setOnGround(Location searchPos) {
        int y;
        Extent world = searchPos.getExtent();
        int worldMinY = world.getMinimumPoint().y();
        int x = searchPos.getBlockX();
        int z = searchPos.getBlockZ();
        int yLessSearchHeight = y - WorldEdit.getInstance().getConfiguration().defaultVerticalHeight;
        int minY = Math.min(worldMinY, yLessSearchHeight) + 2;
        for (y = Math.max(worldMinY, searchPos.getBlockY()); y >= minY; --y) {
            BlockVector3 pos = BlockVector3.at(x, y, z);
            BlockState id = world.getBlock(pos);
            if (!id.getBlockType().getMaterial().isMovementBlocker() || !this.trySetPosition(Vector3.at((double)x + 0.5, y + 1, (double)z + 0.5))) continue;
            return;
        }
    }

    @Override
    public void findFreePosition() {
        this.findFreePosition(this.getBlockLocation());
    }

    private boolean isPlayerHarmingBlock(BlockVector3 location) {
        BlockType type = this.getWorld().getBlock(location).getBlockType();
        return type.getMaterial().isMovementBlocker() || type == BlockTypes.LAVA || BlockCategories.FIRE.contains(type);
    }

    private boolean isLocationGoodForStanding(BlockVector3 location) {
        if (this.isPlayerHarmingBlock(location.add(0, 1, 0))) {
            return false;
        }
        if (this.isPlayerHarmingBlock(location)) {
            return false;
        }
        return this.getWorld().getBlock(location.add(0, -1, 0)).getBlockType().getMaterial().isMovementBlocker();
    }

    @Override
    public boolean ascendLevel() {
        int y;
        World world = this.getWorld();
        Location pos = this.getBlockLocation();
        int x = pos.getBlockX();
        int z = pos.getBlockZ();
        int yPlusSearchHeight = y + WorldEdit.getInstance().getConfiguration().defaultVerticalHeight;
        int maxY = Math.min(world.getMaxY(), yPlusSearchHeight) + 2;
        for (y = Math.max(world.getMinY(), pos.getBlockY() + 1); y <= maxY; ++y) {
            if (!this.isLocationGoodForStanding(BlockVector3.at(x, y, z)) || !this.trySetPosition(Vector3.at((double)x + 0.5, y, (double)z + 0.5))) continue;
            return true;
        }
        return false;
    }

    @Override
    public boolean descendLevel() {
        int y;
        World world = this.getWorld();
        Location pos = this.getBlockLocation();
        int x = pos.getBlockX();
        int z = pos.getBlockZ();
        int yLessSearchHeight = y - WorldEdit.getInstance().getConfiguration().defaultVerticalHeight;
        int minY = Math.min(world.getMinY() + 1, yLessSearchHeight);
        for (y = Math.max(world.getMinY(), pos.getBlockY() - 1); y >= minY; --y) {
            if (!this.isLocationGoodForStanding(BlockVector3.at(x, y, z)) || !this.trySetPosition(Vector3.at((double)x + 0.5, y, (double)z + 0.5))) continue;
            return true;
        }
        return false;
    }

    @Override
    public boolean ascendToCeiling(int clearance) {
        return this.ascendToCeiling(clearance, true);
    }

    @Override
    public boolean ascendToCeiling(int clearance, boolean alwaysGlass) {
        int z;
        World world = this.getWorld();
        Location pos = this.getBlockLocation();
        int x = pos.getBlockX();
        int initialY = Math.max(world.getMinY(), pos.getBlockY());
        int y = Math.max(world.getMinY(), pos.getBlockY() + 2);
        if (!world.getBlock(BlockVector3.at(x, y, z = pos.getBlockZ())).getBlockType().getMaterial().isAir()) {
            return false;
        }
        int yPlusSearchHeight = y + WorldEdit.getInstance().getConfiguration().defaultVerticalHeight;
        int maxY = Math.min(world.getMaxY(), yPlusSearchHeight);
        while (y <= maxY) {
            if (world.getBlock(BlockVector3.at(x, y, z)).getBlockType().getMaterial().isMovementBlocker()) {
                int platformY = Math.max(initialY, y - 3 - clearance);
                if (platformY < initialY) {
                    return false;
                }
                if (platformY == initialY) {
                    return false;
                }
                this.floatAt(x, platformY + 1, z, alwaysGlass);
                return true;
            }
            ++y;
        }
        return false;
    }

    @Override
    public boolean ascendUpwards(int distance) {
        return this.ascendUpwards(distance, true);
    }

    @Override
    public boolean ascendUpwards(int distance, boolean alwaysGlass) {
        World world = this.getWorld();
        Location pos = this.getBlockLocation();
        int x = pos.getBlockX();
        int initialY = Math.max(world.getMinY(), pos.getBlockY());
        int z = pos.getBlockZ();
        int maxY = Math.min(world.getMaxY() + 1, initialY + distance);
        for (int y = Math.max(world.getMinY(), pos.getBlockY() + 1); y <= world.getMaxY() + 2 && !world.getBlock(BlockVector3.at(x, y, z)).getBlockType().getMaterial().isMovementBlocker() && y <= maxY + 1; ++y) {
            if (y != maxY + 1) continue;
            this.floatAt(x, y - 1, z, alwaysGlass);
            return true;
        }
        return false;
    }

    @Override
    public void floatAt(int x, int y, int z, boolean alwaysGlass) {
        if (alwaysGlass || !this.isAllowedToFly()) {
            BlockVector3 spot = BlockVector3.at(x, y - 1, z);
            World world = this.getWorld();
            if (!world.getBlock(spot).getBlockType().getMaterial().isMovementBlocker()) {
                try (EditSession session = WorldEdit.getInstance().newEditSession(this);){
                    session.setBlock(spot, BlockTypes.GLASS.getDefaultState());
                }
                catch (MaxChangedBlocksException maxChangedBlocksException) {}
            }
        } else {
            this.setFlying(true);
        }
        this.trySetPosition(Vector3.at((double)x + 0.5, y, (double)z + 0.5));
    }

    protected boolean isAllowedToFly() {
        return false;
    }

    protected void setFlying(boolean flying) {
    }

    @Override
    public Location getBlockOn() {
        Location location = this.getLocation();
        return location.setPosition(location.setY(location.getY() - 1.0).toVector().floor());
    }

    @Override
    public Location getBlockTrace(int range, boolean useLastBlock) {
        return this.getBlockTrace(range, useLastBlock, null);
    }

    @Override
    public Location getBlockTraceFace(int range, boolean useLastBlock) {
        return this.getBlockTraceFace(range, useLastBlock, null);
    }

    @Override
    public Location getBlockTrace(int range, boolean useLastBlock, @Nullable Mask stopMask) {
        TargetBlock tb = new TargetBlock(this, range, 0.2);
        if (stopMask != null) {
            tb.setStopMask(stopMask);
        }
        return useLastBlock ? tb.getAnyTargetBlock() : tb.getTargetBlock();
    }

    @Override
    public Location getBlockTraceFace(int range, boolean useLastBlock, @Nullable Mask stopMask) {
        TargetBlock tb = new TargetBlock(this, range, 0.2);
        if (stopMask != null) {
            tb.setStopMask(stopMask);
        }
        return useLastBlock ? tb.getAnyTargetBlockFace() : tb.getTargetBlockFace();
    }

    @Override
    public Location getBlockTrace(int range) {
        return this.getBlockTrace(range, false);
    }

    @Override
    public Location getSolidBlockTrace(int range) {
        TargetBlock tb = new TargetBlock(this, range, 0.2);
        return tb.getSolidTargetBlock();
    }

    @Override
    public Direction getCardinalDirection() {
        return this.getCardinalDirection(0);
    }

    @Override
    public Direction getCardinalDirection(int yawOffset) {
        Location location = this.getLocation();
        if ((double)location.getPitch() > 67.5) {
            return Direction.DOWN;
        }
        if ((double)location.getPitch() < -67.5) {
            return Direction.UP;
        }
        double rot = (location.getYaw() + (float)yawOffset) % 360.0f;
        if (rot < 0.0) {
            rot += 360.0;
        }
        return AbstractPlayerActor.getDirection(rot);
    }

    @Override
    public BaseBlock getBlockInHand(HandSide handSide) throws WorldEditException {
        ItemType typeId = this.getItemInHand(handSide).getType();
        if (typeId.hasBlockType()) {
            return typeId.getBlockType().getDefaultState().toBaseBlock();
        }
        throw new NotABlockException(typeId);
    }

    private boolean canPassThroughBlock(Location curBlock) {
        BlockVector3 blockPos = curBlock.toVector().toBlockPoint();
        BlockState block = curBlock.getExtent().getBlock(blockPos);
        return !block.getBlockType().getMaterial().isMovementBlocker();
    }

    private boolean advanceToWall(TargetBlock hitBlox) {
        Location curBlock;
        while ((curBlock = hitBlox.getCurrentBlock()) != null) {
            if (!this.canPassThroughBlock(curBlock)) {
                return true;
            }
            hitBlox.getNextBlock();
        }
        return false;
    }

    private boolean advanceToFree(TargetBlock hitBlox) {
        Location curBlock;
        while ((curBlock = hitBlox.getCurrentBlock()) != null) {
            if (this.canPassThroughBlock(curBlock)) {
                return true;
            }
            hitBlox.getNextBlock();
        }
        return false;
    }

    @Override
    public boolean passThroughForwardWall(int range) {
        TargetBlock hitBlox = new TargetBlock(this, range, 0.2);
        if (!this.advanceToWall(hitBlox)) {
            return false;
        }
        if (!this.advanceToFree(hitBlox)) {
            return false;
        }
        Location foundBlock = hitBlox.getCurrentBlock();
        if (foundBlock != null) {
            this.setOnGround(foundBlock);
            return true;
        }
        return false;
    }

    @Override
    public boolean trySetPosition(Vector3 pos) {
        Location location = this.getLocation();
        return this.trySetPosition(pos, location.getPitch(), location.getYaw());
    }

    @Override
    public File openFileOpenDialog(String[] extensions) {
        this.printError(TranslatableComponent.of("worldedit.platform.no-file-dialog"));
        return null;
    }

    @Override
    public File openFileSaveDialog(String[] extensions) {
        this.printError(TranslatableComponent.of("worldedit.platform.no-file-dialog"));
        return null;
    }

    @Override
    public boolean canDestroyBedrock() {
        return this.hasPermission("worldedit.override.bedrock");
    }

    @Override
    public void dispatchCUIEvent(CUIEvent event) {
    }

    public boolean equals(Object other) {
        if (!(other instanceof Player)) {
            return false;
        }
        Player other2 = (Player)other;
        return other2.getName().equals(this.getName());
    }

    public int hashCode() {
        return this.getName().hashCode();
    }

    @Override
    public void checkPermission(String permission) throws AuthorizationException {
        if (!this.hasPermission(permission)) {
            throw new AuthorizationException();
        }
    }

    @Override
    public boolean isPlayer() {
        return true;
    }

    @Override
    public GameMode getGameMode() {
        return GameModes.SURVIVAL;
    }

    @Override
    public void setGameMode(GameMode gameMode) {
    }

    public Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException("Not supported");
    }

    @Override
    public boolean remove() {
        return false;
    }

    @Override
    public <B extends BlockStateHolder<B>> void sendFakeBlock(BlockVector3 pos, B block) {
    }
}

