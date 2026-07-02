/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.google.common.base.Strings
 *  com.google.common.collect.Sets
 */
package com.sk89q.worldedit.util.formatting.component;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.Sets;
import com.sk89q.worldedit.util.formatting.component.TextComponentProducer;
import com.sk89q.worldedit.util.formatting.text.Component;
import com.sk89q.worldedit.util.formatting.text.TextComponent;
import com.sk89q.worldedit.util.formatting.text.format.TextColor;
import com.sk89q.worldedit.util.formatting.text.format.TextDecoration;
import java.util.Set;

public class MessageBox
extends TextComponentProducer {
    private static final int GUARANTEED_NO_WRAP_CHAT_PAGE_WIDTH = 47;
    private final TextComponentProducer contents;
    private final TextColor borderColor;

    public MessageBox(String title, TextComponentProducer contents) {
        this(title, contents, TextColor.YELLOW);
    }

    public MessageBox(String title, TextComponentProducer contents, TextColor borderColor) {
        Preconditions.checkNotNull((Object)title);
        this.borderColor = borderColor;
        this.append(this.centerAndBorder(TextComponent.of(title))).newline();
        this.contents = contents;
    }

    protected Component centerAndBorder(TextComponent text) {
        TextComponentProducer line = new TextComponentProducer();
        int leftOver = 47 - MessageBox.getLength(text);
        int side = (int)Math.floor((double)leftOver / 2.0);
        if (side > 0) {
            if (side > 1) {
                line.append(this.createBorder(side - 1));
            }
            line.append(TextComponent.space());
        }
        line.append(text);
        if (side > 0) {
            line.append(TextComponent.space());
            if (side > 1) {
                line.append(this.createBorder(side - 1));
            }
        }
        return line.create();
    }

    private static int getLength(TextComponent text) {
        return text.content().length() + text.children().stream().filter(c -> c instanceof TextComponent).mapToInt(c -> MessageBox.getLength((TextComponent)c)).sum();
    }

    private TextComponent createBorder(int count) {
        return TextComponent.of(Strings.repeat((String)"-", (int)count), this.borderColor, (Set<TextDecoration>)Sets.newHashSet((Object[])new TextDecoration[]{TextDecoration.STRIKETHROUGH}));
    }

    public TextComponentProducer getContents() {
        return this.contents;
    }

    @Override
    public TextComponent create() {
        this.append(this.contents.create());
        return super.create();
    }
}

