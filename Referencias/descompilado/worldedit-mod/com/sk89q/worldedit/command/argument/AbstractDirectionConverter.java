/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.auto.value.AutoAnnotation
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableSet
 *  javax.annotation.Nullable
 */
package com.sk89q.worldedit.command.argument;

import com.google.auto.value.AutoAnnotation;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.sk89q.worldedit.UnknownDirectionException;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.command.argument.AutoAnnotation_AbstractDirectionConverter_direction;
import com.sk89q.worldedit.command.argument.AutoAnnotation_AbstractDirectionConverter_multiDirection;
import com.sk89q.worldedit.command.argument.CommaSeparatedValuesConverter;
import com.sk89q.worldedit.entity.Player;
import com.sk89q.worldedit.internal.annotation.Direction;
import com.sk89q.worldedit.internal.annotation.MultiDirection;
import com.sk89q.worldedit.internal.annotation.OptionalArg;
import com.sk89q.worldedit.util.formatting.text.Component;
import com.sk89q.worldedit.util.formatting.text.TextComponent;
import java.lang.annotation.Annotation;
import java.util.List;
import javax.annotation.Nullable;
import org.enginehub.piston.CommandManager;
import org.enginehub.piston.converter.ArgumentConverter;
import org.enginehub.piston.converter.ConversionResult;
import org.enginehub.piston.converter.FailedConversion;
import org.enginehub.piston.converter.SuccessfulConversion;
import org.enginehub.piston.converter.SuggestionHelper;
import org.enginehub.piston.inject.InjectedValueAccess;
import org.enginehub.piston.inject.Key;

public abstract class AbstractDirectionConverter<D>
implements ArgumentConverter<D> {
    private static final ImmutableSet<String> ORTHOGONAL = ImmutableSet.of((Object)"north", (Object)"south", (Object)"east", (Object)"west", (Object)"up", (Object)"down", (Object[])new String[0]);
    private static final ImmutableSet<String> RELATIVE = ImmutableSet.of((Object)"me", (Object)"forward", (Object)"back", (Object)"left", (Object)"right");
    private static final ImmutableSet<String> DIAGONAL = ImmutableSet.of((Object)"northeast", (Object)"northwest", (Object)"southeast", (Object)"southwest");
    private final WorldEdit worldEdit;
    private final boolean includeDiagonals;
    private final ImmutableList<String> suggestions;

    @AutoAnnotation
    private static Direction direction(boolean includeDiagonals) {
        return new AutoAnnotation_AbstractDirectionConverter_direction(includeDiagonals);
    }

    @AutoAnnotation
    private static MultiDirection multiDirection(boolean includeDiagonals) {
        return new AutoAnnotation_AbstractDirectionConverter_multiDirection(includeDiagonals);
    }

    protected static <D> void register(CommandManager commandManager, AbstractDirectionConverter<D> converter, Class<D> keyClass, boolean includeDiagonals) {
        commandManager.registerConverter(Key.of(keyClass, (Annotation)AbstractDirectionConverter.direction(includeDiagonals)), converter);
        commandManager.registerConverter(Key.of(keyClass, (Annotation)AbstractDirectionConverter.multiDirection(includeDiagonals)), CommaSeparatedValuesConverter.wrap(converter));
    }

    protected AbstractDirectionConverter(WorldEdit worldEdit, boolean includeDiagonals) {
        this.worldEdit = worldEdit;
        this.includeDiagonals = includeDiagonals;
        this.suggestions = ImmutableList.builder().addAll(ORTHOGONAL).addAll(RELATIVE).addAll((Iterable)(includeDiagonals ? DIAGONAL : ImmutableList.of())).build();
    }

    @Override
    public ConversionResult<D> convert(String argument, InjectedValueAccess context) {
        Player player = context.injectedValue(Key.of(Player.class, OptionalArg.class)).orElse(null);
        try {
            return SuccessfulConversion.fromSingle(this.convertDirection(argument, player, this.includeDiagonals));
        }
        catch (Exception e) {
            return FailedConversion.from(e);
        }
    }

    protected abstract D convertDirection(String var1, @Nullable Player var2, boolean var3) throws UnknownDirectionException;

    @Override
    public Component describeAcceptableArguments() {
        return TextComponent.of("`me` to use facing direction, or any " + (this.includeDiagonals ? "direction" : "non-diagonal direction"));
    }

    @Override
    public List<String> getSuggestions(String input, InjectedValueAccess context) {
        return SuggestionHelper.limitByPrefix(this.suggestions.stream(), input);
    }

    protected WorldEdit getWorldEdit() {
        return this.worldEdit;
    }
}

