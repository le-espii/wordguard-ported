/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.util.formatting.component;

import com.sk89q.worldedit.util.formatting.text.Component;
import com.sk89q.worldedit.util.formatting.text.TextComponent;

public class TextComponentProducer {
    private TextComponent.Builder builder = TextComponent.builder().content("");

    public TextComponent.Builder getBuilder() {
        return this.builder;
    }

    public TextComponentProducer append(Component component) {
        this.getBuilder().append(component);
        return this;
    }

    public TextComponentProducer append(String string) {
        this.getBuilder().append((Component)TextComponent.of(string));
        return this;
    }

    public TextComponentProducer newline() {
        this.getBuilder().append((Component)TextComponent.newline());
        return this;
    }

    public TextComponent create() {
        return (TextComponent)this.builder.build();
    }

    public TextComponentProducer reset() {
        this.builder = TextComponent.builder().content("");
        return this;
    }
}

