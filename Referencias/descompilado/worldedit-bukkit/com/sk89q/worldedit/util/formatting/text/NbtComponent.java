/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.sk89q.worldedit.util.formatting.text;

import com.sk89q.worldedit.util.formatting.text.BuildableComponent;
import com.sk89q.worldedit.util.formatting.text.NbtComponentBuilder;
import org.checkerframework.checker.nullness.qual.NonNull;

public interface NbtComponent<C extends NbtComponent<C, B>, B extends NbtComponentBuilder<C, B>>
extends BuildableComponent<C, B> {
    public @NonNull String nbtPath();

    public @NonNull C nbtPath(@NonNull String var1);

    public boolean interpret();

    public @NonNull C interpret(boolean var1);
}

