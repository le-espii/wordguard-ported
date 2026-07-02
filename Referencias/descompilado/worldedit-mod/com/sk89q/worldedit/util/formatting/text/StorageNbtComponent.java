/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.sk89q.worldedit.util.formatting.text;

import com.sk89q.worldedit.util.formatting.text.AbstractComponentBuilder;
import com.sk89q.worldedit.util.formatting.text.NbtComponent;
import com.sk89q.worldedit.util.formatting.text.NbtComponentBuilder;
import com.sk89q.worldedit.util.formatting.text.ScopedComponent;
import com.sk89q.worldedit.util.formatting.text.StorageNbtComponentImpl;
import com.sk89q.worldedit.util.kyori.Key;
import java.util.function.Consumer;
import org.checkerframework.checker.nullness.qual.NonNull;

public interface StorageNbtComponent
extends NbtComponent<StorageNbtComponent, Builder>,
ScopedComponent<StorageNbtComponent> {
    public static @NonNull Builder builder() {
        return new StorageNbtComponentImpl.BuilderImpl();
    }

    public static @NonNull StorageNbtComponent of(@NonNull String nbtPath, @NonNull Key storage) {
        return (StorageNbtComponent)((Builder)StorageNbtComponent.builder().nbtPath(nbtPath)).storage(storage).build();
    }

    public static @NonNull StorageNbtComponent make(@NonNull Consumer<? super Builder> consumer) {
        Builder builder = StorageNbtComponent.builder();
        return (StorageNbtComponent)AbstractComponentBuilder.configureAndBuild(builder, consumer);
    }

    public @NonNull Key storage();

    public @NonNull StorageNbtComponent storage(@NonNull Key var1);

    public static interface Builder
    extends NbtComponentBuilder<StorageNbtComponent, Builder> {
        public @NonNull Builder storage(@NonNull Key var1);
    }
}

