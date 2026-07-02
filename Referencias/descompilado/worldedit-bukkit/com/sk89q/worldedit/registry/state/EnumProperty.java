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

public class EnumProperty
extends AbstractProperty<String> {
    public EnumProperty(String name, List<String> values) {
        super(name, values);
    }

    @Override
    @Nullable
    public String getValueFor(String string) {
        if (!this.getValues().contains(string)) {
            throw new IllegalArgumentException("Invalid value: " + string + ". Must be in " + this.getValues().toString());
        }
        return string;
    }
}

