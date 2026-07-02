/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.registry.state;

import com.sk89q.worldedit.registry.state.Property;
import java.util.List;

public abstract class AbstractProperty<T>
implements Property<T> {
    private final String name;
    private final List<T> values;

    public AbstractProperty(String name, List<T> values) {
        this.name = name;
        this.values = values;
    }

    @Override
    public List<T> getValues() {
        return this.values;
    }

    @Override
    public String getName() {
        return this.name;
    }

    public String toString() {
        return this.getClass().getSimpleName() + "{name=" + this.name + "}";
    }

    public int hashCode() {
        return this.name.hashCode();
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof Property)) {
            return false;
        }
        return this.getName().equals(((Property)obj).getName());
    }
}

