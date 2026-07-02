/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.sk89q.worldedit.util.formatting.text;

import com.sk89q.worldedit.util.formatting.text.AbstractComponentBuilder;
import com.sk89q.worldedit.util.formatting.text.EntityNbtComponentImpl;
import com.sk89q.worldedit.util.formatting.text.NbtComponent;
import com.sk89q.worldedit.util.formatting.text.NbtComponentBuilder;
import com.sk89q.worldedit.util.formatting.text.ScopedComponent;
import java.util.function.Consumer;
import org.checkerframework.checker.nullness.qual.NonNull;

public interface EntityNbtComponent
extends NbtComponent<EntityNbtComponent, Builder>,
ScopedComponent<EntityNbtComponent> {
    public static @NonNull Builder builder() {
        return new EntityNbtComponentImpl.BuilderImpl();
    }

    public static @NonNull EntityNbtComponent of(@NonNull String nbtPath, @NonNull String selector) {
        return (EntityNbtComponent)((Builder)EntityNbtComponent.builder().nbtPath(nbtPath)).selector(selector).build();
    }

    public static @NonNull EntityNbtComponent make(@NonNull Consumer<? super Builder> consumer) {
        Builder builder = EntityNbtComponent.builder();
        return (EntityNbtComponent)AbstractComponentBuilder.configureAndBuild(builder, consumer);
    }

    public @NonNull String selector();

    public @NonNull EntityNbtComponent selector(@NonNull String var1);

    public static interface Builder
    extends NbtComponentBuilder<EntityNbtComponent, Builder> {
        public @NonNull Builder selector(@NonNull String var1);
    }
}

