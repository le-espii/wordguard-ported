/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Splitter
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 *  com.google.common.collect.Iterables
 */
package com.sk89q.worldedit.internal.command;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.sk89q.worldedit.internal.util.Substring;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CommandArgParser {
    private final Stream.Builder<Substring> args = Stream.builder();
    private final List<Substring> input;
    private final List<Substring> currentArg = new ArrayList<Substring>();
    private int index = 0;
    private State state = State.NORMAL;

    public static CommandArgParser forArgString(String argString) {
        return new CommandArgParser((List<Substring>)CommandArgParser.spaceSplit(argString));
    }

    public static ImmutableList<Substring> spaceSplit(String string) {
        ImmutableList.Builder result = ImmutableList.builder();
        int index = 0;
        for (String part : Splitter.on((char)' ').split((CharSequence)string)) {
            result.add((Object)Substring.from(string, index, index + part.length()));
            index += part.length() + 1;
        }
        return result.build();
    }

    public CommandArgParser(List<Substring> input) {
        this.input = input;
    }

    public Stream<Substring> parseArgs() {
        while (this.index < this.input.size()) {
            Substring nextPart = this.input.get(this.index);
            switch (this.state.ordinal()) {
                case 0: {
                    this.handleNormal(nextPart);
                    break;
                }
                case 1: {
                    this.handleQuote(nextPart);
                    break;
                }
            }
            ++this.index;
        }
        if (this.currentArg.size() > 0) {
            this.finishArg();
        }
        return this.args.build();
    }

    private void handleNormal(Substring part) {
        String strPart = part.getSubstring();
        if (strPart.startsWith("\"")) {
            if (strPart.endsWith("\"") && strPart.length() > 1) {
                this.currentArg.add(Substring.wrap(strPart.substring(1, strPart.length() - 1), part.getStart() + 1, part.getEnd() - 1));
                this.finishArg();
            } else {
                this.state = State.QUOTE;
                this.currentArg.add(Substring.wrap(strPart.substring(1), part.getStart() + 1, part.getEnd()));
            }
        } else {
            this.currentArg.add(part);
            this.finishArg();
        }
    }

    private void handleQuote(Substring part) {
        if (part.getSubstring().endsWith("\"")) {
            this.state = State.NORMAL;
            this.currentArg.add(Substring.wrap(part.getSubstring().substring(0, part.getSubstring().length() - 1), part.getStart(), part.getEnd() - 1));
            this.finishArg();
        } else {
            this.currentArg.add(part);
        }
    }

    private void finishArg() {
        int start = this.currentArg.get(0).getStart();
        int end = ((Substring)Iterables.getLast(this.currentArg)).getEnd();
        this.args.add(Substring.wrap(this.currentArg.stream().map(Substring::getSubstring).collect(Collectors.joining(" ")), start, end));
        this.currentArg.clear();
    }

    private static enum State {
        NORMAL,
        QUOTE;

    }
}

