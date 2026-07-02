/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 */
package com.sk89q.worldedit.event.platform;

import com.google.common.base.Preconditions;
import com.sk89q.worldedit.event.Event;
import com.sk89q.worldedit.extension.platform.Actor;
import com.sk89q.worldedit.internal.util.Substring;
import java.util.Collections;
import java.util.List;

public class CommandSuggestionEvent
extends Event {
    private final Actor actor;
    private final String arguments;
    private List<Substring> suggestions = Collections.emptyList();

    public CommandSuggestionEvent(Actor actor, String arguments) {
        Preconditions.checkNotNull((Object)actor);
        Preconditions.checkNotNull((Object)arguments);
        this.actor = actor;
        this.arguments = arguments;
    }

    public Actor getActor() {
        return this.actor;
    }

    public String getArguments() {
        return this.arguments;
    }

    public List<Substring> getSuggestions() {
        return this.suggestions;
    }

    public void setSuggestions(List<Substring> suggestions) {
        Preconditions.checkNotNull(suggestions);
        this.suggestions = suggestions;
    }
}

