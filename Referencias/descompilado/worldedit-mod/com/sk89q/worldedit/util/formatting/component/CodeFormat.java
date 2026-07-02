/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.util.formatting.component;

import com.sk89q.worldedit.util.formatting.component.TextComponentProducer;
import com.sk89q.worldedit.util.formatting.text.TextComponent;
import com.sk89q.worldedit.util.formatting.text.format.TextColor;

public class CodeFormat
extends TextComponentProducer {
    private CodeFormat() {
        this.getBuilder().content("").color(TextColor.AQUA);
    }

    public static TextComponent wrap(String ... texts) {
        CodeFormat code = new CodeFormat();
        for (String text : texts) {
            code.append(text);
        }
        return code.create();
    }
}

