/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ImmutableList
 *  net.minecraft.class_2769
 */
package com.sk89q.worldedit.fabric.internal;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.sk89q.worldedit.registry.state.Property;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import net.minecraft.class_2769;

class PropertyAdapter<T extends Comparable<T>>
implements Property<T> {
    private final class_2769<T> property;
    private final List<T> values;

    public PropertyAdapter(class_2769<T> property) {
        this.property = property;
        this.values = ImmutableList.copyOf((Collection)property.method_11898());
    }

    @Override
    public String getName() {
        return this.property.method_11899();
    }

    @Override
    public List<T> getValues() {
        return this.values;
    }

    @Override
    public T getValueFor(String string) throws IllegalArgumentException {
        Optional val = this.property.method_11900(string);
        Preconditions.checkArgument((boolean)val.isPresent(), (String)"%s has no value for %s", (Object)this.getName(), (Object)string);
        return (T)((Comparable)val.get());
    }

    public int hashCode() {
        return this.getName().hashCode();
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof Property)) {
            return false;
        }
        return this.getName().equals(((Property)obj).getName());
    }
}

