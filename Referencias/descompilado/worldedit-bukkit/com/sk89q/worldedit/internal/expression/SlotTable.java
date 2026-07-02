/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.internal.expression;

import com.sk89q.worldedit.internal.expression.LocalSlot;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.Set;

public class SlotTable {
    private final Map<String, LocalSlot> slots = new HashMap<String, LocalSlot>();

    public Set<String> keySet() {
        return this.slots.keySet();
    }

    public void putSlot(String name, LocalSlot slot) {
        this.slots.put(name, slot);
    }

    public boolean containsSlot(String name) {
        return this.slots.containsKey(name);
    }

    public Optional<LocalSlot.Variable> initVariable(String name) {
        this.slots.computeIfAbsent(name, n -> new LocalSlot.Variable(0.0));
        return this.getVariable(name);
    }

    public Optional<LocalSlot> getSlot(String name) {
        return Optional.ofNullable(this.slots.get(name));
    }

    public Optional<LocalSlot.Variable> getVariable(String name) {
        return this.getSlot(name).filter(LocalSlot.Variable.class::isInstance).map(LocalSlot.Variable.class::cast);
    }

    public OptionalDouble getSlotValue(String name) {
        LocalSlot slot = this.slots.get(name);
        return slot == null ? OptionalDouble.empty() : OptionalDouble.of(slot.value());
    }
}

