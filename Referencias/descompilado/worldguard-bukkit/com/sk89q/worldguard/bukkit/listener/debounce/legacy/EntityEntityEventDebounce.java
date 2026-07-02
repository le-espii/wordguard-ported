/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Entity
 *  org.bukkit.event.Cancellable
 *  org.bukkit.event.Event
 */
package com.sk89q.worldguard.bukkit.listener.debounce.legacy;

import com.sk89q.worldguard.bukkit.listener.debounce.legacy.AbstractEventDebounce;
import org.bukkit.entity.Entity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;

public class EntityEntityEventDebounce
extends AbstractEventDebounce<Key> {
    public EntityEntityEventDebounce(int debounceTime) {
        super(debounceTime);
    }

    public <T extends Event> void debounce(Entity source, Entity target, Cancellable originalEvent, T firedEvent) {
        super.debounce(new Key(source, target), originalEvent, firedEvent);
    }

    protected static class Key {
        private final Entity source;
        private final Entity target;

        public Key(Entity source, Entity target) {
            this.source = source;
            this.target = target;
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            Key key = (Key)o;
            if (!this.source.equals((Object)key.source)) {
                return false;
            }
            return this.target.equals((Object)key.target);
        }

        public int hashCode() {
            int result = this.source.hashCode();
            result = 31 * result + this.target.hashCode();
            return result;
        }
    }
}

