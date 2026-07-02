/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.util.formatting.component;

import com.sk89q.worldedit.util.formatting.component.TextComponentProducer;
import com.sk89q.worldedit.util.formatting.text.TextComponent;
import com.sk89q.worldedit.util.formatting.text.format.TextColor;

public class ErrorFormat
extends TextComponentProducer {
    private ErrorFormat() {
        this.getBuilder().content("").color(TextColor.RED);
    }

    public static TextComponent wrap(String ... texts) {
        ErrorFormat error = new ErrorFormat();
        for (String component : texts) {
            error.append(component);
        }
        return error.create();
    }
}

