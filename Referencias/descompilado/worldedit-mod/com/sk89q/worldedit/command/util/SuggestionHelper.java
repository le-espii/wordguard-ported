/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.command.util;

import com.sk89q.worldedit.registry.Keyed;
import com.sk89q.worldedit.registry.NamespacedRegistry;
import com.sk89q.worldedit.registry.Registry;
import com.sk89q.worldedit.registry.state.Property;
import com.sk89q.worldedit.world.block.BlockCategory;
import com.sk89q.worldedit.world.block.BlockType;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class SuggestionHelper {
    private SuggestionHelper() {
    }

    public static Stream<String> getBlockCategorySuggestions(String tag, boolean allowRandom) {
        if (tag.isEmpty() || tag.equals("#")) {
            return allowRandom ? Stream.of("##", "##*") : Stream.of("##");
        }
        if (tag.startsWith("##")) {
            if (tag.equals("##")) {
                return Stream.concat(allowRandom ? Stream.of("##*") : Stream.empty(), SuggestionHelper.getNamespacedRegistrySuggestions(BlockCategory.REGISTRY, tag.substring(2)).map(s -> "##" + s));
            }
            if (tag.equals("##*") && allowRandom) {
                return SuggestionHelper.getNamespacedRegistrySuggestions(BlockCategory.REGISTRY, tag.substring(3)).map(s -> "##*" + s);
            }
            boolean wild = tag.startsWith("##*") && allowRandom;
            return SuggestionHelper.getNamespacedRegistrySuggestions(BlockCategory.REGISTRY, tag.substring(wild ? 3 : 2)).map(s -> (wild ? "##*" : "##") + s);
        }
        return Stream.empty();
    }

    public static Stream<String> getBlockPropertySuggestions(String blockTypeString, BlockType type, String props) {
        if (type == null) {
            return Stream.empty();
        }
        if (!props.toLowerCase(Locale.ROOT).equals(props)) {
            return Stream.empty();
        }
        Map<String, Property<?>> propertyMap = type.getPropertyMap();
        HashSet<String> matchedProperties = new HashSet<String>();
        String[] propParts = props.split(",", -1);
        for (int i = 0; i < propParts.length; ++i) {
            String[] propVal = propParts[i].split("=");
            String matchProp = propVal[0].toLowerCase(Locale.ROOT);
            if (i == propParts.length - 1) {
                String previous = Arrays.stream(propParts, 0, propParts.length - 1).collect(Collectors.joining(",")) + (propParts.length == 1 ? "" : ",");
                String lastValidInput = (blockTypeString + "[" + previous).toLowerCase(Locale.ROOT);
                if (propVal.length == 1) {
                    List matchingProps = propertyMap.entrySet().stream().filter(p -> !matchedProperties.contains(p.getKey()) && ((String)p.getKey()).startsWith(matchProp)).map(Map.Entry::getValue).toList();
                    return switch (matchingProps.size()) {
                        case 0 -> propertyMap.keySet().stream().filter(p -> !matchedProperties.contains(p)).map(prop -> lastValidInput + prop + "=");
                        case 1 -> ((Property)matchingProps.get(0)).getValues().stream().map(val -> lastValidInput + ((Property)matchingProps.get(0)).getName() + "=" + val.toString().toLowerCase(Locale.ROOT));
                        default -> matchingProps.stream().map(p -> lastValidInput + p.getName() + "=");
                    };
                }
                Property<?> prop2 = propertyMap.get(matchProp);
                if (prop2 == null) {
                    return propertyMap.keySet().stream().map(p -> lastValidInput + p);
                }
                List values = prop2.getValues().stream().map(v -> v.toString().toLowerCase(Locale.ROOT)).toList();
                String matchVal = propVal[1].toLowerCase(Locale.ROOT);
                List matchingVals = values.stream().filter(val -> val.startsWith(matchVal)).toList();
                if (matchingVals.isEmpty()) {
                    return values.stream().map(val -> lastValidInput + prop2.getName() + "=" + val);
                }
                if (matchingVals.size() == 1 && ((String)matchingVals.get(0)).equals(matchVal)) {
                    String currProp = lastValidInput + prop2.getName() + "=" + matchVal;
                    if (matchingVals.size() < values.size()) {
                        return Stream.of(currProp + "] ", currProp + ",");
                    }
                    return Stream.of(currProp + "] ");
                }
                return matchingVals.stream().map(val -> lastValidInput + prop2.getName() + "=" + val);
            }
            if (propVal.length != 2) {
                return Stream.empty();
            }
            Property<?> prop3 = propertyMap.get(matchProp);
            if (prop3 == null) {
                return Stream.empty();
            }
            try {
                prop3.getValueFor(propVal[1]);
                matchedProperties.add(prop3.getName());
                continue;
            }
            catch (IllegalArgumentException ignored) {
                return Stream.empty();
            }
        }
        return Stream.empty();
    }

    public static <V extends Keyed> Stream<String> getRegistrySuggestions(Registry<V> registry, String input) {
        if (registry instanceof NamespacedRegistry) {
            return SuggestionHelper.getNamespacedRegistrySuggestions((NamespacedRegistry)registry, input);
        }
        return org.enginehub.piston.converter.SuggestionHelper.limitByPrefix(registry.keySet().stream(), input).stream();
    }

    public static <V extends Keyed> Stream<String> getNamespacedRegistrySuggestions(NamespacedRegistry<V> registry, String input) {
        if (input.isEmpty() || input.equals(":")) {
            Set<String> namespaces = registry.getKnownNamespaces();
            if (namespaces.size() == 1) {
                int def = namespaces.iterator().next().length() + 1;
                return registry.keySet().stream().map(s -> s.substring(def));
            }
            return namespaces.stream().map(s -> s + ":");
        }
        if (input.startsWith(":")) {
            String term = input.substring(1).toLowerCase(Locale.ROOT);
            Predicate<String> search = org.enginehub.piston.converter.SuggestionHelper.byPrefix(term);
            return registry.keySet().stream().filter(s -> search.test(s.substring(s.indexOf(58) + 1)));
        }
        if (input.indexOf(58) < 0) {
            String lowerSearch = input.toLowerCase(Locale.ROOT);
            String defKey = registry.getDefaultNamespace() + ":" + lowerSearch;
            int defLength = registry.getDefaultNamespace().length() + 1;
            return Stream.concat(registry.keySet().stream().filter(s -> s.startsWith(defKey)).map(s -> s.substring(defLength)), registry.getKnownNamespaces().stream().filter(n -> n.startsWith(lowerSearch)).map(n -> n + ":"));
        }
        Predicate<String> search = org.enginehub.piston.converter.SuggestionHelper.byPrefix(input.toLowerCase(Locale.ROOT));
        return registry.keySet().stream().filter(search);
    }
}

