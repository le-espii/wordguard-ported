/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.world.registry;

import com.sk89q.worldedit.registry.Category;
import com.sk89q.worldedit.registry.Keyed;
import java.util.Set;

public interface CategoryRegistry<T extends Keyed> {
    public Set<T> getCategorisedByName(String var1);

    default public Set<T> getAll(Category<T> category) {
        return this.getCategorisedByName(category.id());
    }
}

