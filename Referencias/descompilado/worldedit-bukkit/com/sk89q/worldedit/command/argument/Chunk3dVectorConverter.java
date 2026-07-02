/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Range
 *  com.google.common.reflect.TypeToken
 */
package com.sk89q.worldedit.command.argument;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Range;
import com.google.common.reflect.TypeToken;
import com.sk89q.worldedit.command.argument.CommaSeparatedValuesConverter;
import com.sk89q.worldedit.internal.annotation.Chunk3d;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.util.formatting.text.Component;
import com.sk89q.worldedit.util.formatting.text.TextComponent;
import java.util.List;
import java.util.function.Function;
import org.enginehub.piston.CommandManager;
import org.enginehub.piston.converter.ArgumentConverter;
import org.enginehub.piston.converter.ArgumentConverters;
import org.enginehub.piston.converter.ConversionResult;
import org.enginehub.piston.converter.FailedConversion;
import org.enginehub.piston.converter.SuccessfulConversion;
import org.enginehub.piston.inject.InjectedValueAccess;
import org.enginehub.piston.inject.Key;

public class Chunk3dVectorConverter<C, T>
implements ArgumentConverter<T> {
    private final ArgumentConverter<C> componentConverter;
    private final Range<Integer> componentCount;
    private final Function<List<C>, T> vectorConstructor;
    private final String acceptableArguments;

    public static void register(CommandManager commandManager) {
        CommaSeparatedValuesConverter intConverter = CommaSeparatedValuesConverter.wrap(ArgumentConverters.get(TypeToken.of(Integer.TYPE)));
        commandManager.registerConverter(Key.of(BlockVector3.class, Chunk3d.class), new Chunk3dVectorConverter(intConverter, (Range<Integer>)Range.closed((Comparable)Integer.valueOf(2), (Comparable)Integer.valueOf(3)), cmps -> {
            switch (cmps.size()) {
                case 2: {
                    return BlockVector3.at((Integer)cmps.get(0), 0, (Integer)cmps.get(1));
                }
                case 3: {
                    return BlockVector3.at((Integer)cmps.get(0), (Integer)cmps.get(1), (Integer)cmps.get(2));
                }
            }
            throw new AssertionError((Object)"Expected 2 or 3 components");
        }, "block vector in the form x,z or x,y,z"));
    }

    private Chunk3dVectorConverter(ArgumentConverter<C> componentConverter, Range<Integer> componentCount, Function<List<C>, T> vectorConstructor, String acceptableArguments) {
        this.componentConverter = componentConverter;
        this.componentCount = componentCount;
        this.vectorConstructor = vectorConstructor;
        this.acceptableArguments = acceptableArguments;
    }

    @Override
    public Component describeAcceptableArguments() {
        return TextComponent.of("any " + this.acceptableArguments);
    }

    @Override
    public ConversionResult<T> convert(String argument, InjectedValueAccess context) {
        ConversionResult components = this.componentConverter.convert(argument, context);
        if (!components.isSuccessful()) {
            return components.failureAsAny();
        }
        if (!this.componentCount.contains((Comparable)Integer.valueOf(components.get().size()))) {
            return FailedConversion.from(new IllegalArgumentException("Must have " + String.valueOf(this.componentCount) + " vector components"));
        }
        T vector = this.vectorConstructor.apply((List<C>)ImmutableList.copyOf(components.get()));
        return SuccessfulConversion.fromSingle(vector);
    }
}

