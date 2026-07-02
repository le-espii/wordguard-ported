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

public class BooleanProperty
extends AbstractProperty<Boolean> {
    public BooleanProperty(String name, List<Boolean> values) {
        super(name, values);
    }

    @Override
    @Nullable
    public Boolean getValueFor(String string) {
        boolean val = Boolean.parseBoolean(string);
        if (!this.getValues().contains(val)) {
            throw new IllegalArgumentException("Invalid boolean value: " + string + ". Must be in " + this.getValues().toString());
        }
        return val;
    }
}

