/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  javax.annotation.Nullable
 */
package org.enginehub.piston.config;

import com.google.common.collect.ImmutableList;
import com.sk89q.worldedit.util.formatting.text.Component;
import com.sk89q.worldedit.util.formatting.text.TextComponent;
import com.sk89q.worldedit.util.formatting.text.TranslatableComponent;
import com.sk89q.worldedit.util.formatting.text.format.Style;
import com.sk89q.worldedit.util.formatting.text.format.TextColor;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;
import org.enginehub.piston.config.Config;

public class ColorConfig
extends Config<TextColor> {
    private static final ColorConfig TEXT_MODIFIER = new ColorConfig("piston.style.text.modifier", TextColor.YELLOW);
    private static final ColorConfig MAIN_TEXT = new ColorConfig("piston.style.main.text", TextColor.GOLD);
    private static final ColorConfig HELP_TEXT = new ColorConfig("piston.style.help.text", TextColor.GRAY);
    private static final ColorConfig PART_WRAPPING = new ColorConfig("piston.style.part.wrapping", TextColor.YELLOW);
    private static final Set<Style.Merge> MERGE_NO_COLOR = Style.Merge.of(Style.Merge.DECORATIONS, Style.Merge.INSERTION, Style.Merge.EVENTS);

    public static ColorConfig textModifier() {
        return TEXT_MODIFIER;
    }

    public static ColorConfig mainText() {
        return MAIN_TEXT;
    }

    public static ColorConfig helpText() {
        return HELP_TEXT;
    }

    public static ColorConfig partWrapping() {
        return PART_WRAPPING;
    }

    private ColorConfig(String key, @Nullable TextColor color) {
        super(key, color);
    }

    @Override
    protected Config<TextColor> copyForDefault() {
        return new ColorConfig(this.getKey(), (TextColor)this.getValue());
    }

    public ColorConfig value(@Nullable TextColor value) {
        super.value(value);
        return this;
    }

    public Component wrap(String text) {
        return this.wrap((List<Component>)ImmutableList.of((Object)TextComponent.of(text)));
    }

    public Component wrap(Component ... args) {
        return this.wrap(Arrays.asList(args));
    }

    public Component wrap(List<Component> args) {
        return super.wrapInternal(args);
    }

    @Override
    protected Component apply(TranslatableComponent placeholder) {
        return ((TextComponent.Builder)((TextComponent.Builder)this.renderFromArgs(placeholder.args()).mergeStyle((Component)placeholder, MERGE_NO_COLOR)).append(placeholder.children())).build();
    }

    private TextComponent.Builder renderFromArgs(List<Component> args) {
        TextColor color = (TextColor)this.getValue();
        switch (args.size()) {
            case 0: {
                return TextComponent.builder("", color);
            }
            case 1: {
                Component only = args.get(0);
                if (!(only instanceof TextComponent)) break;
                return (TextComponent.Builder)((TextComponent.Builder)((TextComponent)only).toBuilder()).color(color);
            }
        }
        return (TextComponent.Builder)((TextComponent.Builder)TextComponent.builder().color(color)).append(args);
    }
}

