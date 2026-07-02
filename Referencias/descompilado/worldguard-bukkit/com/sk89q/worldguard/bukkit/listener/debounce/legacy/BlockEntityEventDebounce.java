/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Material
 *  org.bukkit.block.Block
 *  org.bukkit.entity.Entity
 *  org.bukkit.event.Cancellable
 *  org.bukkit.event.Event
 */
package com.sk89q.worldguard.bukkit.listener.debounce.legacy;

import com.sk89q.worldguard.bukkit.listener.debounce.legacy.AbstractEventDebounce;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;

public class BlockEntityEventDebounce
extends AbstractEventDebounce<Key> {
    public BlockEntityEventDebounce(int debounceTime) {
        super(debounceTime);
    }

    public <T extends Event> void debounce(Block block, Entity entity, Cancellable originalEvent, T firedEvent) {
        super.debounce(new Key(block, entity), originalEvent, firedEvent);
    }

    protected static class Key {
        private final Block block;
        private final Material blockMaterial;
        private final Entity entity;

        private Key(Block block, Entity entity) {
            this.block = block;
            this.blockMaterial = block.getType();
            this.entity = entity;
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            Key key = (Key)o;
            if (!this.block.equals((Object)key.block)) {
                return false;
            }
            if (this.blockMaterial != key.blockMaterial) {
                return false;
            }
            return this.entity.equals((Object)key.entity);
        }

        public int hashCode() {
            int result = this.block.hashCode();
            result = 31 * result + this.blockMaterial.hashCode();
            result = 31 * result + this.entity.hashCode();
            return result;
        }
    }
}

