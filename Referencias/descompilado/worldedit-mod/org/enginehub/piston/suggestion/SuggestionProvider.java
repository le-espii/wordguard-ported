/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 */
package org.enginehub.piston.suggestion;

import com.google.common.collect.ImmutableSet;
import java.util.List;
import org.enginehub.piston.CommandParseResult;
import org.enginehub.piston.suggestion.Suggestion;

public interface SuggestionProvider {
    public ImmutableSet<Suggestion> provideSuggestions(List<String> var1, CommandParseResult var2);
}

