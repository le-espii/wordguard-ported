/*
 * Decompiled with CFR 0.152.
 */
package org.enginehub.linbus.format.snbt;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.UncheckedIOException;
import org.enginehub.linbus.common.IOFunction;
import org.enginehub.linbus.format.snbt.impl.LinSnbtWriter;
import org.enginehub.linbus.format.snbt.impl.reader.LinSnbtReader;
import org.enginehub.linbus.format.snbt.impl.reader.LinSnbtTokenizer;
import org.enginehub.linbus.stream.LinStream;
import org.enginehub.linbus.stream.LinStreamable;

public class LinStringIO {
    public static LinStream read(Reader input) {
        return new LinSnbtReader(new LinSnbtTokenizer(input));
    }

    public static LinStream readFromString(String input) {
        return LinStringIO.read(new StringReader(input));
    }

    public static <R> R readUsing(Reader input, IOFunction<? super LinStream, R> transform) throws IOException {
        return transform.apply(LinStringIO.read(input));
    }

    public static <R> R readFromStringUsing(String input, IOFunction<? super LinStream, R> transform) {
        try {
            return transform.apply(LinStringIO.readFromString(input));
        }
        catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static void write(Appendable output, LinStreamable tokens) throws IOException {
        new LinSnbtWriter().write(output, tokens.linStream());
    }

    public static String writeToString(LinStreamable tokens) {
        StringBuilder builder = new StringBuilder();
        try {
            LinStringIO.write(builder, tokens);
        }
        catch (IOException e) {
            throw new AssertionError("No I/O to perform, so shouldn't throw an I/O exception", e);
        }
        return builder.toString();
    }

    private LinStringIO() {
    }
}

