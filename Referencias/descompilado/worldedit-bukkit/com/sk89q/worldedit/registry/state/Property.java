/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.sk89q.worldedit.registry.state;

import java.util.List;
import javax.annotation.Nullable;

public interface Property<T> {
    public String getName();

    public List<T> getValues();

    @Nullable
    public T getValueFor(String var1) throws IllegalArgumentException;
}

