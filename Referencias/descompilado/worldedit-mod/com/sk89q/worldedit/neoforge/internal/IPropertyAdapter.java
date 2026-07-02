/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ImmutableList
 *  net.minecraft.world.level.block.state.properties.Property
 */
package com.sk89q.worldedit.neoforge.internal;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.sk89q.worldedit.registry.state.Property;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

class IPropertyAdapter<T extends Comparable<T>>
implements Property<T> {
    private final net.minecraft.world.level.block.state.properties.Property<T> property;
    private final List<T> values;

    public IPropertyAdapter(net.minecraft.world.level.block.state.properties.Property<T> property) {
        this.property = property;
        this.values = ImmutableList.copyOf((Collection)property.getPossibleValues());
    }

    @Override
    public String getName() {
        return this.property.getName();
    }

    @Override
    public List<T> getValues() {
        return this.values;
    }

    @Override
    public T getValueFor(String string) throws IllegalArgumentException {
        Optional val = this.property.getValue(string);
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

