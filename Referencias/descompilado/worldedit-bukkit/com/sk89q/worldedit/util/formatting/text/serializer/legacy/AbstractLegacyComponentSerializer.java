/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.sk89q.worldedit.util.formatting.text.serializer.legacy;

import com.sk89q.worldedit.util.formatting.text.Component;
import com.sk89q.worldedit.util.formatting.text.TextComponent;
import com.sk89q.worldedit.util.formatting.text.format.TextColor;
import com.sk89q.worldedit.util.formatting.text.format.TextDecoration;
import com.sk89q.worldedit.util.formatting.text.format.TextFormat;
import com.sk89q.worldedit.util.formatting.text.serializer.legacy.LegacyComponentSerializer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

abstract class AbstractLegacyComponentSerializer
implements LegacyComponentSerializer {
    private static final TextDecoration[] DECORATIONS = TextDecoration.values();
    private static final String LEGACY_CHARS = "0123456789abcdefklmnor";
    private static final List<TextFormat> FORMATS;

    AbstractLegacyComponentSerializer() {
    }

    private static @Nullable TextFormat formatByLegacyChar(char legacy) {
        int index = LEGACY_CHARS.indexOf(legacy);
        return index == -1 ? null : FORMATS.get(index);
    }

    private static char getLegacyChar(TextFormat legacy) {
        int index = FORMATS.indexOf(legacy);
        return LEGACY_CHARS.charAt(index);
    }

    @Override
    public @NonNull TextComponent deserialize(@NonNull String input, char character) {
        int next = input.lastIndexOf(character, input.length() - 2);
        if (next == -1) {
            return this.finish(TextComponent.of(input));
        }
        ArrayList parts = new ArrayList();
        TextComponent.Builder current = null;
        boolean reset = false;
        int pos = input.length();
        do {
            TextFormat format;
            if ((format = AbstractLegacyComponentSerializer.formatByLegacyChar(input.charAt(next + 1))) == null) continue;
            int from = next + 2;
            if (from != pos) {
                if (current != null) {
                    if (reset) {
                        parts.add(current.build());
                        reset = false;
                        current = TextComponent.builder();
                    } else {
                        current = (TextComponent.Builder)TextComponent.builder().append((Component)current.build());
                    }
                } else {
                    current = TextComponent.builder();
                }
                current.content(input.substring(from, pos));
            } else if (current == null) {
                current = TextComponent.builder();
            }
            reset |= AbstractLegacyComponentSerializer.applyFormat(current, format);
            pos = next;
        } while ((next = input.lastIndexOf(character, next - 1)) != -1);
        if (current != null) {
            parts.add(current.build());
        }
        Collections.reverse(parts);
        return this.finish((TextComponent)((TextComponent.Builder)TextComponent.builder(pos > 0 ? input.substring(0, pos) : "").append(parts)).build());
    }

    protected abstract @NonNull TextComponent finish(TextComponent var1);

    @Override
    public @NonNull String serialize(@NonNull Component component, char character) {
        Cereal state = new Cereal(character);
        state.append(component);
        return state.toString();
    }

    private static boolean applyFormat(@NonNull TextComponent.Builder builder, @NonNull TextFormat format) {
        if (format instanceof TextColor) {
            builder.colorIfAbsent((TextColor)format);
            return true;
        }
        if (format instanceof TextDecoration) {
            builder.decoration((TextDecoration)format, TextDecoration.State.TRUE);
            return false;
        }
        if (format instanceof Reset) {
            builder.colorIfAbsent(null);
            for (TextDecoration decoration : DECORATIONS) {
                builder.decoration(decoration, TextDecoration.State.NOT_SET);
            }
            return true;
        }
        throw new IllegalArgumentException(String.format("unknown format '%s'", format.getClass()));
    }

    static {
        ArrayList<Reset> formats = new ArrayList<Reset>();
        Collections.addAll(formats, TextColor.values());
        Collections.addAll(formats, DECORATIONS);
        formats.add(Reset.INSTANCE);
        FORMATS = Collections.unmodifiableList(formats);
        if (FORMATS.size() != LEGACY_CHARS.length()) {
            throw new IllegalStateException("FORMATS length differs from LEGACY_CHARS length");
        }
    }

    private static final class Cereal {
        private final StringBuilder sb = new StringBuilder();
        private final Style style = new Style();
        private final char character;

        Cereal(char character) {
            this.character = character;
        }

        void append(@NonNull Component component) {
            this.append(component, new Style());
        }

        private void append(@NonNull Component component, @NonNull Style style) {
            List<Component> children;
            String content;
            style.apply(component);
            if (component instanceof TextComponent && !(content = ((TextComponent)component).content()).isEmpty()) {
                style.applyFormat();
                this.sb.append(content);
            }
            if (!(children = component.children()).isEmpty()) {
                Style childrenStyle = new Style(style);
                for (Component child : children) {
                    this.append(child, childrenStyle);
                    childrenStyle.set(style);
                }
            }
        }

        private void append(@NonNull TextFormat format) {
            this.sb.append(this.character).append(AbstractLegacyComponentSerializer.getLegacyChar(format));
        }

        public String toString() {
            return this.sb.toString();
        }

        private final class Style {
            private @Nullable TextColor color;
            private final Set<TextDecoration> decorations;

            Style() {
                this.decorations = EnumSet.noneOf(TextDecoration.class);
            }

            Style(Style that) {
                this.color = that.color;
                this.decorations = EnumSet.copyOf(that.decorations);
            }

            void set(@NonNull Style that) {
                this.color = that.color;
                this.decorations.clear();
                this.decorations.addAll(that.decorations);
            }

            void apply(@NonNull Component component) {
                TextColor color = component.color();
                if (color != null) {
                    this.color = color;
                }
                int length = DECORATIONS.length;
                block4: for (int i = 0; i < length; ++i) {
                    TextDecoration decoration = DECORATIONS[i];
                    switch (component.decoration(decoration)) {
                        case TRUE: {
                            this.decorations.add(decoration);
                            continue block4;
                        }
                        case FALSE: {
                            this.decorations.remove(decoration);
                        }
                    }
                }
            }

            void applyFormat() {
                if (this.color != ((Cereal)Cereal.this).style.color) {
                    this.applyFullFormat();
                    return;
                }
                if (!this.decorations.containsAll(((Cereal)Cereal.this).style.decorations)) {
                    this.applyFullFormat();
                    return;
                }
                for (TextDecoration decoration : this.decorations) {
                    if (!((Cereal)Cereal.this).style.decorations.add(decoration)) continue;
                    Cereal.this.append(decoration);
                }
            }

            private void applyFullFormat() {
                if (this.color != null) {
                    Cereal.this.append(this.color);
                } else {
                    Cereal.this.append(Reset.INSTANCE);
                }
                ((Cereal)Cereal.this).style.color = this.color;
                for (TextDecoration decoration : this.decorations) {
                    Cereal.this.append(decoration);
                }
                ((Cereal)Cereal.this).style.decorations.clear();
                ((Cereal)Cereal.this).style.decorations.addAll(this.decorations);
            }
        }
    }

    private static enum Reset implements TextFormat
    {
        INSTANCE;

    }
}

