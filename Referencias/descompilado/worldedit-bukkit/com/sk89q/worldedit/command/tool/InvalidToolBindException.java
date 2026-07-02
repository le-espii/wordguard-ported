/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.command.tool;

import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.util.formatting.text.Component;
import com.sk89q.worldedit.world.item.ItemType;

public class InvalidToolBindException
extends WorldEditException {
    private final ItemType item;

    public InvalidToolBindException(ItemType item, Component msg) {
        super(msg);
        this.item = item;
    }

    @Deprecated
    public InvalidToolBindException(ItemType item, String msg) {
        super(msg);
        this.item = item;
    }

    public ItemType getItemType() {
        return this.item;
    }
}

