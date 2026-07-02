/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.jchronic.handlers;

import com.sk89q.worldedit.jchronic.Options;
import com.sk89q.worldedit.jchronic.handlers.Handler;
import com.sk89q.worldedit.jchronic.handlers.IHandler;
import com.sk89q.worldedit.jchronic.repeaters.Repeater;
import com.sk89q.worldedit.jchronic.tags.Tag;
import com.sk89q.worldedit.jchronic.utils.Span;
import com.sk89q.worldedit.jchronic.utils.Time;
import com.sk89q.worldedit.jchronic.utils.Token;
import java.util.Calendar;
import java.util.List;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public abstract class MDHandler
implements IHandler {
    public Span handle(Repeater<?> month, Tag<Integer> day, List<Token> timeTokens, Options options) {
        month.setStart((Calendar)options.getNow().clone());
        Span span = month.thisSpan(options.getContext());
        Calendar dayStart = Time.construct(span.getBeginCalendar().get(1), span.getBeginCalendar().get(2) + 1, day.getType());
        return Handler.dayOrTime(dayStart, timeTokens, options);
    }
}

