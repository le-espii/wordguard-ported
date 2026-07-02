/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldguard.internal.flywaydb.core.internal.util.scanner.classpath;

import java.io.IOException;
import java.net.URL;

public interface UrlResolver {
    public URL toStandardJavaUrl(URL var1) throws IOException;
}

