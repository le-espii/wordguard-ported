/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit;

import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.util.formatting.text.TextComponent;
import com.sk89q.worldedit.util.formatting.text.TranslatableComponent;
import com.sk89q.worldedit.world.item.ItemType;

public class NotABlockException
extends WorldEditException {
    public NotABlockException() {
        super(TranslatableComponent.of("worldedit.error.not-a-block"));
    }

    @Deprecated
    public NotABlockException(String input) {
        super(TranslatableComponent.of("worldedit.error.not-a-block.item", TextComponent.of(input)));
    }

    @Deprecated
    public NotABlockException(int input) {
        super(TranslatableComponent.of("worldedit.error.not-a-block.item", TextComponent.of(input)));
    }

    public NotABlockException(ItemType input) {
        super(TranslatableComponent.of("worldedit.error.not-a-block.item", input.getRichName()));
    }
}

