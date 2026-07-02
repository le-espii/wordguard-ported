/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.minecraft.util.commands;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(value=RetentionPolicy.RUNTIME)
@Deprecated
public @interface CommandPermissions {
    public String[] value();
}

