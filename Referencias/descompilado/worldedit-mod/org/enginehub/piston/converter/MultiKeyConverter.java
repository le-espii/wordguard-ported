/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.auto.value.AutoValue
 *  com.google.auto.value.AutoValue$Builder
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.ImmutableSet$Builder
 *  com.google.common.collect.ImmutableSetMultimap
 *  com.google.common.collect.ImmutableSetMultimap$Builder
 *  com.google.common.collect.ImmutableSortedMap
 *  com.google.common.collect.ImmutableSortedMap$Builder
 *  com.google.common.collect.Multimaps
 *  com.google.common.collect.SetMultimap
 *  javax.annotation.Nullable
 */
package org.enginehub.piston.converter;

import com.google.auto.value.AutoValue;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.ImmutableSortedMap;
import com.google.common.collect.Multimaps;
import com.google.common.collect.SetMultimap;
import com.sk89q.worldedit.util.formatting.text.Component;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import javax.annotation.Nullable;
import org.enginehub.piston.config.ColorConfig;
import org.enginehub.piston.converter.ArgumentConverter;
import org.enginehub.piston.converter.AutoValue_MultiKeyConverter_Arguments;
import org.enginehub.piston.converter.ConversionResult;
import org.enginehub.piston.converter.FailedConversion;
import org.enginehub.piston.converter.SuccessfulConversion;
import org.enginehub.piston.converter.SuggestionHelper;
import org.enginehub.piston.inject.InjectedValueAccess;
import org.enginehub.piston.util.ComponentHelper;

public class MultiKeyConverter<E>
implements ArgumentConverter<E> {
    private final Component choices;
    private final ImmutableSet<String> primaryKeys;
    private final ImmutableMap<String, E> map;
    @Nullable
    private final E unknownValue;
    private final UnaryOperator<String> errorMessage;

    public static <E> Builder<E> builder(SetMultimap<E, String> items) {
        return new AutoValue_MultiKeyConverter_Arguments.Builder().errorMessage(arg -> "Not a valid argument: " + arg).items((SetMultimap)items);
    }

    public static <E> Builder<E> builder(Collection<E> items, Function<E, Set<String>> lookupKeys) {
        ImmutableSetMultimap.Builder map = ImmutableSetMultimap.builder();
        for (E item : items) {
            map.putAll(item, (Iterable)lookupKeys.apply(item));
        }
        return MultiKeyConverter.builder(map.build());
    }

    public static <E> MultiKeyConverter<E> from(SetMultimap<E, String> items) {
        return MultiKeyConverter.from(items, null);
    }

    public static <E> MultiKeyConverter<E> from(Collection<E> items, Function<E, Set<String>> lookupKeys) {
        return MultiKeyConverter.from(items, lookupKeys, null);
    }

    public static <E> MultiKeyConverter<E> from(SetMultimap<E, String> items, @Nullable E unknownValue) {
        return MultiKeyConverter.builder(items).unknownValue(unknownValue).build();
    }

    public static <E> MultiKeyConverter<E> from(Collection<E> items, Function<E, Set<String>> lookupKeys, @Nullable E unknownValue) {
        return MultiKeyConverter.builder(items, lookupKeys).unknownValue(unknownValue).build();
    }

    private MultiKeyConverter(Arguments<E> arguments) {
        ImmutableSortedMap.Builder map = ImmutableSortedMap.orderedBy((Comparator)String.CASE_INSENSITIVE_ORDER);
        ImmutableSet.Builder primaryKeysBuilder = ImmutableSet.builder();
        Multimaps.asMap(arguments.items()).forEach((item, keys) -> {
            Preconditions.checkState((keys.size() > 0 ? 1 : 0) != 0, (String)"No lookup keys for value %s", (Object)item);
            primaryKeysBuilder.add((Object)((String)keys.iterator().next()));
            for (String key : keys) {
                map.put((Object)key, item);
            }
        });
        this.primaryKeys = primaryKeysBuilder.build();
        this.choices = this.primaryKeys.stream().map(ColorConfig.mainText()::wrap).collect(ComponentHelper.joiningWithBar());
        this.map = map.build();
        this.unknownValue = arguments.unknownValue();
        this.errorMessage = arguments.errorMessage();
    }

    @Override
    public Component describeAcceptableArguments() {
        return this.choices;
    }

    @Override
    public List<String> getSuggestions(String input, InjectedValueAccess context) {
        return SuggestionHelper.limitByPrefix(this.primaryKeys.stream(), input);
    }

    @Override
    public ConversionResult<E> convert(String argument, InjectedValueAccess context) {
        Object result = this.map.get((Object)argument);
        if (result == null) {
            if (this.unknownValue != null) {
                return SuccessfulConversion.fromSingle(this.unknownValue, false);
            }
            return FailedConversion.from(new IllegalArgumentException((String)this.errorMessage.apply(argument)));
        }
        return SuccessfulConversion.fromSingle(result);
    }

    @AutoValue
    static abstract class Arguments<E> {
        Arguments() {
        }

        abstract ImmutableSetMultimap<E, String> items();

        @Nullable
        abstract E unknownValue();

        abstract UnaryOperator<String> errorMessage();

        @AutoValue.Builder
        static interface Builder<E>
        extends org.enginehub.piston.converter.MultiKeyConverter$Builder<E> {
            @Override
            public Builder<E> items(SetMultimap<E, String> var1);

            @Override
            public Builder<E> unknownValue(@Nullable E var1);

            @Override
            public Builder<E> errorMessage(UnaryOperator<String> var1);

            public Arguments<E> autoBuild();

            @Override
            default public MultiKeyConverter<E> build() {
                return new MultiKeyConverter<E>(this.autoBuild());
            }
        }
    }

    public static interface Builder<E> {
        public Builder<E> items(SetMultimap<E, String> var1);

        public Builder<E> unknownValue(@Nullable E var1);

        public Builder<E> errorMessage(UnaryOperator<String> var1);

        public MultiKeyConverter<E> build();
    }
}

