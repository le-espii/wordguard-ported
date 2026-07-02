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
import com.sk89q.worldedit.util.formatting.text.event.ClickEvent;
import com.sk89q.worldedit.util.formatting.text.format.Style;
import com.sk89q.worldedit.util.formatting.text.serializer.legacy.AbstractLegacyComponentSerializer;
import com.sk89q.worldedit.util.formatting.text.serializer.legacy.LegacyComponentSerializer;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

final class LinkingLegacyComponentSerializer
extends AbstractLegacyComponentSerializer {
    static final LegacyComponentSerializer NO_STYLE = new LinkingLegacyComponentSerializer(null);
    private static final Pattern URL_PATTERN = Pattern.compile("(?:(https?)://)?([-\\w_.]+\\.\\w{2,})(/\\S*)?");
    private final @Nullable Style style;

    LinkingLegacyComponentSerializer(@Nullable Style style) {
        this.style = style;
    }

    @Override
    protected @NonNull TextComponent finish(@NonNull TextComponent original) {
        ArrayList<Component> produced = new ArrayList<Component>();
        ArrayDeque<TextComponent> queue = new ArrayDeque<TextComponent>();
        queue.add(original);
        while (!queue.isEmpty()) {
            TextComponent current = (TextComponent)queue.remove();
            String content = current.content();
            Matcher matcher = URL_PATTERN.matcher(content);
            TextComponent withoutChildren = (TextComponent)current.children(Collections.emptyList());
            if (matcher.find()) {
                int lastEnd = 0;
                do {
                    int start = matcher.start();
                    int end = matcher.end();
                    String matched = matcher.group();
                    String prefix = content.substring(lastEnd, start);
                    if (!prefix.isEmpty()) {
                        produced.add(withoutChildren.content(prefix));
                    }
                    Component link = withoutChildren.content(matched).clickEvent(ClickEvent.openUrl(matched));
                    if (this.style != null) {
                        link = link.style(link.style().merge(this.style));
                    }
                    produced.add(link);
                    lastEnd = end;
                } while (matcher.find());
                if (content.length() - lastEnd > 0) {
                    produced.add(withoutChildren.content(content.substring(lastEnd)));
                }
            } else {
                produced.add(withoutChildren);
            }
            for (Component child : current.children()) {
                queue.add((TextComponent)child);
            }
        }
        if (produced.size() == 1) {
            return (TextComponent)produced.get(0);
        }
        List<Component> children = produced.subList(1, produced.size());
        return (TextComponent)((Component)produced.get(0)).children(children);
    }
}

