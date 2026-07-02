/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldguard.internal.flywaydb.core.internal.util;

import com.sk89q.worldguard.internal.flywaydb.core.api.FlywayException;
import com.sk89q.worldguard.internal.flywaydb.core.internal.util.StringUtils;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PlaceholderReplacer {
    public static final PlaceholderReplacer NO_PLACEHOLDERS = new PlaceholderReplacer(new HashMap<String, String>(), "", "");
    private final Map<String, String> placeholders;
    private final String placeholderPrefix;
    private final String placeholderSuffix;

    public PlaceholderReplacer(Map<String, String> placeholders, String placeholderPrefix, String placeholderSuffix) {
        this.placeholders = placeholders;
        this.placeholderPrefix = placeholderPrefix;
        this.placeholderSuffix = placeholderSuffix;
    }

    public String replacePlaceholders(String input) {
        String noPlaceholders = input;
        for (String placeholder : this.placeholders.keySet()) {
            String searchTerm = this.placeholderPrefix + placeholder + this.placeholderSuffix;
            String value = this.placeholders.get(placeholder);
            noPlaceholders = StringUtils.replaceAll(noPlaceholders, searchTerm, value == null ? "" : value);
        }
        this.checkForUnmatchedPlaceholderExpression(noPlaceholders);
        return noPlaceholders;
    }

    private void checkForUnmatchedPlaceholderExpression(String input) {
        String regex = Pattern.quote(this.placeholderPrefix) + "(.+?)" + Pattern.quote(this.placeholderSuffix);
        Matcher matcher = Pattern.compile(regex).matcher(input);
        TreeSet<String> unmatchedPlaceHolderExpressions = new TreeSet<String>();
        while (matcher.find()) {
            unmatchedPlaceHolderExpressions.add(matcher.group());
        }
        if (!unmatchedPlaceHolderExpressions.isEmpty()) {
            throw new FlywayException("No value provided for placeholder expressions: " + StringUtils.collectionToCommaDelimitedString(unmatchedPlaceHolderExpressions) + ".  Check your configuration!");
        }
    }
}

