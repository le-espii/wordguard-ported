/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldguard.internal.flywaydb.core.internal.util.scanner;

public interface Resource {
    public String getLocation();

    public String getLocationOnDisk();

    public String loadAsString(String var1);

    public byte[] loadAsBytes();

    public String getFilename();
}

