/*
 * Decompiled with CFR 0.152.
 */
package org.enginehub.piston.converter;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SuggestionHelper {
    public static List<String> limitByPrefix(Stream<String> choices, String input) {
        return choices.filter(SuggestionHelper.byPrefix(input)).collect(Collectors.toList());
    }

    public static Predicate<String> byPrefix(String input) {
        return s -> s.length() > input.length() && SuggestionHelper.startsWithIgnoreCase(s, input);
    }

    private static boolean startsWithIgnoreCase(String whole, String prefix) {
        return whole.regionMatches(true, 0, prefix, 0, prefix.length());
    }

    private SuggestionHelper() {
    }
}

