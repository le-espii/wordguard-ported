/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.sk89q.worldedit.util.formatting.text;

import com.sk89q.worldedit.util.formatting.text.ComponentBuilder;
import com.sk89q.worldedit.util.formatting.text.NbtComponent;
import org.checkerframework.checker.nullness.qual.NonNull;

public interface NbtComponentBuilder<C extends NbtComponent<C, B>, B extends NbtComponentBuilder<C, B>>
extends ComponentBuilder<C, B> {
    public @NonNull B nbtPath(@NonNull String var1);

    public @NonNull B interpret(boolean var1);
}

