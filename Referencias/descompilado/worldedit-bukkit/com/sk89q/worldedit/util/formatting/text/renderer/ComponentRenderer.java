/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.sk89q.worldedit.util.formatting.text.renderer;

import com.sk89q.worldedit.util.formatting.text.Component;
import org.checkerframework.checker.nullness.qual.NonNull;

public interface ComponentRenderer<C> {
    public @NonNull Component render(@NonNull Component var1, @NonNull C var2);
}

