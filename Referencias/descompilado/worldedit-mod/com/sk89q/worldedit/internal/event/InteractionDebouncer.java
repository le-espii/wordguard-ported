/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.internal.event;

import com.sk89q.worldedit.extension.platform.Platform;
import com.sk89q.worldedit.util.Identifiable;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class InteractionDebouncer {
    private final Platform platform;
    private final Map<UUID, Interaction> lastInteractions = new HashMap<UUID, Interaction>();

    public InteractionDebouncer(Platform platform) {
        this.platform = platform;
    }

    public void clearInteraction(Identifiable player) {
        this.lastInteractions.remove(player.getUniqueId());
    }

    public void setLastInteraction(Identifiable player, boolean result) {
        this.lastInteractions.put(player.getUniqueId(), new Interaction(this.platform.getTickCount(), result));
    }

    public Optional<Boolean> getDuplicateInteractionResult(Identifiable player) {
        Interaction last = this.lastInteractions.get(player.getUniqueId());
        if (last == null) {
            return Optional.empty();
        }
        long now = this.platform.getTickCount();
        if (now - last.tick <= 1L) {
            return Optional.of(last.result);
        }
        return Optional.empty();
    }

    private record Interaction(long tick, boolean result) {
    }
}

