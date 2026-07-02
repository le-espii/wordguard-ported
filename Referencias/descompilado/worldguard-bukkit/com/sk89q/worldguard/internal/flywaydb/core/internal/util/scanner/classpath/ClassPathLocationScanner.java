/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldguard.internal.flywaydb.core.internal.util.scanner.classpath;

import java.io.IOException;
import java.net.URL;
import java.util.Set;

public interface ClassPathLocationScanner {
    public Set<String> findResourceNames(String var1, URL var2) throws IOException;
}

