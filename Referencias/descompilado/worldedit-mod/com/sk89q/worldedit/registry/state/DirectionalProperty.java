/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.sk89q.worldedit.registry.state;

import com.sk89q.worldedit.registry.state.AbstractProperty;
import com.sk89q.worldedit.util.Direction;
import java.util.List;
import java.util.Locale;
import javax.annotation.Nullable;

public class DirectionalProperty
extends AbstractProperty<Direction> {
    public DirectionalProperty(String name, List<Direction> values) {
        super(name, values);
    }

    @Override
    @Nullable
    public Direction getValueFor(String string) {
        Direction direction = Direction.valueOf(string.toUpperCase(Locale.ROOT));
        if (!this.getValues().contains((Object)direction)) {
            throw new IllegalArgumentException("Invalid direction value: " + string + ". Must be in " + this.getValues().toString());
        }
        return direction;
    }
}

