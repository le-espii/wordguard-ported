/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 */
package org.enginehub.piston;

import com.google.common.collect.ImmutableList;

public interface CommandMetadata {
    public String getCalledName();

    public ImmutableList<String> getArguments();
}

