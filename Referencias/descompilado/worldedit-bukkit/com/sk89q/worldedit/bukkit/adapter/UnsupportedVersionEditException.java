/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.bukkit.adapter;

import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.util.formatting.text.TranslatableComponent;

public class UnsupportedVersionEditException
extends WorldEditException {
    public UnsupportedVersionEditException() {
        super(TranslatableComponent.of("worldedit.bukkit.no-edit-without-adapter"));
    }
}

