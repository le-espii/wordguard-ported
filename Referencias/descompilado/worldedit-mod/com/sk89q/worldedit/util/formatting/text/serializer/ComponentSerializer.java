/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.sk89q.worldedit.util.formatting.text.serializer;

import com.sk89q.worldedit.util.formatting.text.Component;
import org.checkerframework.checker.nullness.qual.NonNull;

public interface ComponentSerializer<I extends Component, O extends Component, R> {
    public @NonNull O deserialize(@NonNull R var1);

    public @NonNull R serialize(@NonNull I var1);
}

