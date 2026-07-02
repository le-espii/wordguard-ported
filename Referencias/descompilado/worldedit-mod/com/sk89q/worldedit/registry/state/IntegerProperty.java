/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.sk89q.worldedit.registry.state;

import com.sk89q.worldedit.registry.state.AbstractProperty;
import java.util.List;
import javax.annotation.Nullable;

public class IntegerProperty
extends AbstractProperty<Integer> {
    public IntegerProperty(String name, List<Integer> values) {
        super(name, values);
    }

    @Override
    @Nullable
    public Integer getValueFor(String string) {
        try {
            int val = Integer.parseInt(string);
            if (!this.getValues().contains(val)) {
                throw new IllegalArgumentException("Invalid int value: " + string + ". Must be in " + this.getValues().toString());
            }
            return val;
        }
        catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid int value: " + string + ". Not an int.");
        }
    }
}

