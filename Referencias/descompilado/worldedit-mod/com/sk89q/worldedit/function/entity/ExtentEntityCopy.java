/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  java.lang.MatchException
 */
package com.sk89q.worldedit.function.entity;

import com.google.common.base.Preconditions;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.entity.BaseEntity;
import com.sk89q.worldedit.entity.Entity;
import com.sk89q.worldedit.extent.Extent;
import com.sk89q.worldedit.function.EntityFunction;
import com.sk89q.worldedit.internal.helper.MCDirections;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.math.Vector3;
import com.sk89q.worldedit.math.transform.Transform;
import com.sk89q.worldedit.util.Direction;
import com.sk89q.worldedit.util.Location;
import com.sk89q.worldedit.util.concurrency.LazyReference;
import com.sk89q.worldedit.world.entity.EntityTypes;
import org.enginehub.linbus.tree.LinCompoundTag;
import org.enginehub.linbus.tree.LinNumberTag;
import org.enginehub.linbus.tree.LinTag;
import org.enginehub.linbus.tree.LinTagType;

public class ExtentEntityCopy
implements EntityFunction {
    private final Extent destination;
    private final Vector3 from;
    private final Vector3 to;
    private final Transform transform;
    private boolean removing;

    public ExtentEntityCopy(Vector3 from, Extent destination, Vector3 to, Transform transform) {
        Preconditions.checkNotNull((Object)((Object)from));
        Preconditions.checkNotNull((Object)destination);
        Preconditions.checkNotNull((Object)((Object)to));
        Preconditions.checkNotNull((Object)transform);
        this.destination = destination;
        this.from = from;
        this.to = to;
        this.transform = transform;
    }

    public boolean isRemoving() {
        return this.removing;
    }

    public void setRemoving(boolean removing) {
        this.removing = removing;
    }

    @Override
    public boolean apply(Entity entity) throws WorldEditException {
        BaseEntity state = entity.getState();
        if (state != null) {
            boolean success;
            Object v;
            Location location = entity.getLocation();
            LinCompoundTag tag = state.getNbt();
            boolean hasTilePosition = false;
            if (tag != null && (v = tag.value().get("TileX")) instanceof LinNumberTag) {
                LinNumberTag tagX = (LinNumberTag)v;
                v = tag.value().get("TileY");
                if (v instanceof LinNumberTag) {
                    LinNumberTag tagY = (LinNumberTag)v;
                    v = tag.value().get("TileZ");
                    if (v instanceof LinNumberTag) {
                        LinNumberTag tagZ = (LinNumberTag)v;
                        location = location.setPosition(Vector3.at(((Number)tagX.value()).intValue(), ((Number)tagY.value()).intValue(), ((Number)tagZ.value()).intValue()).add(0.5, 0.5, 0.5));
                        hasTilePosition = true;
                    }
                }
            }
            Vector3 pivot = this.from.round().add(0.5, 0.5, 0.5);
            Vector3 newPosition = this.transform.apply(location.toVector().subtract(pivot));
            if (hasTilePosition) {
                newPosition = newPosition.subtract(0.5, 0.5, 0.5);
            }
            Vector3 newDirection = this.transform.isIdentity() ? entity.getLocation().getDirection() : this.transform.apply(location.getDirection()).subtract(this.transform.apply(Vector3.ZERO)).normalize();
            Location newLocation = new Location(this.destination, newPosition.add(this.to.round().add(0.5, 0.5, 0.5)), newDirection);
            state = this.transformNbtData(state);
            boolean bl = success = this.destination.createEntity(newLocation, state) != null;
            if (this.isRemoving() && success) {
                entity.remove();
            }
            return success;
        }
        return false;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    private BaseEntity transformNbtData(BaseEntity state) {
        Vector3 vector;
        Direction newDirection;
        Direction direction;
        String facingKey;
        Object object;
        Object tilePosition;
        LinNumberTag tagY;
        LinNumberTag tagX;
        Object v;
        LinCompoundTag tag = state.getNbt();
        if (tag == null) return state;
        LinCompoundTag leashCompound = tag.findTag("Leash", LinTagType.compoundTag());
        if (leashCompound != null && (v = tag.value().get("X")) instanceof LinNumberTag) {
            tagX = (LinNumberTag)v;
            v = tag.value().get("Y");
            if (v instanceof LinNumberTag) {
                tagY = (LinNumberTag)v;
                v = tag.value().get("Z");
                if (v instanceof LinNumberTag) {
                    LinNumberTag tagZ = (LinNumberTag)v;
                    tilePosition = Vector3.at(((Number)tagX.value()).intValue(), ((Number)tagY.value()).intValue(), ((Number)tagZ.value()).intValue());
                    BlockVector3 newLeash = this.transform.apply(((Vector3)((Object)tilePosition)).subtract(this.from)).add(this.to).toBlockPoint();
                    return new BaseEntity(state.getType(), LazyReference.computed(tag.toBuilder().put("Leash", leashCompound.toBuilder().putInt("X", newLeash.x()).putInt("Y", newLeash.y()).putInt("Z", newLeash.z()).build()).build()));
                }
            }
        }
        if (!((tilePosition = tag.value().get("TileX")) instanceof LinNumberTag)) return state;
        tagX = (LinNumberTag)tilePosition;
        tilePosition = tag.value().get("TileY");
        if (!(tilePosition instanceof LinNumberTag)) return state;
        tagY = (LinNumberTag)tilePosition;
        tilePosition = tag.value().get("TileZ");
        if (!(tilePosition instanceof LinNumberTag)) return state;
        LinNumberTag tagZ = (LinNumberTag)tilePosition;
        tilePosition = Vector3.at(((Number)tagX.value()).intValue(), ((Number)tagY.value()).intValue(), ((Number)tagZ.value()).intValue());
        BlockVector3 newTilePosition = this.transform.apply(((Vector3)((Object)tilePosition)).subtract(this.from)).add(this.to).toBlockPoint();
        LinCompoundTag.Builder builder = tag.toBuilder().putInt("TileX", newTilePosition.x()).putInt("TileY", newTilePosition.y()).putInt("TileZ", newTilePosition.z());
        FacingTagData facingTagData = ExtentEntityCopy.tryGetFacingData(tag);
        if (!(facingTagData instanceof FacingTagData)) return new BaseEntity(state.getType(), LazyReference.computed(builder.build()));
        FacingTagData facingTagData2 = facingTagData;
        try {
            object = facingTagData2.facingKey();
            facingKey = object;
        }
        catch (Throwable throwable) {
            throw new MatchException(throwable.toString(), throwable);
        }
        Object tagFacing = object = facingTagData2.tagFacing();
        boolean isPainting = state.getType() == EntityTypes.PAINTING;
        Direction direction2 = direction = isPainting ? MCDirections.fromHorizontalHanging(((Number)((LinTag)tagFacing).value()).intValue()) : MCDirections.fromHanging(((Number)((LinTag)tagFacing).value()).intValue());
        if (direction == null || (newDirection = Direction.findClosest(vector = this.transform.apply(direction.toVector()).subtract(this.transform.apply(Vector3.ZERO)).normalize(), Direction.Flag.CARDINAL)) == null) return new BaseEntity(state.getType(), LazyReference.computed(builder.build()));
        byte facingValue = (byte)(isPainting ? MCDirections.toHorizontalHanging(newDirection) : MCDirections.toHanging(newDirection));
        builder.putByte(facingKey, facingValue);
        return new BaseEntity(state.getType(), LazyReference.computed(builder.build()));
    }

    private static FacingTagData tryGetFacingData(LinCompoundTag tag) {
        Object v = tag.value().get("Facing");
        if (v instanceof LinNumberTag) {
            LinNumberTag tagFacingCapital = (LinNumberTag)v;
            return new FacingTagData("Facing", tagFacingCapital);
        }
        v = tag.value().get("facing");
        if (v instanceof LinNumberTag) {
            LinNumberTag tagFacingLower = (LinNumberTag)v;
            return new FacingTagData("facing", tagFacingLower);
        }
        return null;
    }

    private record FacingTagData(String facingKey, LinNumberTag<?> tagFacing) {
    }
}

