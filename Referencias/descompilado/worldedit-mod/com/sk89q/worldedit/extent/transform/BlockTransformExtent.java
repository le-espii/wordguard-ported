/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.Sets
 *  javax.annotation.Nullable
 */
package com.sk89q.worldedit.extent.transform;

import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.extent.AbstractDelegateExtent;
import com.sk89q.worldedit.extent.Extent;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.math.Vector3;
import com.sk89q.worldedit.math.transform.AffineTransform;
import com.sk89q.worldedit.math.transform.Transform;
import com.sk89q.worldedit.registry.state.BooleanProperty;
import com.sk89q.worldedit.registry.state.DirectionalProperty;
import com.sk89q.worldedit.registry.state.EnumProperty;
import com.sk89q.worldedit.registry.state.IntegerProperty;
import com.sk89q.worldedit.registry.state.Property;
import com.sk89q.worldedit.util.Direction;
import com.sk89q.worldedit.world.block.BaseBlock;
import com.sk89q.worldedit.world.block.BlockState;
import com.sk89q.worldedit.world.block.BlockStateHolder;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Set;
import javax.annotation.Nullable;

public class BlockTransformExtent
extends AbstractDelegateExtent {
    private final Transform transform;
    private static final Set<String> directionNames = Sets.newHashSet((Object[])new String[]{"north", "south", "east", "west"});

    public BlockTransformExtent(Extent extent, Transform transform) {
        super(extent);
        Preconditions.checkNotNull((Object)transform);
        this.transform = transform;
    }

    public Transform getTransform() {
        return this.transform;
    }

    private <T extends BlockStateHolder<T>> T transformBlock(T block, boolean reverse) {
        return BlockTransformExtent.transform(block, reverse ? this.transform.inverse() : this.transform);
    }

    @Override
    public BlockState getBlock(BlockVector3 position) {
        return this.transformBlock(super.getBlock(position), false);
    }

    @Override
    public BaseBlock getFullBlock(BlockVector3 position) {
        return this.transformBlock(super.getFullBlock(position), false);
    }

    public <B extends BlockStateHolder<B>> boolean setBlock(BlockVector3 location, B block) throws WorldEditException {
        return super.setBlock(location, this.transformBlock(block, true));
    }

    public static <B extends BlockStateHolder<B>> B transform(B block, Transform transform) {
        Preconditions.checkNotNull(block);
        Preconditions.checkNotNull((Object)transform);
        if (transform.isIdentity()) {
            return block;
        }
        B result = block;
        List<Property<?>> properties = block.getBlockType().getProperties();
        for (Property<?> property : properties) {
            OptionalInt newRotation;
            Vector3 vec;
            if (property instanceof DirectionalProperty) {
                Vector3 newValue;
                DirectionalProperty dirProp = (DirectionalProperty)property;
                Direction value = (Direction)((Object)result.getState(property));
                if (value == null || (newValue = BlockTransformExtent.getNewStateValue(dirProp.getValues(), transform, value.toVector())) == null) continue;
                result = result.with(dirProp, (Direction)Direction.findClosest(newValue, Direction.Flag.ALL));
                continue;
            }
            if (property instanceof EnumProperty) {
                Object newValue;
                Object value;
                EnumProperty enumProp = (EnumProperty)property;
                if (property.getName().equals("axis")) {
                    String axis;
                    if ((value = (switch ((String)result.getState(property)) {
                        case "x" -> Direction.EAST;
                        case "y" -> Direction.UP;
                        case "z" -> Direction.NORTH;
                        default -> null;
                    })) == null || (newValue = BlockTransformExtent.getNewStateValue(Direction.valuesOf(Direction.Flag.UPRIGHT | Direction.Flag.CARDINAL), transform, ((Direction)((Object)value)).toVector())) == null) continue;
                    Direction newDir = Direction.findClosest(newValue, Direction.Flag.UPRIGHT | Direction.Flag.CARDINAL);
                    if ((axis = (switch (newDir) {
                        case Direction.NORTH, Direction.SOUTH -> "z";
                        case Direction.EAST, Direction.WEST -> "x";
                        case Direction.UP, Direction.DOWN -> "y";
                        default -> null;
                    })) == null) continue;
                    result = result.with(enumProp, (String)axis);
                    continue;
                }
                if (property.getName().equals("type") && transform instanceof AffineTransform) {
                    AffineTransform affineTransform = (AffineTransform)transform;
                    if (affineTransform.isHorizontalFlip()) {
                        switch (value = (String)result.getState(property)) {
                            case "left": {
                                Object object = "right";
                                break;
                            }
                            case "right": {
                                Object object = "left";
                                break;
                            }
                            default: {
                                Object object = newValue = null;
                            }
                        }
                        if (newValue != null && enumProp.getValues().contains(newValue)) {
                            result = result.with(enumProp, (Vector3)((Object)newValue));
                        }
                    }
                    if (!affineTransform.isVerticalFlip()) continue;
                    if ((newValue = (switch (value = (String)result.getState(property)) {
                        case "bottom" -> "top";
                        case "top" -> "bottom";
                        default -> null;
                    })) == null || !enumProp.getValues().contains(newValue)) continue;
                    result = result.with(enumProp, (Vector3)((Object)newValue));
                    continue;
                }
                if (property.getName().equals("half") && transform instanceof AffineTransform) {
                    AffineTransform affineTransform = (AffineTransform)transform;
                    if (!affineTransform.isVerticalFlip()) continue;
                    if ((newValue = (switch (value = (String)result.getState(property)) {
                        case "bottom" -> "top";
                        case "top" -> "bottom";
                        default -> null;
                    })) == null || !enumProp.getValues().contains(newValue)) continue;
                    result = result.with(enumProp, (Vector3)((Object)newValue));
                    continue;
                }
                if (property.getName().equals("shape") && transform instanceof AffineTransform) {
                    AffineTransform affineTransform = (AffineTransform)transform;
                    if (!affineTransform.isHorizontalFlip()) continue;
                    if ((newValue = (switch (value = (String)result.getState(property)) {
                        case "outer_left" -> "outer_right";
                        case "outer_right" -> "outer_left";
                        case "inner_left" -> "inner_right";
                        case "inner_right" -> "inner_left";
                        default -> null;
                    })) == null || !enumProp.getValues().contains(newValue)) continue;
                    result = result.with(enumProp, (Vector3)((Object)newValue));
                    continue;
                }
                if (!property.getName().equals("orientation") || !(transform instanceof AffineTransform)) continue;
                AffineTransform affineTransform = (AffineTransform)transform;
                if (affineTransform.isHorizontalFlip()) {
                    switch (value = (String)result.getState(property)) {
                        case "north_up": {
                            Object object = "south_up";
                            break;
                        }
                        case "south_up": {
                            Object object = "north_up";
                            break;
                        }
                        case "east_up": {
                            Object object = "west_up";
                            break;
                        }
                        case "west_up": {
                            Object object = "east_up";
                            break;
                        }
                        default: {
                            Object object = newValue = null;
                        }
                    }
                    if (newValue != null && enumProp.getValues().contains(newValue)) {
                        result = result.with(enumProp, (Vector3)((Object)newValue));
                    }
                }
                if (!affineTransform.isVerticalFlip()) continue;
                if ((newValue = (switch (value = (String)result.getState(property)) {
                    case "down_east" -> "up_east";
                    case "down_north" -> "up_north";
                    case "down_south" -> "up_south";
                    case "down_west" -> "up_west";
                    case "up_east" -> "down_east";
                    case "up_north" -> "down_north";
                    case "up_south" -> "down_south";
                    case "up_west" -> "down_west";
                    default -> null;
                })) == null || !enumProp.getValues().contains(newValue)) continue;
                result = result.with(enumProp, (Vector3)((Object)newValue));
                continue;
            }
            if (!(property instanceof IntegerProperty)) continue;
            IntegerProperty intProp = (IntegerProperty)property;
            if (!property.getName().equals("rotation") || intProp.getValues().size() != 16) continue;
            Optional<Direction> direction = Direction.fromRotationIndex(result.getState(intProp));
            int horizontalFlags = Direction.Flag.CARDINAL | Direction.Flag.ORDINAL | Direction.Flag.SECONDARY_ORDINAL;
            if (!direction.isPresent() || (vec = BlockTransformExtent.getNewStateValue(Direction.valuesOf(horizontalFlags), transform, direction.get().toVector())) == null || !(newRotation = Direction.findClosest(vec, horizontalFlags).toRotationIndex()).isPresent()) continue;
            result = result.with(intProp, newRotation.getAsInt());
        }
        HashMap<String, Boolean> directionalProperties = new HashMap<String, Boolean>();
        for (Property<?> prop : properties) {
            String origProp;
            Direction dir;
            Direction closest;
            if (!directionNames.contains(prop.getName())) continue;
            Object state = result.getState(prop);
            if ((!(prop instanceof BooleanProperty) || !((Boolean)state).booleanValue()) && (!(prop instanceof EnumProperty) || state.toString().equals("none")) || (closest = Direction.findClosest(transform.apply((dir = Direction.valueOf(origProp = prop.getName().toUpperCase(Locale.ROOT))).toVector()), Direction.Flag.CARDINAL)) == null) continue;
            String closestProp = closest.name().toLowerCase(Locale.ROOT);
            if (prop instanceof BooleanProperty) {
                result = result.with((BooleanProperty)prop, (Boolean)Boolean.FALSE);
                directionalProperties.put(closestProp, Boolean.TRUE);
                continue;
            }
            if (prop.getValues().contains("none")) {
                Property<?> propAsObj = prop;
                result = result.with(propAsObj, (String)"none");
            }
            directionalProperties.put(closestProp, (Boolean)state);
        }
        if (!directionalProperties.isEmpty()) {
            for (String directionName : directionNames) {
                Property dirProp = block.getBlockType().getProperty(directionName);
                result = result.with(dirProp, directionalProperties.get(directionName));
            }
        }
        return result;
    }

    @Nullable
    private static Vector3 getNewStateValue(List<Direction> allowedStates, Transform transform, Vector3 oldDirection) {
        Vector3 newDirection = transform.apply(oldDirection).subtract(transform.apply(Vector3.ZERO)).normalize();
        Vector3 newValue = null;
        double closest = -2.0;
        boolean found = false;
        for (Direction v : allowedStates) {
            double dot = v.toVector().normalize().dot(newDirection);
            if (!(dot >= closest)) continue;
            closest = dot;
            newValue = v.toVector();
            found = true;
        }
        if (found) {
            return newValue;
        }
        return null;
    }
}

