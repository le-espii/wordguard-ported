/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.reflect.TypeToken
 */
package com.sk89q.worldedit.command.argument;

import com.google.common.collect.ImmutableList;
import com.google.common.reflect.TypeToken;
import com.sk89q.worldedit.command.argument.CommaSeparatedValuesConverter;
import com.sk89q.worldedit.math.BlockVector2;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.math.Vector2;
import com.sk89q.worldedit.math.Vector3;
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

public class VectorConverter<C, T>
implements ArgumentConverter<T> {
    private static final CommaSeparatedValuesConverter<Integer> INT_CONVERTER = CommaSeparatedValuesConverter.wrap(ArgumentConverters.get(TypeToken.of(Integer.TYPE)));
    public static final VectorConverter<Integer, BlockVector3> BLOCK_VECTOR_3_CONVERTER = new VectorConverter<Integer, BlockVector3>(INT_CONVERTER, 3, cmps -> BlockVector3.at((Integer)cmps.get(0), (Integer)cmps.get(1), (Integer)cmps.get(2)), "block vector in the form x,y,z");
    private final ArgumentConverter<C> componentConverter;
    private final int componentCount;
    private final Function<List<C>, T> vectorConstructor;
    private final String acceptableArguments;

    public static void register(CommandManager commandManager) {
        CommaSeparatedValuesConverter doubleConverter = CommaSeparatedValuesConverter.wrap(ArgumentConverters.get(TypeToken.of(Double.TYPE)));
        commandManager.registerConverter(Key.of(BlockVector2.class), new VectorConverter<Integer, BlockVector2>(INT_CONVERTER, 2, cmps -> BlockVector2.at((Integer)cmps.get(0), (Integer)cmps.get(1)), "block vector in the form x,z"));
        commandManager.registerConverter(Key.of(Vector2.class), new VectorConverter(doubleConverter, 2, cmps -> Vector2.at((Double)cmps.get(0), (Double)cmps.get(1)), "vector in the form x,z"));
        commandManager.registerConverter(Key.of(BlockVector3.class), BLOCK_VECTOR_3_CONVERTER);
        commandManager.registerConverter(Key.of(Vector3.class), new VectorConverter(doubleConverter, 3, cmps -> Vector3.at((Double)cmps.get(0), (Double)cmps.get(1), (Double)cmps.get(2)), "vector in the form x,y,z"));
    }

    private VectorConverter(ArgumentConverter<C> componentConverter, int componentCount, Function<List<C>, T> vectorConstructor, String acceptableArguments) {
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
        if (components.get().size() != this.componentCount) {
            return FailedConversion.from(new IllegalArgumentException("Must have exactly " + this.componentCount + " vector components"));
        }
        T vector = this.vectorConstructor.apply((List<C>)ImmutableList.copyOf(components.get()));
        return SuccessfulConversion.fromSingle(vector);
    }
}

