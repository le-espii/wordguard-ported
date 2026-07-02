/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 */
package com.sk89q.worldguard.blacklist.target;

import com.google.common.base.Preconditions;

public class TargetMatcherParseException
extends Exception {
    public TargetMatcherParseException(String message) {
        super((String)Preconditions.checkNotNull((Object)message));
    }
}

