/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 *  javax.annotation.Nullable
 */
package com.sk89q.worldedit.command.argument;

import com.google.common.collect.ImmutableSet;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.command.util.HookMode;
import com.sk89q.worldedit.extent.TracingExtent;
import com.sk89q.worldedit.session.PlacementType;
import com.sk89q.worldedit.util.SideEffect;
import com.sk89q.worldedit.util.TreeGenerator;
import java.util.EnumSet;
import java.util.Locale;
import java.util.Set;
import java.util.function.Function;
import javax.annotation.Nullable;
import org.enginehub.piston.CommandManager;
import org.enginehub.piston.converter.ArgumentConverter;
import org.enginehub.piston.converter.MultiKeyConverter;
import org.enginehub.piston.inject.Key;

public final class EnumConverter {
    public static void register(CommandManager commandManager) {
        commandManager.registerConverter(Key.of(TreeGenerator.TreeType.class), EnumConverter.full(TreeGenerator.TreeType.class, t -> ImmutableSet.copyOf(t.lookupKeys), null));
        commandManager.registerConverter(Key.of(EditSession.ReorderMode.class), EnumConverter.full(EditSession.ReorderMode.class, r -> ImmutableSet.of((Object)r.getDisplayName()), null));
        commandManager.registerConverter(Key.of(SideEffect.State.class), MultiKeyConverter.from(EnumSet.of(SideEffect.State.OFF, SideEffect.State.ON), r -> ImmutableSet.of((Object)r.name().toLowerCase(Locale.US)), null));
        commandManager.registerConverter(Key.of(HookMode.class), EnumConverter.basic(HookMode.class));
        commandManager.registerConverter(Key.of(TracingExtent.Action.class), EnumConverter.basic(TracingExtent.Action.class));
        commandManager.registerConverter(Key.of(PlacementType.class), EnumConverter.basic(PlacementType.class));
    }

    private static <E extends Enum<E>> ArgumentConverter<E> basic(Class<E> enumClass) {
        return EnumConverter.full(enumClass, e -> ImmutableSet.of((Object)e.name().toLowerCase(Locale.ROOT)), null);
    }

    private static <E extends Enum<E>> ArgumentConverter<E> full(Class<E> enumClass, Function<E, Set<String>> lookupKeys, @Nullable E unknownValue) {
        return MultiKeyConverter.from(EnumSet.allOf(enumClass), lookupKeys, unknownValue);
    }

    private EnumConverter() {
    }
}

