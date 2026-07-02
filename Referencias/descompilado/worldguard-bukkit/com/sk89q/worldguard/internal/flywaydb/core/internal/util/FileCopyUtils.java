/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldguard.internal.flywaydb.core.internal.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;

public class FileCopyUtils {
    private FileCopyUtils() {
    }

    public static String copyToString(Reader in) throws IOException {
        StringWriter out = new StringWriter();
        FileCopyUtils.copy(in, out);
        String str = out.toString();
        if (str.startsWith("\ufeff")) {
            return str.substring(1);
        }
        return str;
    }

    public static byte[] copyToByteArray(InputStream in) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream(4096);
        FileCopyUtils.copy(in, out);
        return out.toByteArray();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static void copy(Reader in, Writer out) throws IOException {
        try {
            int bytesRead;
            char[] buffer = new char[4096];
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
            out.flush();
        }
        finally {
            try {
                in.close();
            }
            catch (IOException ex) {}
            try {
                out.close();
            }
            catch (IOException iOException) {}
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static int copy(InputStream in, OutputStream out) throws IOException {
        try {
            int bytesRead;
            int byteCount = 0;
            byte[] buffer = new byte[4096];
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
                byteCount += bytesRead;
            }
            out.flush();
            int n = byteCount;
            return n;
        }
        finally {
            try {
                in.close();
            }
            catch (IOException ex) {}
            try {
                out.close();
            }
            catch (IOException ex) {}
        }
    }
}

