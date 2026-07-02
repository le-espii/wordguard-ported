/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.google.common.base.Splitter
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Iterables
 */
package com.sk89q.worldedit.command.argument;

import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.sk89q.worldedit.util.formatting.text.Component;
import com.sk89q.worldedit.util.formatting.text.TextComponent;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.enginehub.piston.converter.ArgumentConverter;
import org.enginehub.piston.converter.ConversionResult;
import org.enginehub.piston.converter.SuccessfulConversion;
import org.enginehub.piston.inject.InjectedValueAccess;

public class CommaSeparatedValuesConverter<T>
implements ArgumentConverter<T> {
    private static final Splitter COMMA = Splitter.on((char)',');
    private final ArgumentConverter<T> delegate;
    private final int maximum;
    private final boolean repeats;

    public static <T> CommaSeparatedValuesConverter<T> wrap(ArgumentConverter<T> delegate) {
        return CommaSeparatedValuesConverter.wrapAndLimit(delegate, -1);
    }

    public static <T> CommaSeparatedValuesConverter<T> wrapNoRepeats(ArgumentConverter<T> delegate) {
        return CommaSeparatedValuesConverter.wrapAndLimitNoRepeats(delegate, -1);
    }

    public static <T> CommaSeparatedValuesConverter<T> wrapAndLimit(ArgumentConverter<T> delegate, int maximum) {
        return new CommaSeparatedValuesConverter<T>(delegate, maximum, true);
    }

    public static <T> CommaSeparatedValuesConverter<T> wrapAndLimitNoRepeats(ArgumentConverter<T> delegate, int maximum) {
        return new CommaSeparatedValuesConverter<T>(delegate, maximum, false);
    }

    private CommaSeparatedValuesConverter(ArgumentConverter<T> delegate, int maximum, boolean repeats) {
        Preconditions.checkArgument((maximum == -1 || maximum > 1 ? 1 : 0) != 0, (Object)"Maximum must be bigger than 1, or exactly -1");
        this.delegate = delegate;
        this.maximum = maximum;
        this.repeats = repeats;
    }

    @Override
    public Component describeAcceptableArguments() {
        TextComponent.Builder result = TextComponent.builder("");
        if (this.maximum > -1) {
            ((TextComponent.Builder)((TextComponent.Builder)result.append((Component)TextComponent.of("up to "))).append((Component)TextComponent.of(this.maximum))).append((Component)TextComponent.space());
        }
        ((TextComponent.Builder)result.append((Component)TextComponent.of("comma separated values of: "))).append(this.delegate.describeAcceptableArguments());
        return result.build();
    }

    @Override
    public List<String> getSuggestions(String input, InjectedValueAccess context) {
        String lastInput = (String)Iterables.getLast((Iterable)COMMA.split((CharSequence)input), (Object)"");
        assert (lastInput != null);
        List<String> suggestions = this.delegate.getSuggestions(lastInput, context);
        if (input.contains(",")) {
            String prefix = input.substring(0, input.length() - lastInput.length());
            ImmutableSet entries = ImmutableSet.copyOf((Iterable)COMMA.split((CharSequence)input));
            suggestions = suggestions.stream().filter(arg_0 -> this.lambda$getSuggestions$0((Set)entries, arg_0)).map(suggestion -> prefix + suggestion).collect(Collectors.toList());
        }
        return suggestions;
    }

    @Override
    public ConversionResult<T> convert(String argument, InjectedValueAccess context) {
        ImmutableList.Builder result = ImmutableList.builder();
        for (String input : COMMA.split((CharSequence)argument)) {
            ConversionResult temp = this.delegate.convert(input, context);
            if (!temp.isSuccessful()) {
                return temp;
            }
            result.addAll(temp.get());
        }
        return SuccessfulConversion.from(result.build());
    }

    private /* synthetic */ boolean lambda$getSuggestions$0(Set entries, String suggestion) {
        return this.repeats || !entries.contains(suggestion);
    }
}

