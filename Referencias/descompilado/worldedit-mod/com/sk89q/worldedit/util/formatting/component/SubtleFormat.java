/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.util.formatting.component;

import com.sk89q.worldedit.util.formatting.component.TextComponentProducer;
import com.sk89q.worldedit.util.formatting.text.TextComponent;
import com.sk89q.worldedit.util.formatting.text.format.TextColor;

public class SubtleFormat
extends TextComponentProducer {
    private SubtleFormat() {
        this.getBuilder().content("").color(TextColor.GRAY);
    }

    public static TextComponent wrap(String ... texts) {
        SubtleFormat subtle = new SubtleFormat();
        for (String component : texts) {
            subtle.append(component);
        }
        return subtle.create();
    }
}

