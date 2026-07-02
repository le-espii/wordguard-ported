/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldguard.internal.flywaydb.core.internal.util;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;

public class UrlUtils {
    private UrlUtils() {
    }

    public static String toFilePath(URL url) {
        String filePath;
        try {
            filePath = URLDecoder.decode(url.getPath(), "UTF-8");
        }
        catch (UnsupportedEncodingException e) {
            throw new IllegalStateException("Can never happen", e);
        }
        if (filePath.endsWith("/")) {
            filePath = filePath.substring(0, filePath.length() - 1);
        }
        return filePath;
    }
}

