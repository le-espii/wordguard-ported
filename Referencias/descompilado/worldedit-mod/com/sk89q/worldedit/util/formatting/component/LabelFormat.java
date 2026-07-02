/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.util.formatting.component;

import com.sk89q.worldedit.util.formatting.component.TextComponentProducer;
import com.sk89q.worldedit.util.formatting.text.TextComponent;
import com.sk89q.worldedit.util.formatting.text.format.TextColor;

public class LabelFormat
extends TextComponentProducer {
    private LabelFormat() {
        this.getBuilder().content("").color(TextColor.YELLOW);
    }

    public static TextComponent wrap(String ... texts) {
        LabelFormat label = new LabelFormat();
        for (String component : texts) {
            label.append(component);
        }
        return label.create();
    }
}

