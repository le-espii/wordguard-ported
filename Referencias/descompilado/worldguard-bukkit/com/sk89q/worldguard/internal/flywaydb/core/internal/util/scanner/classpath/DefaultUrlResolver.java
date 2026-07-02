/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldguard.internal.flywaydb.core.internal.util.scanner.classpath;

import com.sk89q.worldguard.internal.flywaydb.core.internal.util.scanner.classpath.UrlResolver;
import java.io.IOException;
import java.net.URL;

public class DefaultUrlResolver
implements UrlResolver {
    @Override
    public URL toStandardJavaUrl(URL url) throws IOException {
        return url;
    }
}

